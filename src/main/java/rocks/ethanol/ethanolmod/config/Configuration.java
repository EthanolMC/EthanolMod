package rocks.ethanol.ethanolmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String DEFAULT_COMMAND_PREFIX = "-";
    public static final ConfigButtonPosition DEFAULT_BUTTON_POSITION = ConfigButtonPosition.BOTTOM_LEFT;
    public static final boolean DEFAULT_DISPLAY_COMMAND_SEND_WARNING = true;
    public static final boolean DEFAULT_DISPLAY_VANISHED_WARNING = true;
    public static final boolean DEFAULT_INFINITE_COMMAND_INPUT_LENGTH = true;

    private final Path file;
    private String commandPrefix;
    private ConfigButtonPosition configButtonPosition;
    private boolean displayCommandSendWarning;
    private boolean displayVanishedWarning;
    private boolean infiniteCommandInputLength;

    public Configuration(final Path file) {
        this.file = file;
        this.commandPrefix = Configuration.DEFAULT_COMMAND_PREFIX;
        this.configButtonPosition = Configuration.DEFAULT_BUTTON_POSITION;
        this.displayCommandSendWarning = Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING;
        this.displayVanishedWarning = Configuration.DEFAULT_DISPLAY_VANISHED_WARNING;
        this.infiniteCommandInputLength = Configuration.DEFAULT_INFINITE_COMMAND_INPUT_LENGTH;
    }

    public final void load() throws Exception {
        if (!Files.exists(this.file)) {
            return;
        }
        try (final Reader reader = Files.newBufferedReader(this.file)) {
            final JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
            if (data.has("commandPrefix")) {
                this.commandPrefix = data.get("commandPrefix").getAsString();
            }
            if (data.has("configButtonPosition")) {
                final String configButtonPosition = data.get("configButtonPosition").getAsString();
                for (final ConfigButtonPosition value : ConfigButtonPosition.values()) {
                    if (value.getDisplayName().equals(configButtonPosition)) {
                        this.configButtonPosition = value;
                        break;
                    }
                }
            }
            if (data.has("displayCommandSendWarning")) {
                this.displayCommandSendWarning = data.get("displayCommandSendWarning").getAsBoolean();
            }
            if (data.has("displayVanishedWarning")) {
                this.displayVanishedWarning = data.get("displayVanishedWarning").getAsBoolean();
            }
            if (data.has("infiniteCommandInputLength")) {
                this.infiniteCommandInputLength = data.get("infiniteCommandInputLength").getAsBoolean();
            }
        }
    }

    public final void save() throws Exception {
        final JsonObject output = new JsonObject();

        if (this.commandPrefix.isEmpty()) {
            this.commandPrefix = DEFAULT_COMMAND_PREFIX;
        }
        output.addProperty("commandPrefix", this.commandPrefix);
        output.addProperty("configButtonPosition", this.configButtonPosition.getDisplayName());
        output.addProperty("displayCommandSendWarning", this.displayCommandSendWarning);
        output.addProperty("displayVanishedWarning", this.displayVanishedWarning);
        output.addProperty("infiniteCommandInputLength", this.infiniteCommandInputLength);

        Files.writeString(this.file, Configuration.GSON.toJson(output), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public final String getCommandPrefix() {
        return this.commandPrefix;
    }

    public final void setCommandPrefix(final String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public final ConfigButtonPosition getConfigButtonPosition() {
        return this.configButtonPosition;
    }

    public final void setConfigButtonPosition(final ConfigButtonPosition configButtonPosition) {
        this.configButtonPosition = configButtonPosition;
    }

    public final boolean getDisplayCommandSendWarning() {
        return this.displayCommandSendWarning;
    }

    public final void setDisplayCommandSendWarning(final boolean displayCommandSendWarning) {
        this.displayCommandSendWarning = displayCommandSendWarning;
    }

    public final boolean getDisplayVanishedWarning() {
        return this.displayVanishedWarning;
    }

    public final void setDisplayVanishedWarning(final boolean displayVanishedWarning) {
        this.displayVanishedWarning = displayVanishedWarning;
    }

    public final boolean getInfiniteCommandInputLength() {
        return this.infiniteCommandInputLength;
    }

    public final void setInfiniteCommandInputLength(final boolean infiniteCommandInputLength) {
        this.infiniteCommandInputLength = infiniteCommandInputLength;
    }

    public enum ConfigButtonPosition {

        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right");

        private final String displayName;

        ConfigButtonPosition(final String displayName) {
            this.displayName = displayName;
        }

        public final String getDisplayName() {
            return this.displayName;
        }

    }

}
