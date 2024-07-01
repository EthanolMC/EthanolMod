package rocks.ethanol.ethanolmod.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private static final String DISCORD_INVITE_URL = "https://discord.com/invite/Xx4V9V6gfC";
    private static final String GITHUB_REPOSITORY_URL = "https://github.com/EthanolMC/EthanolMod";

    private static final String DISCORD_TEXT = "Discord";
    private static final String GITHUB_TEXT = "GitHub";

    private final Screen parentScreen;

    private TextFieldWidget commandPrefixField;

    public ConfigScreen(final Screen parentScreen) {
        super(Text.of("Ethanol Mod Config"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final TextRenderer textRenderer = this.textRenderer;

        this.addDrawableChild(new PressableTextWidget(
                4,
                this.height - textRenderer.fontHeight - 4,
                textRenderer.getWidth(ConfigScreen.DISCORD_TEXT),
                textRenderer.fontHeight + 2,
                Text.of(ConfigScreen.DISCORD_TEXT),
                ConfirmLinkScreen.opening(this, DISCORD_INVITE_URL),
                textRenderer
        )).setTooltip(Tooltip.of(Text.of("Join the Ethanol Discord server.")));

        this.addDrawableChild(new PressableTextWidget(
                this.width - textRenderer.getWidth(ConfigScreen.GITHUB_TEXT) - 4,
                this.height - textRenderer.fontHeight - 4,
                textRenderer.getWidth(ConfigScreen.GITHUB_TEXT),
                textRenderer.fontHeight + 2,
                Text.of(ConfigScreen.GITHUB_TEXT),
                ConfirmLinkScreen.opening(this, GITHUB_REPOSITORY_URL),
                textRenderer
        )).setTooltip(Tooltip.of(Text.of("View the Ethanol Mod GitHub repository.")));

        final Configuration configuration = EthanolMod.getInstance().getConfiguration();

        this.commandPrefixField = new TextFieldWidget(
                textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 50,
                200,
                20,
                Text.of("Command Prefix")
        );
        this.commandPrefixField.setText(configuration.getCommandPrefix());
        this.commandPrefixField.setTooltip(Tooltip.of(Text.of("The prefix to use Ethanol commands.")));
        this.commandPrefixField.setMaxLength(25);
        this.commandPrefixField.setChangedListener(configuration::setCommandPrefix);
        this.addSelectableChild(this.commandPrefixField);

        final int buttonWidth = 190;
        final int commandPrefixFieldY = this.commandPrefixField.getY() + this.commandPrefixField.getHeight();
        final int x = this.width / 2 - 95;
        final int offsetY = 22;
        int y = commandPrefixFieldY + 14;

        final ButtonWidget configPositionButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Config Button Position: ".concat(configuration.getConfigButtonPosition().getName())
        ), (button) -> {
            final Configuration.ConfigButtonPosition[] values = Configuration.ConfigButtonPosition.values();
            final int nextIndex = configuration.getConfigButtonPosition().ordinal() + 1;
            if (nextIndex < values.length) {
                configuration.setConfigButtonPosition(values[nextIndex]);
            } else {
                configuration.setConfigButtonPosition(values[0]);
            }
            button.setMessage(Text.of(
                    "Config Button Position: ".concat(configuration.getConfigButtonPosition().getName())
            ));
        }).position(x, y).width(buttonWidth).build());
        configPositionButton.setTooltip(Tooltip.of(Text.of("The position of the config button in the game menu screen and the multiplayer screen.")));
        y += offsetY;

        final ButtonWidget displayCommandSendWarningButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Display Command Send Warning: ".concat(String.valueOf(configuration.getDisplayCommandSendWarning()))
        ), (button) -> {
            configuration.setDisplayCommandSendWarning(!configuration.getDisplayCommandSendWarning());
            button.setMessage(Text.of(
                    "Display Command Send Warning: ".concat(String.valueOf(configuration.getDisplayCommandSendWarning()))
            ));
        }).position(x, y).width(buttonWidth).build());
        displayCommandSendWarningButton.setTooltip(Tooltip.of(Text.of("This will display a warning when Ethanol is not installed on the server and when you try to send a chat message with the Ethanol prefix.")));
        y += offsetY;

        final ButtonWidget displayVanishedWarningButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Display Vanished Warning: ".concat(String.valueOf(configuration.getDisplayVanishedWarning()))
        ), (button) -> {
            configuration.setDisplayVanishedWarning(!configuration.getDisplayVanishedWarning());
            button.setMessage(Text.of(
                    "Display Vanished Warning: ".concat(String.valueOf(configuration.getDisplayVanishedWarning()))
            ));
        }).position(x, y).width(buttonWidth).build());
        displayVanishedWarningButton.setTooltip(Tooltip.of(Text.of("This will display a warning when you are vanished and when you try to send a chat message.")));
        y += offsetY * 2;

        final ButtonWidget resetConfigButton = this.addDrawableChild(ButtonWidget.builder(Text.of("Reset Config"), (button) -> {
            configuration.setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
            configuration.setConfigButtonPosition(Configuration.DEFAULT_BUTTON_POSITION);
            configuration.setDisplayCommandSendWarning(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING);
            configuration.setDisplayVanishedWarning(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING);
            this.commandPrefixField.setText(Configuration.DEFAULT_COMMAND_PREFIX);
            configPositionButton.setMessage(Text.of(
                    "Config Button Position: ".concat(Configuration.DEFAULT_BUTTON_POSITION.getName())
            ));
            displayCommandSendWarningButton.setMessage(Text.of(
                    "Display Command Send Warning: ".concat(String.valueOf(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING))
            ));
            displayVanishedWarningButton.setMessage(Text.of(
                    "Display Vanished Warning: ".concat(String.valueOf(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING))
            ));
        }).position(x, y).width(buttonWidth).build());
        resetConfigButton.setTooltip(Tooltip.of(Text.literal("WARNING: This will reset all of your settings to the default values.").formatted(Formatting.RED)));
        y += offsetY;

        this.addDrawableChild(
                ButtonWidget
                        .builder(ScreenTexts.BACK, (button) -> this.close())
                        .position(this.width / 2 - 75, y + 2)
                        .build()
        );
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        final TextRenderer textRenderer = this.textRenderer;
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawTextWithShadow(
                textRenderer,
                "Command Prefix",
                this.commandPrefixField.getX(),
                this.commandPrefixField.getY() - textRenderer.fontHeight - 2,
                16777215
        );
        this.commandPrefixField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.commandPrefixField.getText().isEmpty()) {
            EthanolMod.getInstance().getConfiguration().setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
        }
        this.client.setScreen(this.parentScreen);
    }

    public static ButtonWidget createButton(final Screen currentScreen) {
        final ButtonWidget.Builder buttonBuilder = ButtonWidget.builder(Text.of("E"), button -> {
            if (mc != null) {
                mc.setScreen(new ConfigScreen(currentScreen));
            }
        });
        switch (EthanolMod.getInstance().getConfiguration().getConfigButtonPosition()) {
            case Configuration.ConfigButtonPosition.TOP_LEFT -> buttonBuilder.position(4, 4);
            case Configuration.ConfigButtonPosition.TOP_RIGHT -> buttonBuilder.position(currentScreen.width - 24, 4);
            case Configuration.ConfigButtonPosition.BOTTOM_LEFT -> buttonBuilder.position(4, currentScreen.height - 24);
            case Configuration.ConfigButtonPosition.BOTTOM_RIGHT -> buttonBuilder.position(currentScreen.width - 24, currentScreen.height - 24);
        }
        return buttonBuilder.size(20, 20).build();
    }

}
