package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

public class HudRenderEventHandler implements HudRenderCallback, MinecraftWrapper {

    @Override
    public void onHudRender(final DrawContext drawContext, final RenderTickCounter tickCounter) {
        if (EthanolMod.getInstance().isInstalled()) {
            final TextRenderer textRenderer = this.mc.textRenderer;
            final int bottom = drawContext.getScaledWindowHeight() - textRenderer.fontHeight;
            if (System.currentTimeMillis() - EthanolMod.getInstance().getShowStart() < 10_000L) {
                drawContext.drawText(
                        textRenderer,
                        Text.of("Ethanol detected"),
                        0,
                        bottom,
                        0xFFFFFF,
                        true
                );
            }
            if (EthanolMod.getInstance().isVanished()) {
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
