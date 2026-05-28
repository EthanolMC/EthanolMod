package rocks.ethanol.ethanolmod.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class MainScreen extends Screen implements MinecraftWrapper {

    private static final String DISCORD_INVITE_URL = "https://discord.com/invite/Xx4V9V6gfC";
    private static final String GITHUB_REPOSITORY_URL = "https://github.com/EthanolMC/EthanolMod";

    private static final int PANEL_WIDTH = 260;
    private static final int PANEL_HEIGHT = 180;
    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parentScreen;

    public MainScreen(final Screen parentScreen) {
        super(Component.literal("Ethanol"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final Font textRenderer = this.font;

        final int panelX = (this.width - PANEL_WIDTH) / 2;
        final int panelY = (this.height - PANEL_HEIGHT) / 2;
        final int buttonX = (this.width - BUTTON_WIDTH) / 2;

        int y = panelY + 70;

        Button configButton = Button.builder(Component.literal("Configuration"), button -> mc.setScreen(new ConfigScreen(this)))
                .bounds(buttonX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        configButton.setTooltip(Tooltip.create(Component.literal("Open the configuration screen.")));
        this.addRenderableWidget(configButton);
        y += BUTTON_HEIGHT + 6;

        Button authButton = Button.builder(Component.literal("Auth Options"), button -> mc.setScreen(new AuthOptionsScreen(this)))
                .bounds(buttonX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        authButton.setTooltip(Tooltip.create(Component.literal("Open the auth options.")));
        this.addRenderableWidget(authButton);
        y += BUTTON_HEIGHT + 16;

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose())
                .bounds(buttonX, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        final int linkSpacing = 10;
        final int discordWidth = textRenderer.width("Discord");
        final int githubWidth = textRenderer.width("GitHub");
        final int totalWidth = discordWidth + githubWidth + linkSpacing;
        final int linksX = (this.width - totalWidth) / 2;
        final int linksY = this.height - textRenderer.lineHeight - 6;

        PlainTextButton discord = new PlainTextButton(
                linksX, linksY,
                discordWidth, textRenderer.lineHeight + 2,
                Component.literal("Discord"),
                button -> ConfirmLinkScreen.confirmLinkNow(this, DISCORD_INVITE_URL),
                textRenderer
        );
        discord.setTooltip(Tooltip.create(Component.literal("Join the Ethanol Discord server.")));
        this.addRenderableWidget(discord);

        PlainTextButton github = new PlainTextButton(
                linksX + discordWidth + linkSpacing, linksY,
                githubWidth, textRenderer.lineHeight + 2,
                Component.literal("GitHub"),
                button -> ConfirmLinkScreen.confirmLinkNow(this, GITHUB_REPOSITORY_URL),
                textRenderer
        );
        github.setTooltip(Tooltip.create(Component.literal("View the Ethanol Mod GitHub repository.")));
        this.addRenderableWidget(github);
    }

    @Override
    public final void extractRenderState(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float delta) {
        Theme.drawBackdrop(context, this.width, this.height);

        final int panelX = (this.width - PANEL_WIDTH) / 2;
        final int panelY = (this.height - PANEL_HEIGHT) / 2;

        Theme.drawPanel(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, Theme.ACCENT);

        super.extractRenderState(context, mouseX, mouseY, delta);

        final Font textRenderer = this.font;
        final int centerX = this.width / 2;

        context.text(textRenderer, "ETHANOL", centerX - textRenderer.width("ETHANOL") / 2, panelY + 18, Theme.TEXT, false);
        context.text(textRenderer, "Plugin client implementation", centerX - textRenderer.width("Plugin client implementation") / 2, panelY + 32, Theme.TEXT_MUTED, false);

        final String versionLabel = "v" + getModVersion();
        final int badgeWidth = textRenderer.width(versionLabel) + 8;
        Theme.drawBadge(context, textRenderer, versionLabel, centerX - badgeWidth / 2, panelY + 48, Theme.ACCENT);
    }

    private static String getModVersion() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer(EthanolMod.ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("?");
    }

    @Override
    public final void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }

    public static Button createButton(final Screen currentScreen) {
        final Button.Builder buttonBuilder = Button.builder(Component.literal("E"), button -> {
            if (mc != null) {
                mc.setScreen(new MainScreen(currentScreen));
            }
        });
        switch (EthanolMod.getInstance().getConfiguration().getConfigButtonPosition()) {
            case TOP_LEFT -> buttonBuilder.bounds(4, 4, 20, 20);
            case TOP_RIGHT -> buttonBuilder.bounds(currentScreen.width - 24, 4, 20, 20);
            case BOTTOM_LEFT -> buttonBuilder.bounds(4, currentScreen.height - 24, 20, 20);
            case BOTTOM_RIGHT -> buttonBuilder.bounds(currentScreen.width - 24, currentScreen.height - 24, 20, 20);
        }
        return buttonBuilder.build();
    }

}
