package rocks.ethanol.ethanolmod.auth;

import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class AuthOptions {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;
    private Mode mode;
    private Set<String> knownHosts;

    public AuthOptions(final Path file) {
        this.file = file;
        this.mode = Mode.SEMI_AUTOMATIC;
        this.knownHosts = new HashSet<>();
    }

    public final void load() throws IOException {
        if (!Files.exists(this.file)) {
            return;
        }

        try (final Reader reader = Files.newBufferedReader(this.file)) {
            final JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
            if (data.has("mode")) {
                try {
                    this.mode = Mode.valueOf(data.get("mode").getAsString());
                } catch (final IllegalArgumentException ignored) { }
            }

            if (data.has("knownHosts")) {
                final JsonArray array = data.get("knownHosts").getAsJsonArray();
                this.knownHosts = new HashSet<>();
                for (final JsonElement element : array) {
                    this.knownHosts.add(element.getAsString());
                }
            }
        }
    }

    public final void save() throws IOException {
        final JsonObject output = new JsonObject();

        output.addProperty("mode", this.mode.name());
        {
            final JsonArray array = new JsonArray(this.knownHosts.size());
            for (final String knownHost : this.knownHosts) {
                array.add(knownHost);
            }
            output.add("knownHosts", array);
        }

        Files.writeString(this.file, AuthOptions.GSON.toJson(output), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public final Mode getMode() {
        return this.mode;
    }

    public final void setMode(final Mode mode) {
        this.mode = mode;
    }

    public final Set<String> getKnownHosts() {
        return this.knownHosts;
    }

    public enum Mode {
        MANUAL("Manual", "Requires you to manually authenticate everytime you join a server."),
        SEMI_AUTOMATIC("Semi Automatic", "Remembers servers where authentication succeeded and tries to authenticate automatically on join."),
        AUTOMATIC("Automatic", "Tries to authenticate automatically on every server you join.");

        private final String displayName;
        private final String description;

        Mode(final String displayName, final String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public final String getDisplayName() {
            return this.displayName;
        }

        public final String getDescription() {
            return this.description;
        }
    }
}
