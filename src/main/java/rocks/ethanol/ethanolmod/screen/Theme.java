package rocks.ethanol.ethanolmod.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class Theme {

    public static final int ACCENT = 0xFF38BDF8;
    public static final int ACCENT_DIM = 0xFF0F3D52;
    public static final int DANGER = 0xFFF87171;
    public static final int SUCCESS = 0xFF4ADE80;
    public static final int WARNING = 0xFFFBBF24;

    public static final int TEXT = 0xFFE5E7EB;
    public static final int TEXT_MUTED = 0xFF94A3B8;
    public static final int TEXT_DIM = 0xFF6B7280;

    public static final int PANEL_BG = 0xC0101218;
    public static final int PANEL_BG_LIGHT = 0x80161A22;
    public static final int PANEL_BORDER = 0xFF1F2733;
    public static final int DIVIDER = 0x40FFFFFF;

    public static final int OVERLAY_TOP = 0xC8060810;
    public static final int OVERLAY_BOTTOM = 0xE0060810;

    private Theme() {}

    public static void drawBackdrop(final GuiGraphicsExtractor context, final int width, final int height) {
        context.fillGradient(0, 0, width, height, OVERLAY_TOP, OVERLAY_BOTTOM);
    }

    public static void drawPanel(final GuiGraphicsExtractor context, final int x, final int y, final int width, final int height) {
        context.fill(x, y, x + width, y + height, PANEL_BG);
        drawBorder(context, x, y, width, height, PANEL_BORDER);
    }

    public static void drawCard(final GuiGraphicsExtractor context, final int x, final int y, final int width, final int height) {
        context.fill(x, y, x + width, y + height, PANEL_BG_LIGHT);
        drawBorder(context, x, y, width, height, PANEL_BORDER);
    }

    public static void drawAccentBar(final GuiGraphicsExtractor context, final int x, final int y, final int height) {
        context.fill(x, y, x + 2, y + height, ACCENT);
    }

    public static void drawBorder(final GuiGraphicsExtractor context, final int x, final int y, final int width, final int height, final int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawDivider(final GuiGraphicsExtractor context, final int x, final int y, final int width) {
        context.fill(x, y, x + width, y + 1, DIVIDER);
    }

    public static void drawSectionHeader(final GuiGraphicsExtractor context, final Font textRenderer, final String label, final int x, final int y) {
        final String upper = label.toUpperCase();
        context.text(textRenderer, Component.literal(upper).withStyle(ChatFormatting.BOLD), x, y, ACCENT, false);
        final int width = textRenderer.width(upper);
        context.fill(x, y + textRenderer.lineHeight + 1, x + width, y + textRenderer.lineHeight + 2, ACCENT);
    }

    public static void drawHeader(final GuiGraphicsExtractor context, final Font textRenderer, final Component title, final int centerX, final int y) {
        final int titleWidth = textRenderer.width(title);
        context.text(textRenderer, title, centerX - titleWidth / 2, y, TEXT, true);
        context.fill(centerX - titleWidth / 2 - 4, y + textRenderer.lineHeight + 3, centerX + titleWidth / 2 + 4, y + textRenderer.lineHeight + 4, ACCENT);
    }

    public static void drawBadge(final GuiGraphicsExtractor context, final Font textRenderer, final String label, final int x, final int y, final int color) {
        final int width = textRenderer.width(label) + 8;
        final int height = textRenderer.lineHeight + 4;
        final int bgColor = (color & 0x00FFFFFF) | 0x40000000;
        context.fill(x, y, x + width, y + height, bgColor);
        context.fill(x, y, x + 2, y + height, color);
        context.text(textRenderer, label, x + 5, y + 3, color, false);
    }

    public static int withAlpha(final int color, final float alpha) {
        final int a = (int) (Math.max(0f, Math.min(1f, alpha)) * 255f);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    public static float easeOutCubic(final float t) {
        final float clamped = Math.max(0f, Math.min(1f, t));
        final float inverted = 1f - clamped;
        return 1f - inverted * inverted * inverted;
    }

}
