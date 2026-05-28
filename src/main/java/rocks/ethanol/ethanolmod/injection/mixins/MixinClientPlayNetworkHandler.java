package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPair;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundAuthDataPayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundCommandTreePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundMessagePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundSuggestionsResponsePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundVanishPayload;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthResponsePacket;
import rocks.ethanol.ethanolmod.networking.impl.shared.SharedInitPayload;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonPacketListenerImpl {

    protected MixinClientPlayNetworkHandler(final Minecraft client, final Connection connection, final CommonListenerCookie cookie) {
        super(client, connection, cookie);
    }

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void executeClientCommands(final String message, final CallbackInfo info) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<SharedSuggestionProvider> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final String prefix = ethanolMod.getConfiguration().getCommandPrefix();
        if (message.startsWith(prefix) && this.minecraft.screen instanceof ChatScreen) {
            final ChatComponent chatHud = this.minecraft.gui.getChat();
            try {
                commandDispatcher.execute(message.substring(prefix.length()), ethanolMod.getCommandSource());
            } catch (final CommandSyntaxException exception) {
                chatHud.addClientSystemMessage(Component.literal("[".concat(EthanolMod.NAME).concat("] Failed to execute command: ").concat(exception.getMessage())));
            }
            chatHud.addRecentChat(message);
            info.cancel();
        }
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(final CustomPacketPayload payload, final CallbackInfo info) {
        switch (payload) {
            case final ClientboundCommandTreePayload commandTreePayload -> EthanolMod.getInstance().updateCommandDispatcher(new CommandDispatcher<>(commandTreePayload.getRoot()));

            case final ClientboundMessagePayload messagePayload -> this.minecraft.gui.getChat().addClientSystemMessage(Component.literal(messagePayload.getMessage()));

            case final ClientboundSuggestionsResponsePayload suggestionsResponsePayload -> {
                final Map<Long, CompletableFuture<Suggestions>> pendingRequests = EthanolMod.getInstance().getPendingRequests();
                final long nonce = suggestionsResponsePayload.getNonce();
                final CompletableFuture<Suggestions> future = pendingRequests.get(nonce);
                if (future != null) {
                    pendingRequests.remove(nonce);
                    future.complete(suggestionsResponsePayload.getSuggestions());
                }
            }

            case final ClientboundVanishPayload vanishPayload -> EthanolMod.getInstance().setVanished(vanishPayload.isVanished());

            case final SharedInitPayload ignored -> {
                final ClientPacketListener self = (ClientPacketListener) (Object) this;
                if (EthanolMod.getInstance().isAuthEnabled() && EthanolMod.getInstance().getAuthOptions().getMode() == AuthOptions.Mode.SEMI_AUTOMATIC && self.getServerData() != null) {
                    EthanolMod.getInstance().getAuthOptions().getKnownHosts().add(self.getServerData().ip);
                }

                EthanolMod.getInstance().setInstalled(true);
                EthanolMod.getInstance().setSend(false);
                EthanolMod.getInstance().setShowStart(System.currentTimeMillis());
            }

            case final ClientboundAuthDataPayload authDataPayload -> {
                final AuthKeyPair authKeyPair = EthanolMod.getInstance().getAuthKeyPairs().getByHash(authDataPayload.getPublicKeyHash());
                if (authKeyPair == null) {
                    return;
                }

                EthanolMod.LOGGER.info("Authenticating with '{}'.", authKeyPair.name());
                EthanolMod.getInstance().setAuthEnabled(true);
                try {
                    final Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, authKeyPair.keyPair().getPrivate());
                    final byte[] verifyToken = cipher.doFinal(authDataPayload.getEncryptedVerifyToken());
                    this.send(new ServerboundCustomPayloadPacket(new ServerboundAuthResponsePacket(verifyToken)));
                } catch (final NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                               IllegalBlockSizeException | BadPaddingException exception) {
                    throw new RuntimeException(exception);
                }
            }

            default -> {
                return;
            }
        }
        info.cancel();
    }

}
