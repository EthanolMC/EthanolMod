package rocks.ethanol.ethanolmod.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private final Screen parentScreen;

    private TextFieldWidget commandPrefixField;

    public ConfigScreen(final Screen parentScreen) {
        super(Text.literal("Ethanol Mod Config").formatted(Formatting.UNDERLINE));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final TextRenderer textRenderer = this.textRenderer;

        final Configuration configuration = EthanolMod.getInstance().getConfiguration();

        this.commandPrefixField = new TextFieldWidget(
                textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 70,
                200,
                20,
                Text.of("Command Prefix")
        );
        this.commandPrefixField.setText(configuration.getCommandPrefix());
        this.commandPrefixField.setTooltip(Tooltip.of(Text.of("The prefix to use Ethanol commands.")));
        this.commandPrefixField.setMaxLength(25);
        this.commandPrefixField.setChangedListener(text -> {
            if (text.startsWith("/")) {
                text = Configuration.DEFAULT_COMMAND_PREFIX;
                this.commandPrefixField.setText(text);
            }
            configuration.setCommandPrefix(text);
        });
        this.addSelectableChild(this.commandPrefixField);

        final int buttonWidth = 190;
        final int commandPrefixFieldY = this.commandPrefixField.getY() + this.commandPrefixField.getHeight();
        final int x = this.width / 2 - 95;
        final int offsetY = 22;
        int y = commandPrefixFieldY + 14;

        final ButtonWidget configPositionButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Config Button Position: ".concat(configuration.getConfigButtonPosition().getDisplayName())
        ), (button) -> {
            Configuration.ConfigButtonPosition position = configuration.getConfigButtonPosition();
            final Configuration.ConfigButtonPosition[] values = Configuration.ConfigButtonPosition.values();
            final int nextIndex = position.ordinal() + 1;
            if (nextIndex < values.length) {
                configuration.setConfigButtonPosition(position = values[nextIndex]);
            } else {
                configuration.setConfigButtonPosition(position = values[0]);
            }
            button.setMessage(Text.of(
                    "Config Button Position: ".concat(position.getDisplayName())
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
        displayCommandSendWarningButton.setTooltip(Tooltip.of(Text.of("Displays a warning upon trying to send messages recognized as ethanol commands on a server where Ethanol is not installed.")));
        y += offsetY;

        final ButtonWidget displayVanishedWarningButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Display Vanished Warning: ".concat(String.valueOf(configuration.getDisplayVanishedWarning()))
        ), (button) -> {
            configuration.setDisplayVanishedWarning(!configuration.getDisplayVanishedWarning());
            button.setMessage(Text.of(
                    "Display Vanished Warning: ".concat(String.valueOf(configuration.getDisplayVanishedWarning()))
            ));
        }).position(x, y).width(buttonWidth).build());
        displayVanishedWarningButton.setTooltip(Tooltip.of(Text.of("Displays a warning when trying to send a chat message while being vanished.")));
        y += offsetY;

        final ButtonWidget infiniteCommandInputLengthButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Infinite Command Input Length: ".concat(String.valueOf(configuration.getInfiniteCommandInputLength()))
        ), (button) -> {
            configuration.setInfiniteCommandInputLength(!configuration.getInfiniteCommandInputLength());
            button.setMessage(Text.of(
                    "Infinite Command Input Length: ".concat(String.valueOf(configuration.getInfiniteCommandInputLength()))
            ));
        }).position(x, y).width(buttonWidth).build());
        infiniteCommandInputLengthButton.setTooltip(Tooltip.of(Text.of("This will allow you to type an infinite amount of characters in the chat input field when it starts with the Ethanol prefix and when Ethanol is installed on the server.")));
        y += offsetY * 2;

        final ButtonWidget resetConfigButton = this.addDrawableChild(ButtonWidget.builder(Text.of("Reset Config"), (button) -> {
            configuration.setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
            configuration.setConfigButtonPosition(Configuration.DEFAULT_BUTTON_POSITION);
            configuration.setDisplayCommandSendWarning(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING);
            configuration.setDisplayVanishedWarning(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING);
            configuration.setInfiniteCommandInputLength(Configuration.DEFAULT_INFINITE_COMMAND_INPUT_LENGTH);
            this.commandPrefixField.setText(Configuration.DEFAULT_COMMAND_PREFIX);
            configPositionButton.setMessage(Text.of(
                    "Config Button Position: ".concat(Configuration.DEFAULT_BUTTON_POSITION.getDisplayName())
            ));
            displayCommandSendWarningButton.setMessage(Text.of(
                    "Display Command Send Warning: ".concat(String.valueOf(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING))
            ));
            displayVanishedWarningButton.setMessage(Text.of(
                    "Display Vanished Warning: ".concat(String.valueOf(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING))
            ));
            infiniteCommandInputLengthButton.setMessage(Text.of(
                    "Infinite Command Input Length: ".concat(String.valueOf(Configuration.DEFAULT_INFINITE_COMMAND_INPUT_LENGTH))
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
    public final void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
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
    public final void close() {
        if (this.commandPrefixField.getText().isEmpty()) {
            EthanolMod.getInstance().getConfiguration().setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
        }
        this.client.setScreen(this.parentScreen);
    }

}
