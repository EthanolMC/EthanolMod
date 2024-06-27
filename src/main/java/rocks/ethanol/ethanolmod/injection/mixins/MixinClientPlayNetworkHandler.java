package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements MinecraftWrapper {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void executeClientCommands(final String message, final CallbackInfo ci) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<CommandSource> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final String prefix = ethanolMod.getConfigManager().getCommandPrefix();
        if (message.startsWith(prefix) && this.mc.currentScreen instanceof ChatScreen) {
            final ChatHud chatHud = this.mc.inGameHud.getChatHud();
            try {
                commandDispatcher.execute(message.substring(prefix.length()), ethanolMod.getCommandSource());
            } catch (final CommandSyntaxException e) {
                chatHud.addMessage(Text.literal("[" + EthanolMod.getInstance().getName() + "] Failed to execute command: " + e.getMessage()));
            }
            chatHud.addToMessageHistory(message);
            ci.cancel();
        }
    }

}
