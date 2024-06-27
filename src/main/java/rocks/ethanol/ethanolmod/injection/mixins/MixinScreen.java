package rocks.ethanol.ethanolmod.injection.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

@Mixin(value = Screen.class, priority = 9999)
public abstract class MixinScreen implements MinecraftWrapper {

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringHelper;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1), cancellable = true)
    private void executeClientCommands(final Style style, final CallbackInfoReturnable<Boolean> cir) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final CommandDispatcher<CommandSource> commandDispatcher = ethanolMod.getCommandDispatcher();
        if (commandDispatcher == null) return;
        final ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null) {
            if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                final String value = clickEvent.getValue(), secret = ethanolMod.getCommandSecret();
                if (value.startsWith(secret) && this.mc.currentScreen instanceof ChatScreen) {
                    try {
                        commandDispatcher.execute(value.replaceFirst(secret, ""), ethanolMod.getCommandSource());
                        cir.setReturnValue(true);
                    } catch (final CommandSyntaxException e) {
                        this.mc.inGameHud.getChatHud().addMessage(Text.literal("[" + EthanolMod.getInstance().getName() + "] Failed to execute command: " + e.getMessage()));
                    }
                }
            }
        }
    }

}