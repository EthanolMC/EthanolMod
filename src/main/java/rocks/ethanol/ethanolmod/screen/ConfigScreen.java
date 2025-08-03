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

import java.awt.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private static final int BUTTON_OFFSET_Y = 22;
    private static final int BUTTON_WIDTH = 190;

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
        this.commandPrefixField.setTextPredicate(text -> !text.startsWith("/"));
        this.addSelectableChild(this.commandPrefixField);

        this.detectionNotificationDisplayDurationField = new TextFieldWidget(
                textRenderer,
                this.width / 2 - 100,
                this.height / 2 - 30,
                200,
                20,
                Text.of("Detection Notification Display Duration")
        );
        this.detectionNotificationDisplayDurationField.setText(String.valueOf(configuration.getDetectionNotificationDisplayDuration()));
        this.detectionNotificationDisplayDurationField.setTooltip(Tooltip.of(Text.of("The duration in milliseconds to display the Ethanol detection notification.")));
        this.detectionNotificationDisplayDurationField.setMaxLength(20);
        this.detectionNotificationDisplayDurationField.setChangedListener(text -> {
            try {
                long value = Long.parseLong(text);
                configuration.setDetectionNotificationDisplayDuration(value);
                this.detectionNotificationDisplayDurationField.setEditableColor(Color.WHITE.getRGB());
            } catch (NumberFormatException ignored) {
                this.detectionNotificationDisplayDurationField.setEditableColor(Color.RED.getRGB());
            }
        });
        this.detectionNotificationDisplayDurationField.setTextPredicate(text -> text.isEmpty() || text.matches("\\d+"));
        this.addSelectableChild(this.detectionNotificationDisplayDurationField);

        final int startY = this.detectionNotificationDisplayDurationField.getY() + this.detectionNotificationDisplayDurationField.getHeight();
        final int x = this.width / 2 - 95;
        int y = startY + 14;

        y = addButton(
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

        y = addButton(
                x, y,
                "Display Command Send Warning",
                "Displays a warning when sending Ethanol commands on a server without Ethanol.",
                String.valueOf(configuration.getDisplayCommandSendWarning()),
                () -> {
                    boolean value = !configuration.getDisplayCommandSendWarning();
                    configuration.setDisplayCommandSendWarning(value);
                    return String.valueOf(value);
                }
        );

        y = addButton(
                x, y,
                "Display Vanished Warning",
                "Displays a warning when trying to send a chat message while vanished.",
                String.valueOf(configuration.getDisplayVanishedWarning()),
                () -> {
                    boolean value = !configuration.getDisplayVanishedWarning();
                    configuration.setDisplayVanishedWarning(value);
                    return String.valueOf(value);
                }
        );

        y = addButton(
                x, y,
                "Infinite Command Input Length",
                "Allows infinite characters in the chat input when using Ethanol commands.",
                String.valueOf(configuration.getInfiniteCommandInputLength()),
                () -> {
                    boolean value = !configuration.getInfiniteCommandInputLength();
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

        context.drawTextWithShadow(
                textRenderer,
                "Detection Notification Display Duration",
                this.detectionNotificationDisplayDurationField.getX(),
                this.detectionNotificationDisplayDurationField.getY() - textRenderer.fontHeight - 2,
                16777215
        );
        this.detectionNotificationDisplayDurationField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public final void close() {
        final Configuration configuration = EthanolMod.getInstance().getConfiguration();
        final String prefix = commandPrefixField.getText();
        configuration.setCommandPrefix(prefix.isEmpty() ? Configuration.DEFAULT_COMMAND_PREFIX : prefix);

        final String detectionNotificationDisplayDuration = this.detectionNotificationDisplayDurationField.getText();
        if (detectionNotificationDisplayDuration.isEmpty()) {
            configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);
        } else {
            try {
                configuration.setDetectionNotificationDisplayDuration(Long.parseLong(detectionNotificationDisplayDuration));
            } catch (final NumberFormatException ignored) {
                configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);
            }
        }

        this.client.setScreen(this.parentScreen);
    }

    private int addButton(final int x, final int y, final String label, String tooltip, final String initial, final Supplier<String> value) {
        ButtonWidget button = this.addDrawableChild(ButtonWidget.builder(Text.of(label + ": " + initial), btn -> {
            String newValue = value.get();
            btn.setMessage(Text.of(label + ": " + newValue));
        }).position(x, y).width(BUTTON_WIDTH).build());
        button.setTooltip(Tooltip.of(Text.of(tooltip)));
        return y + BUTTON_OFFSET_Y;
    }

}
