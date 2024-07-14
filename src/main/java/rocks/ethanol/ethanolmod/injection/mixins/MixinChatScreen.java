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
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

@Mixin(value = ChatScreen.class, priority = 9969)
public abstract class MixinChatScreen implements MinecraftWrapper {

    @Unique
    private static final String ETHANOL_NOT_INSTALLED_WARNING_1 = "Ethanol is not installed on the server.";

    @Unique
    private static final String ETHANOL_NOT_INSTALLED_WARNING_2 = "Are you sure you want to send this as a message in the chat?";

    @Unique
    private static final String ETHANOL_VANISHED_WARNING_1 = "You are vanished.";

    @Unique
    private static final String ETHANOL_VANISHED_WARNING_2 = "Are you sure you want to send this message in the chat?";

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private int ethanol$realMaxLength = 0;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void setRealMaxLength(final CallbackInfo ci) {
        this.ethanol$realMaxLength = this.chatField.getMaxLength();
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    public final void displayEthanolModWarningsAndSetMaxChatInputLength(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo info) {
        if (this.mc.isIntegratedServerRunning()) {
            return;
        }
        final String text = this.chatField.getText();
        if (text.isEmpty()) return;
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        final Configuration configuration = ethanolMod.getConfiguration();
        final TextRenderer textRenderer = this.mc.textRenderer;
        final int color = 0xFF0000;
        final boolean shadow = true;
        final int x = this.chatField.getX() + 2;
        int y = this.chatField.getY() - 22;
        if (text.startsWith(configuration.getCommandPrefix())) {
            if (!ethanolMod.isInstalled()) {
                if (configuration.getDisplayCommandSendWarning()) {
                    context.drawText(textRenderer, ETHANOL_NOT_INSTALLED_WARNING_1, x, y, color, shadow);
                    y += textRenderer.fontHeight;
                    context.drawText(textRenderer, ETHANOL_NOT_INSTALLED_WARNING_2, x, y, color, shadow);
                }
                if (configuration.getInfiniteCommandInputLength()) {
                    this.chatField.setMaxLength(ethanol$realMaxLength);
                }
            } else if (configuration.getInfiniteCommandInputLength()) {
                this.chatField.setMaxLength(Integer.MAX_VALUE);
            }
        } else {
            if (ethanolMod.isVanished()) {
                if (configuration.getDisplayVanishedWarning()) {
                    context.drawText(textRenderer, ETHANOL_VANISHED_WARNING_1, x, y, color, shadow);
                    y += textRenderer.fontHeight;
                    context.drawText(textRenderer, ETHANOL_VANISHED_WARNING_2, x, y, color, shadow);
                }
            }
        }
    }

}
