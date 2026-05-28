package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.screen.Theme;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class HudRenderEventHandler implements HudElement, MinecraftWrapper {

    private static final long FADE_MS = 350L;
    private static final int PADDING_X = 8;
    private static final int PADDING_Y = 4;
    private static final int MARGIN = 6;

    @Override
    public final void extractRenderState(final GuiGraphicsExtractor drawContext, final DeltaTracker tickCounter) {
        if (mc.hasSingleplayerServer()) {
            return;
        }

        final EthanolMod ethanolMod = EthanolMod.getInstance();
        if (!ethanolMod.isInstalled()) {
            return;
        }

        final Font textRenderer = mc.font;
        final long now = System.currentTimeMillis();
        final long elapsed = now - ethanolMod.getShowStart();
        final long duration = ethanolMod.getConfiguration().getDetectionNotificationDisplayDuration();

        if (elapsed < duration) {
            final float alpha = computeFade(elapsed, duration);
            renderDetectionToast(drawContext, textRenderer, ethanolMod.isAuthEnabled(), alpha);
        }

        if (ethanolMod.isVanished()) {
            renderVanishedBadge(drawContext, textRenderer);
        }
    }

    private static float computeFade(final long elapsed, final long duration) {
        if (elapsed < FADE_MS) {
            return Theme.easeOutCubic(elapsed / (float) FADE_MS);
        }
        final long fadeOutStart = duration - FADE_MS;
        if (elapsed > fadeOutStart) {
            return Theme.easeOutCubic((duration - elapsed) / (float) FADE_MS);
        }
        return 1f;
    }

    private static void renderDetectionToast(final GuiGraphicsExtractor context, final Font textRenderer, final boolean authenticated, final float alpha) {
        if (alpha <= 0.01f) {
            return;
        }

        final String label = "Ethanol detected";
        final String status = authenticated ? "AUTHENTICATED" : "READY";
        final int statusColor = authenticated ? Theme.SUCCESS : Theme.ACCENT;

        final int textWidth = textRenderer.width(label);
        final int statusWidth = textRenderer.width(status);
        final int innerWidth = textWidth + 6 + statusWidth;
        final int width = innerWidth + PADDING_X * 2 + 4;
        final int height = textRenderer.lineHeight + PADDING_Y * 2;

        final int slideOffset = (int) ((1f - alpha) * 12f);
        final int x = MARGIN - slideOffset;
        final int y = context.guiHeight() - height - MARGIN;

        final int bgColor = Theme.withAlpha(Theme.PANEL_BG, alpha * 0.85f);
        final int borderColor = Theme.withAlpha(Theme.PANEL_BORDER, alpha);
        final int barColor = Theme.withAlpha(statusColor, alpha);
        final int textColor = Theme.withAlpha(Theme.TEXT, alpha);
        final int statusTextColor = Theme.withAlpha(statusColor, alpha);

        context.fill(x, y, x + width, y + height, bgColor);
        Theme.drawBorder(context, x, y, width, height, borderColor);
        context.fill(x, y, x + 2, y + height, barColor);

        final int textY = y + PADDING_Y;
        context.text(textRenderer, label, x + PADDING_X + 2, textY, textColor, false);
        context.text(textRenderer, status, x + PADDING_X + 2 + textWidth + 6, textY, statusTextColor, false);
    }

    private static void renderVanishedBadge(final GuiGraphicsExtractor context, final Font textRenderer) {
        final String label = "VANISHED";
        final int width = textRenderer.width(label) + PADDING_X * 2 + 4;
        final int height = textRenderer.lineHeight + PADDING_Y * 2;

        final int x = context.guiWidth() - width - MARGIN;
        final int y = context.guiHeight() - height - MARGIN;

        context.fill(x, y, x + width, y + height, Theme.withAlpha(Theme.PANEL_BG, 0.85f));
        Theme.drawBorder(context, x, y, width, height, Theme.PANEL_BORDER);
        context.fill(x + width - 2, y, x + width, y + height, Theme.DANGER);

        context.text(textRenderer, label, x + PADDING_X, y + PADDING_Y, Theme.DANGER, false);
    }

}
