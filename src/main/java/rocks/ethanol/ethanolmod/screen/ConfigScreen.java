package rocks.ethanol.ethanolmod.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigScreen extends Screen implements MinecraftWrapper {

    private static final int PANEL_WIDTH = 300;
    private static final int CHILD_WIDTH = 220;
    private static final int CHILD_HEIGHT = 20;
    private static final int ROW_SPACING = 6;
    private static final int LABEL_GAP = 4;
    private static final int SECTION_GAP = 12;

    private final Screen parentScreen;
    private EditBox commandPrefixField;
    private EditBox detectionDurationField;
    private int panelX;
    private int panelY;
    private int panelHeight;

    public ConfigScreen(final Screen parentScreen) {
        super(Component.literal("Configuration"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected final void init() {
        final Configuration configuration = EthanolMod.getInstance().getConfiguration();
        final Font textRenderer = this.font;

        final int rowHeight = CHILD_HEIGHT + ROW_SPACING;
        final int labeledRowHeight = textRenderer.lineHeight + LABEL_GAP + CHILD_HEIGHT + ROW_SPACING;

        this.panelHeight = 40
                + textRenderer.lineHeight + 4
                + labeledRowHeight * 2
                + SECTION_GAP
                + textRenderer.lineHeight + 4
                + rowHeight * 4
                + 8;

        this.panelX = (this.width - PANEL_WIDTH) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;

        final int contentX = this.panelX + (PANEL_WIDTH - CHILD_WIDTH) / 2;
        int y = this.panelY + 40;

        // Section: General label
        y += textRenderer.lineHeight + 4;

        // Command Prefix
        y += textRenderer.lineHeight + LABEL_GAP;
        this.commandPrefixField = this.addTextField(
                contentX, y,
                configuration.getCommandPrefix(),
                "The prefix to use Ethanol commands.",
                25,
                text -> !text.startsWith("/")
        );
        y += CHILD_HEIGHT + ROW_SPACING;

        // Detection Notification Duration
        y += textRenderer.lineHeight + LABEL_GAP;
        this.detectionDurationField = this.addTextField(
                contentX, y,
                String.valueOf(configuration.getDetectionNotificationDisplayDuration()),
                "The duration in milliseconds to display the Ethanol detection notification.",
                20,
                text -> text.isEmpty() || text.matches("\\d+")
        );
        this.detectionDurationField.setResponder(text -> {
            try {
                configuration.setDetectionNotificationDisplayDuration(Long.parseLong(text));
                this.detectionDurationField.setTextColor(Theme.TEXT);
            } catch (final NumberFormatException ignored) {
                this.detectionDurationField.setTextColor(Theme.DANGER);
            }
        });
        y += CHILD_HEIGHT + ROW_SPACING + SECTION_GAP;

        // Section: Behavior label
        y += textRenderer.lineHeight + 4;

        y = this.addToggle(contentX, y,
                "Config Button Position",
                "The position of the config button in the game menu screen and the multiplayer screen.",
                configuration.getConfigButtonPosition().getDisplayName(),
                () -> {
                    final Configuration.ConfigButtonPosition position = configuration.getConfigButtonPosition();
                    final Configuration.ConfigButtonPosition[] values = Configuration.ConfigButtonPosition.values();
                    final Configuration.ConfigButtonPosition next = values[(position.ordinal() + 1) % values.length];
                    configuration.setConfigButtonPosition(next);
                    return next.getDisplayName();
                }
        );

        y = this.addToggle(contentX, y,
                "Command Send Warning",
                "Displays a warning when sending Ethanol commands on a server without Ethanol.",
                String.valueOf(configuration.getDisplayCommandSendWarning()),
                () -> {
                    final boolean value = !configuration.getDisplayCommandSendWarning();
                    configuration.setDisplayCommandSendWarning(value);
                    return String.valueOf(value);
                }
        );

        y = this.addToggle(contentX, y,
                "Vanished Warning",
                "Displays a warning when trying to send a chat message while vanished.",
                String.valueOf(configuration.getDisplayVanishedWarning()),
                () -> {
                    final boolean value = !configuration.getDisplayVanishedWarning();
                    configuration.setDisplayVanishedWarning(value);
                    return String.valueOf(value);
                }
        );

        this.addToggle(contentX, y,
                "Infinite Command Input",
                "Allows infinite characters in the chat input when using Ethanol commands.",
                String.valueOf(configuration.getInfiniteCommandInputLength()),
                () -> {
                    final boolean value = !configuration.getInfiniteCommandInputLength();
                    configuration.setInfiniteCommandInputLength(value);
                    return String.valueOf(value);
                }
        );

        // Footer
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose())
                .bounds(2, this.height - 22, 70, 20)
                .build());

        Button reset = Button.builder(Component.literal("Reset"), button -> {
            configuration.setCommandPrefix(Configuration.DEFAULT_COMMAND_PREFIX);
            configuration.setConfigButtonPosition(Configuration.DEFAULT_BUTTON_POSITION);
            configuration.setDisplayCommandSendWarning(Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING);
            configuration.setDisplayVanishedWarning(Configuration.DEFAULT_DISPLAY_VANISHED_WARNING);
            configuration.setInfiniteCommandInputLength(Configuration.DEFAULT_INFINITE_COMMAND_INPUT_LENGTH);
            configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);
            this.rebuildWidgets();
        }).bounds(this.width - 72, this.height - 22, 70, 20).build();
        reset.setTooltip(Tooltip.create(Component.literal("Reset all settings to default.").withStyle(ChatFormatting.RED)));
        this.addRenderableWidget(reset);
    }

    @Override
    public final void extractRenderState(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float delta) {
        Theme.drawBackdrop(context, this.width, this.height);
        Theme.drawPanel(context, this.panelX, this.panelY, PANEL_WIDTH, this.panelHeight);
        context.fill(this.panelX, this.panelY, this.panelX + PANEL_WIDTH, this.panelY + 2, Theme.ACCENT);

        super.extractRenderState(context, mouseX, mouseY, delta);

        final Font textRenderer = this.font;
        final int centerX = this.panelX + PANEL_WIDTH / 2;

        context.text(textRenderer, this.title, centerX - textRenderer.width(this.title) / 2, this.panelY + 14, Theme.TEXT, false);
        context.text(textRenderer, "Tweak how Ethanol behaves",
                centerX - textRenderer.width("Tweak how Ethanol behaves") / 2,
                this.panelY + 14 + textRenderer.lineHeight + 2, Theme.TEXT_MUTED, false);

        final int contentX = this.panelX + (PANEL_WIDTH - CHILD_WIDTH) / 2;
        Theme.drawSectionHeader(context, textRenderer, "General", contentX, this.panelY + 40);

        if (this.commandPrefixField != null) {
            context.text(textRenderer, "Command Prefix", contentX, this.commandPrefixField.getY() - textRenderer.lineHeight - LABEL_GAP, Theme.TEXT_MUTED, false);
        }
        if (this.detectionDurationField != null) {
            context.text(textRenderer, "Detection Notification Duration (ms)", contentX, this.detectionDurationField.getY() - textRenderer.lineHeight - LABEL_GAP, Theme.TEXT_MUTED, false);

            final int behaviorY = this.detectionDurationField.getY() + CHILD_HEIGHT + ROW_SPACING + SECTION_GAP;
            Theme.drawSectionHeader(context, textRenderer, "Behavior", contentX, behaviorY);
        }
    }

    @Override
    public final void onClose() {
        final Configuration configuration = EthanolMod.getInstance().getConfiguration();
        final String prefix = this.commandPrefixField.getValue();
        configuration.setCommandPrefix(prefix.isEmpty() ? Configuration.DEFAULT_COMMAND_PREFIX : prefix);

        try {
            configuration.setDetectionNotificationDisplayDuration(Long.parseLong(this.detectionDurationField.getValue()));
        } catch (final NumberFormatException ignored) {
            configuration.setDetectionNotificationDisplayDuration(Configuration.DEFAULT_DETECTION_NOTIFICATION_DISPLAY_DURATION);
        }

        this.minecraft.setScreen(this.parentScreen);
    }

    private EditBox addTextField(final int x, final int y, final String text, final String tooltip, final int maxLength, final Predicate<String> predicate) {
        final EditBox field = new EditBox(this.font, x, y, CHILD_WIDTH, CHILD_HEIGHT, Component.empty());
        field.setTooltip(Tooltip.create(Component.literal(tooltip)));
        field.setMaxLength(maxLength);
        field.setValue(text);
        field.setResponder(value -> field.setTextColor(predicate.test(value) ? Theme.TEXT : Theme.DANGER));
        this.addRenderableWidget(field);
        return field;
    }

    private int addToggle(final int x, final int y, final String label, final String tooltip, final String initial, final Supplier<String> value) {
        final Button button = Button.builder(Component.literal(label + ": " + initial), btn -> {
            btn.setMessage(Component.literal(label + ": " + value.get()));
        }).bounds(x, y, CHILD_WIDTH, CHILD_HEIGHT).build();
        button.setTooltip(Tooltip.create(Component.literal(tooltip)));
        this.addRenderableWidget(button);
        return y + CHILD_HEIGHT + ROW_SPACING;
    }

}
