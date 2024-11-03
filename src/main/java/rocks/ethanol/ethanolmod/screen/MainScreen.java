package rocks.ethanol.ethanolmod.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class MainScreen extends Screen implements MinecraftWrapper {

    private static final String DISCORD_INVITE_URL = "https://discord.com/invite/Xx4V9V6gfC";
    private static final String GITHUB_REPOSITORY_URL = "https://github.com/EthanolMC/EthanolMod";

    private static final String DISCORD_TEXT = "Discord";
    private static final String GITHUB_TEXT = "GitHub";

    private final Screen parentScreen;

    public MainScreen(final Screen parentScreen) {
        super(Text.literal("Ethanol Mod").formatted(Formatting.UNDERLINE));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final TextRenderer textRenderer = this.textRenderer;

        this.addDrawableChild(new PressableTextWidget(
                this.width - textRenderer.getWidth(MainScreen.GITHUB_TEXT) - 4,
                this.height - textRenderer.fontHeight * 2 - 8,
                textRenderer.getWidth(MainScreen.GITHUB_TEXT),
                textRenderer.fontHeight + 2,
                Text.of(MainScreen.GITHUB_TEXT),
                ConfirmLinkScreen.opening(this, GITHUB_REPOSITORY_URL),
                textRenderer
        )).setTooltip(Tooltip.of(Text.of("View the Ethanol Mod GitHub repository.")));

        this.addDrawableChild(new PressableTextWidget(
                this.width - textRenderer.getWidth(MainScreen.DISCORD_TEXT) - 4,
                this.height - textRenderer.fontHeight - 4,
                textRenderer.getWidth(MainScreen.DISCORD_TEXT),
                textRenderer.fontHeight + 2,
                Text.of(MainScreen.DISCORD_TEXT),
                ConfirmLinkScreen.opening(this, DISCORD_INVITE_URL),
                textRenderer
        )).setTooltip(Tooltip.of(Text.of("Join the Ethanol Discord server.")));

        final int buttonWidth = 190;
        final int x = this.width / 2 - 95;
        final int offsetY = 22;
        int y = (this.height / 2 - 50) + 14;

        final ButtonWidget openConfigButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Open Configuration"
        ), (button) -> mc.setScreen(new ConfigScreen(this))).position(x, y).width(buttonWidth).build());
        openConfigButton.setTooltip(Tooltip.of(Text.of("Open the configuration screen.")));
        y += offsetY;

        final ButtonWidget openAuthKeyPairsButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Open Auth Options"
        ), (button) -> mc.setScreen(new AuthOptionsScreen(this))).position(x, y).width(buttonWidth).build());
        openAuthKeyPairsButton.setTooltip(Tooltip.of(Text.of("Open the auth options.")));

        this.addDrawableChild(
                ButtonWidget
                        .builder(ScreenTexts.BACK, (button) -> this.close())
                        .dimensions(2, this.height - 22, 70, 20)
                        .build()
        );
    }

    @Override
    public final void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        final TextRenderer textRenderer = this.textRenderer;
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public final void close() {
        this.client.setScreen(this.parentScreen);
    }

    public static ButtonWidget createButton(final Screen currentScreen) {
        final ButtonWidget.Builder buttonBuilder = ButtonWidget.builder(Text.of("E"), button -> {
            if (mc != null) {
                mc.setScreen(new MainScreen(currentScreen));
            }
        });
        switch (EthanolMod.getInstance().getConfiguration().getConfigButtonPosition()) {
            case Configuration.ConfigButtonPosition.TOP_LEFT -> buttonBuilder.position(4, 4);
            case Configuration.ConfigButtonPosition.TOP_RIGHT -> buttonBuilder.position(currentScreen.width - 24, 4);
            case Configuration.ConfigButtonPosition.BOTTOM_LEFT -> buttonBuilder.position(4, currentScreen.height - 24);
            case Configuration.ConfigButtonPosition.BOTTOM_RIGHT ->
                    buttonBuilder.position(currentScreen.width - 24, currentScreen.height - 24);
        }
        return buttonBuilder.size(20, 20).build();
    }

}
