package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
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
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements MinecraftWrapper {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void executeClientCommands(final String message, final CallbackInfo info) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<CommandSource> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final String prefix = ethanolMod.getConfiguration().getCommandPrefix();
        if (message.startsWith(prefix) && this.mc.currentScreen instanceof ChatScreen) {
            final ChatHud chatHud = this.mc.inGameHud.getChatHud();
            try {
                commandDispatcher.execute(message.substring(prefix.length()), ethanolMod.getCommandSource());
            } catch (final CommandSyntaxException exception) {
                chatHud.addMessage(Text.literal("[".concat(EthanolMod.NAME).concat("] Failed to execute command: ").concat(exception.getMessage())));
            }
            chatHud.addToMessageHistory(message);
            info.cancel();
        }
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(final CustomPayload payload, final CallbackInfo info) {
        switch (payload) {
            case final ClientboundCommandTreePayload commandTreePayload -> EthanolMod.getInstance().updateCommandDispatcher(new CommandDispatcher<>(commandTreePayload.getRoot()));

            case final ClientboundMessagePayload messagePayload -> this.mc.inGameHud.getChatHud().addMessage(Text.of(messagePayload.getMessage()));

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
                if (EthanolMod.getInstance().isAuthEnabled() && EthanolMod.getInstance().getAuthOptions().getMode() == AuthOptions.Mode.SEMI_AUTOMATIC) {
                    EthanolMod.getInstance().getAuthOptions().getKnownHosts().add(((ClientPlayNetworkHandler) ((Object) this)).getServerInfo().address);
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
                    ((ClientPlayNetworkHandler) ((Object) this)).sendPacket(new CustomPayloadC2SPacket(new ServerboundAuthResponsePacket(verifyToken)));
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
