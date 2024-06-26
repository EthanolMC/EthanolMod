package rocks.ethanol.ethanolmod.injection.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.config.ConfigManager;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen implements MinecraftWrapper {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private static final String ETHANOL_NOT_INSTALLED_WARNING_1 = "Ethanol is not installed on the server.";

    @Unique
    private static final String ETHANOL_NOT_INSTALLED_WARNING_2 = "Are you sure you want to send this as a message in the chat?";

    @Unique
    private static final String ETHANOL_VANISHED_WARNING_1 = "You are vanished.";

    @Unique
    private static final String ETHANOL_VANISHED_WARNING_2 = "Are you sure you want to send this message in the chat?";

    @Inject(method = "render", at = @At(value = "RETURN"))
    public void displayEthanolModWarnings(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        final String text = this.chatField.getText();
        final TextRenderer textRenderer = this.mc.textRenderer;
        final int x = this.chatField.getX() + 2;
        int y = this.chatField.getY() - 22;
        final int color = 0xFF0000;
        final boolean shadow = true;
        if (text.isEmpty()) return;
        final ConfigManager configManager = EthanolMod.getInstance().getConfigManager();
        if (text.startsWith(configManager.getCommandPrefix())) {
            if (!EthanolMod.getInstance().isInstalled()) {
                if (configManager.getDisplayCommandSendWarning()) {
                    context.drawText(textRenderer, ETHANOL_NOT_INSTALLED_WARNING_1, x, y, color, shadow);
                    y += textRenderer.fontHeight;
                    context.drawText(textRenderer, ETHANOL_NOT_INSTALLED_WARNING_2, x, y, color, shadow);
                }
            }
        } else if (EthanolMod.getInstance().isVanished()) {
            if (configManager.getDisplayVanishedWarning()) {
                context.drawText(textRenderer, ETHANOL_VANISHED_WARNING_1, x, y, color, shadow);
                y += textRenderer.fontHeight;
                context.drawText(textRenderer, ETHANOL_VANISHED_WARNING_2, x, y, color, shadow);
            }
        }
    }

}
