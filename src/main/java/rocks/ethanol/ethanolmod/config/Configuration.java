package rocks.ethanol.ethanolmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
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

    private final Path file;
    private String commandPrefix;
    private ConfigButtonPosition configButtonPosition;
    private boolean displayCommandSendWarning;
    private boolean displayVanishedWarning;

    public Configuration(final Path file) {
        this.file = file;
        this.commandPrefix = Configuration.DEFAULT_COMMAND_PREFIX;
        this.configButtonPosition = Configuration.DEFAULT_BUTTON_POSITION;
        this.displayCommandSendWarning = Configuration.DEFAULT_DISPLAY_COMMAND_SEND_WARNING;
        this.displayVanishedWarning = Configuration.DEFAULT_DISPLAY_VANISHED_WARNING;
    }

    public final void load() throws IOException {
        if (!Files.exists(this.file)) {
            return;
        }

        try (final Reader reader = Files.newBufferedReader(this.file)) {
            final JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonNull()) {
                final JsonObject configObject = jsonElement.getAsJsonObject();
                if (configObject.has("commandPrefix")) {
                    this.commandPrefix = configObject.get("commandPrefix").getAsString();
                }
                if (configObject.has("configButtonPosition")) {
                    final String configButtonPosition = configObject.get("configButtonPosition").getAsString();
                    for (final ConfigButtonPosition value : ConfigButtonPosition.values()) {
                        if (value.getName().equals(configButtonPosition)) {
                            this.configButtonPosition = value;
                            break;
                        }
                    }
                }
                if (configObject.has("displayCommandSendWarning")) {
                    this.displayCommandSendWarning = configObject.get("displayCommandSendWarning").getAsBoolean();
                }
                if (configObject.has("displayVanishedWarning")) {
                    this.displayVanishedWarning = configObject.get("displayVanishedWarning").getAsBoolean();
                }
            }
        }
    }

    public final void save() throws Exception {
        final JsonObject configObject = new JsonObject();
        if (this.commandPrefix.isEmpty()) {
            this.commandPrefix = DEFAULT_COMMAND_PREFIX;
        }
        configObject.addProperty("commandPrefix", this.commandPrefix);
        configObject.addProperty("configButtonPosition", this.configButtonPosition.getName());
        configObject.addProperty("displayCommandSendWarning", this.displayCommandSendWarning);
        configObject.addProperty("displayVanishedWarning", this.displayVanishedWarning);
        Files.writeString(this.file, Configuration.GSON.toJson(configObject), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

    public enum ConfigButtonPosition {

        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right");

        private final String name;

        ConfigButtonPosition(final String name) {
            this.name = name;
        }

        public final String getName() {
            return this.name;
        }

    }

}
