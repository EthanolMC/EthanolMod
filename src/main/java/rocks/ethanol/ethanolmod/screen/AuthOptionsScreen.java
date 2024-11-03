package rocks.ethanol.ethanolmod.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPair;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthInitPacket;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class AuthOptionsScreen extends Screen implements MinecraftWrapper {

    private final Screen parentScreen;
    private ButtonWidget tryAuthenticateButton;
    private ButtonWidget resetKnownHostsButton;
    private int tryAuthenticateButtonDisabledTicks;

    public AuthOptionsScreen(final Screen parentScreen) {
        super(Text.literal("Ethanol Auth Options").formatted(Formatting.UNDERLINE));
        this.parentScreen = parentScreen;
        this.tryAuthenticateButtonDisabledTicks = 0;
    }

    @Override
    protected final void init() {
        final int buttonWidth = 190;
        final int x = this.width / 2 - 95;
        final int offsetY = 22;
        int y = (this.height / 2 - 50) + 14;

        final ButtonWidget openConfigButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Open Auth Key Pairs Directory"
        ), (button) -> Util.getOperatingSystem().open(EthanolMod.getInstance().getAuthKeyPairs().getDirectory())).position(x, y).width(buttonWidth).build());
        openConfigButton.setTooltip(Tooltip.of(Text.of("Open the auth key pairs directory.")));
        y += offsetY;

        this.tryAuthenticateButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Try Authenticate"
        ), (button) -> {
            if (mc.getNetworkHandler() != null && !EthanolMod.getInstance().isInstalled()) {
                mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new ServerboundAuthInitPacket(EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().stream().map(AuthKeyPair::hash).toArray(byte[][]::new))));
                this.tryAuthenticateButtonDisabledTicks = 20;
                button.active = false;
            }
        }).position(x, y).width(buttonWidth).build());
        this.tryAuthenticateButton.setTooltip(Tooltip.of(Text.of("Tries to authenticate you on the current server.")));
        this.tryAuthenticateButton.active = AuthOptionsScreen.shouldTryAuthenticateButtonBeActive();
        y += offsetY;

        final ButtonWidget modeButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                ""
        ), (button) -> {
            final AuthOptions options = EthanolMod.getInstance().getAuthOptions();
            AuthOptions.Mode mode = options.getMode();
            final AuthOptions.Mode[] values = AuthOptions.Mode.values();
            final int nextIndex = mode.ordinal() + 1;
            if (nextIndex < values.length) {
                options.setMode(mode = values[nextIndex]);
            } else {
                options.setMode(mode = values[0]);
            }
            AuthOptionsScreen.syncModeButton(button, mode);
        }).position(x, y).width(buttonWidth).build());
        AuthOptionsScreen.syncModeButton(modeButton, EthanolMod.getInstance().getAuthOptions().getMode());
        y += offsetY;

        this.resetKnownHostsButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Reset Known Hosts"
        ), (button) -> {
            EthanolMod.getInstance().getAuthOptions().getKnownHosts().clear();
            AuthOptionsScreen.syncResetKnownHostsButton(button);
        }).position(x, y).width(buttonWidth).build());
        this.resetKnownHostsButton.setTooltip(Tooltip.of(Text.of("Reset known hosts used for semi automatic mode.")));
        AuthOptionsScreen.syncResetKnownHostsButton(this.resetKnownHostsButton);

        this.addDrawableChild(
                ButtonWidget
                        .builder(ScreenTexts.BACK, (button) -> this.close())
                        .dimensions(2, this.height - 22, 70, 20)
                        .build()
        );
    }

    private static void syncResetKnownHostsButton(final ButtonWidget widget) {
        widget.active = !EthanolMod.getInstance().getAuthOptions().getKnownHosts().isEmpty();
    }

    private static void syncModeButton(final ButtonWidget widget, final AuthOptions.Mode mode) {
        widget.setMessage(Text.of("Mode: ".concat(mode.getDisplayName())));
        widget.setTooltip(Tooltip.of(Text.of(mode.getDescription())));
    }

    @Override
    public final void tick() {
        AuthOptionsScreen.syncResetKnownHostsButton(this.resetKnownHostsButton);

        if (this.tryAuthenticateButtonDisabledTicks > 0) {
            this.tryAuthenticateButtonDisabledTicks--;
        } else {
            this.tryAuthenticateButton.active = AuthOptionsScreen.shouldTryAuthenticateButtonBeActive();
        }
    }

    private static boolean shouldTryAuthenticateButtonBeActive() {
        return mc.getNetworkHandler() != null && !EthanolMod.getInstance().isInstalled();
    }

    @Override
    public final void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        final TextRenderer textRenderer = this.textRenderer;
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 20, 16777215);

        context.drawTextWithShadow(
                textRenderer,
                "Loaded Auth Key Pairs: ".concat(String.valueOf(EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().size())),
                4,
                4,
                0xFFFFFF
        );

        context.drawTextWithShadow(
                textRenderer,
                "Known Hosts: ".concat(String.valueOf(EthanolMod.getInstance().getAuthOptions().getKnownHosts().size())),
                4,
                4 + textRenderer.fontHeight + 4,
                0xFFFFFF
        );
    }

    @Override
    public final void close() {
        this.client.setScreen(this.parentScreen);
    }

}
