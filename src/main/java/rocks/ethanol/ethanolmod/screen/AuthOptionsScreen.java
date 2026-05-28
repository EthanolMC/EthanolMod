package rocks.ethanol.ethanolmod.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.util.Util;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPair;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthInitPacket;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class AuthOptionsScreen extends Screen implements MinecraftWrapper {

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 220;
    private static final int CHILD_WIDTH = 220;
    private static final int CHILD_HEIGHT = 20;
    private static final int ROW_SPACING = 6;

    private final Screen parentScreen;
    private Button tryAuthenticateButton;
    private Button resetKnownHostsButton;
    private int tryAuthenticateButtonDisabledTicks;
    private int panelX;
    private int panelY;

    public AuthOptionsScreen(final Screen parentScreen) {
        super(Component.literal("Auth Options"));
        this.parentScreen = parentScreen;
        this.tryAuthenticateButtonDisabledTicks = 0;
    }

    @Override
    protected final void init() {
        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - PANEL_HEIGHT) / 2;

        final int contentX = this.panelX + (PANEL_WIDTH - CHILD_WIDTH) / 2;
        int y = this.panelY + 90;

        Button openDir = Button.builder(
                Component.literal("Open Key Pairs Directory"),
                button -> Util.getPlatform().openPath(EthanolMod.getInstance().getAuthKeyPairs().getDirectory())
        ).bounds(contentX, y, CHILD_WIDTH, CHILD_HEIGHT).build();
        openDir.setTooltip(Tooltip.create(Component.literal("Open the auth key pairs directory.")));
        this.addRenderableWidget(openDir);
        y += CHILD_HEIGHT + ROW_SPACING;

        this.tryAuthenticateButton = Button.builder(
                Component.literal("Try Authenticate"),
                button -> {
                    if (mc.getConnection() != null && !EthanolMod.getInstance().isInstalled()) {
                        mc.getConnection().send(new ServerboundCustomPayloadPacket(new ServerboundAuthInitPacket(
                                EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().stream()
                                        .map(AuthKeyPair::hash).toArray(byte[][]::new)
                        )));
                        this.tryAuthenticateButtonDisabledTicks = 20;
                        button.active = false;
                    }
                }
        ).bounds(contentX, y, CHILD_WIDTH, CHILD_HEIGHT).build();
        this.tryAuthenticateButton.setTooltip(Tooltip.create(Component.literal("Tries to authenticate you on the current server.")));
        this.tryAuthenticateButton.active = shouldTryAuthenticateButtonBeActive();
        this.addRenderableWidget(this.tryAuthenticateButton);
        y += CHILD_HEIGHT + ROW_SPACING;

        Button modeButton = Button.builder(
                Component.empty(),
                button -> {
                    final AuthOptions options = EthanolMod.getInstance().getAuthOptions();
                    final AuthOptions.Mode current = options.getMode();
                    final AuthOptions.Mode[] values = AuthOptions.Mode.values();
                    final AuthOptions.Mode next = values[(current.ordinal() + 1) % values.length];
                    options.setMode(next);
                    syncModeButton(button, next);
                }
        ).bounds(contentX, y, CHILD_WIDTH, CHILD_HEIGHT).build();
        syncModeButton(modeButton, EthanolMod.getInstance().getAuthOptions().getMode());
        this.addRenderableWidget(modeButton);
        y += CHILD_HEIGHT + ROW_SPACING;

        this.resetKnownHostsButton = Button.builder(
                Component.literal("Reset Known Hosts"),
                button -> {
                    EthanolMod.getInstance().getAuthOptions().getKnownHosts().clear();
                    syncResetKnownHostsButton(button);
                }
        ).bounds(contentX, y, CHILD_WIDTH, CHILD_HEIGHT).build();
        this.resetKnownHostsButton.setTooltip(Tooltip.create(Component.literal("Reset known hosts used for semi automatic mode.")));
        syncResetKnownHostsButton(this.resetKnownHostsButton);
        this.addRenderableWidget(this.resetKnownHostsButton);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose())
                .bounds(2, this.height - 22, 70, 20)
                .build());
    }

    private static void syncResetKnownHostsButton(final Button widget) {
        widget.active = !EthanolMod.getInstance().getAuthOptions().getKnownHosts().isEmpty();
    }

    private static void syncModeButton(final Button widget, final AuthOptions.Mode mode) {
        widget.setMessage(Component.literal("Mode: " + mode.getDisplayName()));
        widget.setTooltip(Tooltip.create(Component.literal(mode.getDescription())));
    }

    @Override
    public final void tick() {
        syncResetKnownHostsButton(this.resetKnownHostsButton);

        if (this.tryAuthenticateButtonDisabledTicks > 0) {
            this.tryAuthenticateButtonDisabledTicks--;
        } else {
            this.tryAuthenticateButton.active = shouldTryAuthenticateButtonBeActive();
        }
    }

    private static boolean shouldTryAuthenticateButtonBeActive() {
        return mc.getConnection() != null && !EthanolMod.getInstance().isInstalled();
    }

    @Override
    public final void extractRenderState(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float delta) {
        Theme.drawBackdrop(context, this.width, this.height);
        Theme.drawPanel(context, this.panelX, this.panelY, PANEL_WIDTH, PANEL_HEIGHT);
        context.fill(this.panelX, this.panelY, this.panelX + PANEL_WIDTH, this.panelY + 2, Theme.ACCENT);

        super.extractRenderState(context, mouseX, mouseY, delta);

        final Font textRenderer = this.font;
        final int centerX = this.panelX + PANEL_WIDTH / 2;

        context.text(textRenderer, this.title, centerX - textRenderer.width(this.title) / 2, this.panelY + 14, Theme.TEXT, false);
        context.text(textRenderer, "Manage authentication for Ethanol servers",
                centerX - textRenderer.width("Manage authentication for Ethanol servers") / 2,
                this.panelY + 14 + textRenderer.lineHeight + 2, Theme.TEXT_MUTED, false);

        final int cardX = this.panelX + (PANEL_WIDTH - CHILD_WIDTH) / 2;
        final int cardY = this.panelY + 50;
        final int cardHeight = 30;
        Theme.drawCard(context, cardX, cardY, CHILD_WIDTH, cardHeight);
        Theme.drawAccentBar(context, cardX, cardY, cardHeight);

        final int keyPairs = EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().size();
        final int knownHosts = EthanolMod.getInstance().getAuthOptions().getKnownHosts().size();

        context.text(textRenderer, "Key Pairs", cardX + 8, cardY + 4, Theme.TEXT_MUTED, false);
        context.text(textRenderer, String.valueOf(keyPairs), cardX + 8, cardY + 4 + textRenderer.lineHeight + 2, Theme.ACCENT, false);

        final int dividerX = cardX + CHILD_WIDTH / 2;
        context.fill(dividerX, cardY + 4, dividerX + 1, cardY + cardHeight - 4, Theme.PANEL_BORDER);

        context.text(textRenderer, "Known Hosts", dividerX + 8, cardY + 4, Theme.TEXT_MUTED, false);
        context.text(textRenderer, String.valueOf(knownHosts), dividerX + 8, cardY + 4 + textRenderer.lineHeight + 2, Theme.ACCENT, false);
    }

    @Override
    public final void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }

}
