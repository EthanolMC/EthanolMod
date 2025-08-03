package rocks.ethanol.ethanolmod.screen;

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

import java.awt.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private static final int CHILD_WIDTH = 200;
    private static final int CHILD_OFFSET_Y = 22;
    private static final int TEXT_FIELD_HEIGHT = 20;

    private final Screen parentScreen;
    private TextFieldWidget commandPrefixField;
    private TextFieldWidget detectionNotificationDisplayDurationField;

    public ConfigScreen(final Screen parentScreen) {
        super(Text.literal("Ethanol Mod Config").formatted(Formatting.UNDERLINE));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final Configuration configuration = EthanolMod.getInstance().getConfiguration();
        final int centerX = this.width / 2;
        final int x = centerX - (CHILD_WIDTH / 2);
        int y = this.height / 2 - 70;

        this.commandPrefixField = this.addTextField(
                centerX - 100, y,
                configuration.getCommandPrefix(),
                "The prefix to use Ethanol commands.",
                25,
                text -> !text.startsWith("/")
        );
        this.addSelectableChild(this.commandPrefixField);
        y += CHILD_OFFSET_Y + mc.textRenderer.fontHeight + 5;

        this.detectionNotificationDisplayDurationField = this.addTextField(
                centerX - 100, y,
                String.valueOf(configuration.getDetectionNotificationDisplayDuration()),
                "The duration in milliseconds to display the Ethanol detection notification.",
                20,
                text -> text.isEmpty() || text.matches("\\d+")
        );
        this.detectionNotificationDisplayDurationField.setChangedListener(text -> {
            try {
                long value = Long.parseLong(text);
                configuration.setDetectionNotificationDisplayDuration(value);
                this.detectionNotificationDisplayDurationField.setEditableColor(Color.WHITE.getRGB());
            } catch (NumberFormatException ignored) {
                this.detectionNotificationDisplayDurationField.setEditableColor(Color.RED.getRGB());
            }
        });
        this.addSelectableChild(this.detectionNotificationDisplayDurationField);
        y += CHILD_OFFSET_Y + 5;

        // buttons
        y = this.addButton(
                x, y,
                "Config Button Position",
                "The position of the config button in the game menu screen and the multiplayer screen.",
                configuration.getConfigButtonPosition().getDisplayName(),
                () -> {
                    var position = configuration.getConfigButtonPosition();
                    var values = Configuration.ConfigButtonPosition.values();
                    var next = values[(position.ordinal() + 1) % values.length];
                    configuration.setConfigButtonPosition(next);
                    return next.getDisplayName();
                }
        );

        y = this.addButton(
                x, y,
                "Display Command Send Warning",
                "Displays a warning when sending Ethanol commands on a server without Ethanol.",
                String.valueOf(configuration.getDisplayCommandSendWarning()),
                () -> {
                    final boolean value = !configuration.getDisplayCommandSendWarning();
                    configuration.setDisplayCommandSendWarning(value);
                    return String.valueOf(value);
                }
        );

        y = this.addButton(
                x, y,
                "Display Vanished Warning",
                "Displays a warning when trying to send a chat message while vanished.",
                String.valueOf(configuration.getDisplayVanishedWarning()),
                () -> {
                    final boolean value = !configuration.getDisplayVanishedWarning();
                    configuration.setDisplayVanishedWarning(value);
                    return String.valueOf(value);
                }
        );

        this.addButton(
                x, y,
                "Infinite Command Input Length",
                "Allows infinite characters in the chat input when using Ethanol commands.",
                String.valueOf(configuration.getInfiniteCommandInputLength()),
                () -> {
                    final boolean value = !configuration.getInfiniteCommandInputLength();
                    configuration.setInfiniteCommandInputLength(value);
                    return String.valueOf(value);
                }
        );

        this.addDrawableChild(ButtonWidget.builder(Text.of("Reset Config"), button -> {
            configuration.setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
            configuration.setConfigButtonPosition(Configuration.DEFAULT_BUTTON_POSITION);
            configuration.setDisplayCommandSendWarning(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING);
            configuration.setDisplayVanishedWarning(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING);
            configuration.setInfiniteCommandInputLength(Configuration.DEFAULT_INFINITE_COMMAND_INPUT_LENGTH);
            configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);

            this.commandPrefixField.setText(Configuration.DEFAULT_COMMAND_PREFIX);
            this.detectionNotificationDisplayDurationField.setText(String.valueOf(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION));
            this.init();
        }).dimensions(this.width - 72, this.height - 22, 70, 20).build()).setTooltip(Tooltip.of(Text.literal("WARNING: This will reset all settings.").formatted(Formatting.RED)));

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, b -> this.close())
                .dimensions(2, this.height - 22, 70, 20).build());
    }

    @Override
    public final void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 20, Color.WHITE.getRGB());

        // render cmd prefix text
        context.drawTextWithShadow(
                textRenderer,
                "Command Prefix",
                this.commandPrefixField.getX() + 1,
                this.commandPrefixField.getY() - textRenderer.fontHeight - 1,
                Color.WHITE.getRGB()
        );
        this.commandPrefixField.render(context, mouseX, mouseY, delta);

        // render detection notification display duration text
        context.drawTextWithShadow(
                textRenderer,
                "Detection Notification Display Duration",
                this.detectionNotificationDisplayDurationField.getX() + 1,
                this.detectionNotificationDisplayDurationField.getY() - textRenderer.fontHeight - 1,
                Color.WHITE.getRGB()
        );
        this.detectionNotificationDisplayDurationField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public final void close() {
        final Configuration configuration = EthanolMod.getInstance().getConfiguration();
        final String prefix = commandPrefixField.getText();
        configuration.setCommandPrefix(prefix.isEmpty() ? Configuration.DEFAULT_COMMAND_PREFIX : prefix);

        // sanitize
        final String detectionNotificationDisplayDuration = this.detectionNotificationDisplayDurationField.getText();
        try {
            configuration.setDetectionNotificationDisplayDuration(Long.parseLong(detectionNotificationDisplayDuration));
        } catch (final NumberFormatException ignored) {
            configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);
        }

        this.client.setScreen(this.parentScreen);
    }

    private TextFieldWidget addTextField(final int x, final int y, final String text, final String tooltip, final int maxLength, final Predicate<String> predicate) {
        final TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, CHILD_WIDTH, TEXT_FIELD_HEIGHT, Text.of(""));
        field.setTooltip(Tooltip.of(Text.of(tooltip)));
        field.setText(text);
        field.setMaxLength(maxLength);
        field.setTextPredicate(predicate);
        return field;
    }

    private int addButton(final int x, final int y, final String label, String tooltip, final String initial, final Supplier<String> value) {
        final ButtonWidget button = this.addDrawableChild(ButtonWidget.builder(Text.of(label + ": " + initial), btn -> {
            btn.setMessage(Text.of(label + ": " + value.get()));
        }).position(x, y).width(CHILD_WIDTH).build());
        button.setTooltip(Tooltip.of(Text.of(tooltip)));
        return y + CHILD_OFFSET_Y;
    }

}
