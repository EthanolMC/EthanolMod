package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class HudRenderEventHandler implements HudRenderCallback, MinecraftWrapper {

    @Override
    public final void onHudRender(final DrawContext drawContext, final RenderTickCounter tickCounter) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        if (ethanolMod.isInstalled()) {
            final TextRenderer textRenderer = this.mc.textRenderer;
            final int bottom = drawContext.getScaledWindowHeight() - textRenderer.fontHeight;
            if (System.currentTimeMillis() - ethanolMod.getShowStart() < 10_000L) { // TODO: Ethanol Detection Notification Display Duration
                drawContext.drawText(
                        textRenderer,
                        Text.of(ethanolMod.isAuthEnabled() ? "Ethanol detected (authenticated)" : "Ethanol detected"),
                        0,
                        bottom,
                        0xFFFFFF,
                        true
                );
            }
            if (ethanolMod.isVanished()) {
                final Text text = Text.of("Vanished");
                drawContext.drawText(
                        textRenderer,
                        text,
                        drawContext.getScaledWindowWidth() - textRenderer.getWidth(text),
                        bottom,
                        0xFF0000,
                        true
                );
            }
        }
    }

}
