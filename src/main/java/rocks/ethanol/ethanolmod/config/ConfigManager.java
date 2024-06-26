package rocks.ethanol.ethanolmod.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File configFile;

    public static final String DEFAULT_COMMAND_PREFIX = "-";

    private String commandPrefix = DEFAULT_COMMAND_PREFIX;

    public static final ConfigButtonPosition DEFAULT_BUTTON_POSITION = ConfigButtonPosition.BOTTOM_LEFT;

    private ConfigButtonPosition configButtonPosition = DEFAULT_BUTTON_POSITION;

    public ConfigManager(final File configDir) {
        this.configFile = new File(configDir, "ethanolmod.json");
    }

    public void load() throws Exception {
        if (!this.configFile.exists()) {
            return;
        }
        final FileReader fileReader = new FileReader(this.configFile);
        final JsonReader jsonReader = new JsonReader(fileReader);
        final JsonElement jsonElement = JsonParser.parseReader(jsonReader);
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
        }
        jsonReader.close();
        fileReader.close();
    }

    public void save() throws Exception {
        if (this.configFile.exists()) {
            this.configFile.delete();
        }
        this.configFile.createNewFile();
        final FileWriter fileWriter = new FileWriter(this.configFile);
        final PrintWriter printWriter = new PrintWriter(fileWriter);
        final JsonObject configObject = new JsonObject();
        if (this.commandPrefix.isEmpty()) {
            this.commandPrefix = DEFAULT_COMMAND_PREFIX;
        }
        configObject.addProperty("commandPrefix", this.commandPrefix);
        configObject.addProperty("configButtonPosition", this.configButtonPosition.getName());
        printWriter.println(GSON.toJson(configObject));
        printWriter.close();
        fileWriter.close();
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public void setCommandPrefix(final String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public ConfigButtonPosition getConfigButtonPosition() {
        return this.configButtonPosition;
    }

    public void setConfigButtonPosition(final ConfigButtonPosition configButtonPosition) {
        this.configButtonPosition = configButtonPosition;
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

        public String getName() {
            return this.name;
        }

    }

}
