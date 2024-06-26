package rocks.ethanol.ethanolmod.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private final Screen parentScreen;

    private TextFieldWidget commandPrefixField;

    public ConfigScreen(final Screen parentScreen) {
        super(Text.of("Ethanol Mod Config"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        final ConfigManager configManager = EthanolMod.getInstance().getConfigManager();

        this.commandPrefixField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 50,
                200,
                20,
                Text.of("Command Prefix")
        );
        this.commandPrefixField.setText(configManager.getCommandPrefix());
        this.commandPrefixField.setTooltip(Tooltip.of(Text.of("The prefix to use Ethanol commands.")));
        this.commandPrefixField.setMaxLength(25);
        this.commandPrefixField.setChangedListener(configManager::setCommandPrefix);
        this.addSelectableChild(this.commandPrefixField);

        final int commandPrefixFieldY = this.commandPrefixField.getY() + this.commandPrefixField.getHeight();
        final int x = this.width / 2 - 95;
        final int width = 190;
        final int offsetY = 22;
        int y = commandPrefixFieldY + 14;

        final ButtonWidget configPositionButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Config Button Position: " + configManager.getConfigButtonPosition().getName()
        ), (button) -> {
            final ConfigManager.ConfigButtonPosition[] values = ConfigManager.ConfigButtonPosition.values();
            final int nextIndex = configManager.getConfigButtonPosition().ordinal() + 1;
            if (nextIndex < values.length) {
                configManager.setConfigButtonPosition(values[nextIndex]);
            } else {
                configManager.setConfigButtonPosition(values[0]);
            }
            button.setMessage(Text.of(
                    "Config Button Position: " + configManager.getConfigButtonPosition().getName()
            ));
        }).position(x, y).width(width).build());
        configPositionButton.setTooltip(Tooltip.of(Text.of("The position of the config button in the game menu screen and the multiplayer screen.")));
        y += offsetY;

        final ButtonWidget displayCommandSendWarningButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Display Command Send Warning: " + configManager.getDisplayCommandSendWarning()
        ), (button) -> {
            configManager.setDisplayCommandSendWarning(!configManager.getDisplayCommandSendWarning());
            button.setMessage(Text.of(
                    "Display Command Send Warning: " + configManager.getDisplayCommandSendWarning()
            ));
        }).position(x, y).width(width).build());
        displayCommandSendWarningButton.setTooltip(Tooltip.of(Text.of("This will display a warning when Ethanol is not installed on the server and when you try to send a chat message with the Ethanol prefix.")));
        y += offsetY;

        final ButtonWidget displayVanishedWarningButton = this.addDrawableChild(ButtonWidget.builder(Text.of(
                "Display Vanished Warning: " + configManager.getDisplayVanishedWarning()
        ), (button) -> {
            configManager.setDisplayVanishedWarning(!configManager.getDisplayVanishedWarning());
            button.setMessage(Text.of(
                    "Display Vanished Warning: " + configManager.getDisplayVanishedWarning()
            ));
        }).position(x, y).width(width).build());
        displayVanishedWarningButton.setTooltip(Tooltip.of(Text.of("This will display a warning when you are vanished and when you try to send a chat message.")));
        y += offsetY * 2;

        final ButtonWidget resetConfigButton = this.addDrawableChild(ButtonWidget.builder(Text.of("Reset Config"), (button) -> {
            configManager.setCommandPrefix(ConfigManager.DEFAULT_COMMAND_PREFIX);
            configManager.setConfigButtonPosition(ConfigManager.DEFAULT_BUTTON_POSITION);
            configManager.setDisplayCommandSendWarning(ConfigManager.DEFAULT_DISPLAY_COMMAND_SEND_WARNING);
            configManager.setDisplayVanishedWarning(ConfigManager.DEFAULT_DISPLAY_VANISHED_WARNING);
            this.commandPrefixField.setText(ConfigManager.DEFAULT_COMMAND_PREFIX);
            configPositionButton.setMessage(Text.of(
                    "Config Button Position: " + ConfigManager.DEFAULT_BUTTON_POSITION.getName()
            ));
            displayCommandSendWarningButton.setMessage(Text.of(
                    "Display Command Send Warning: " + ConfigManager.DEFAULT_DISPLAY_COMMAND_SEND_WARNING
            ));
            displayVanishedWarningButton.setMessage(Text.of(
                    "Display Vanished Warning: " + ConfigManager.DEFAULT_DISPLAY_VANISHED_WARNING
            ));
        }).position(x, y).width(width).build());
        resetConfigButton.setTooltip(Tooltip.of(Text.of(Formatting.RED + "WARNING: This will reset all of your settings to the default values.")));
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
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawTextWithShadow(
                this.textRenderer,
                "Command Prefix",
                this.commandPrefixField.getX(),
                this.commandPrefixField.getY() - this.textRenderer.fontHeight - 2,
                16777215
        );
        this.commandPrefixField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.commandPrefixField.getText().isEmpty()) {
            EthanolMod.getInstance().getConfigManager().setCommandPrefix(ConfigManager.DEFAULT_COMMAND_PREFIX);
        }
        this.client.setScreen(this.parentScreen);
    }

    public static ButtonWidget createButton(final Screen currentScreen) {
        final ButtonWidget.Builder buttonBuilder = ButtonWidget.builder(Text.of("E"), button -> {
            if (mc != null) {
                mc.setScreen(new ConfigScreen(currentScreen));
            }
        });
        switch (EthanolMod.getInstance().getConfigManager().getConfigButtonPosition()) {
            case ConfigManager.ConfigButtonPosition.TOP_LEFT -> buttonBuilder.position(4, 4);
            case ConfigManager.ConfigButtonPosition.TOP_RIGHT -> buttonBuilder.position(currentScreen.width - 24, 4);
            case ConfigManager.ConfigButtonPosition.BOTTOM_LEFT -> buttonBuilder.position(4, currentScreen.height - 24);
            case ConfigManager.ConfigButtonPosition.BOTTOM_RIGHT -> buttonBuilder.position(currentScreen.width - 24, currentScreen.height - 24);
        }
        return buttonBuilder.size(20, 20).build();
    }

}
