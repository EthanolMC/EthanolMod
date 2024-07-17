package rocks.ethanol.ethanolmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPairs;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.eventhandler.EventInitializer;
import rocks.ethanol.ethanolmod.networking.PayloadInitializer;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EthanolMod implements ClientModInitializer, MinecraftWrapper {

    public static final String NAME = "Ethanol Mod";
    public static final String ID = "ethanolmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(EthanolMod.class);

    private static EthanolMod instance;

    private Configuration configuration;
    private AuthKeyPairs authKeyPairs;
    private AuthOptions authOptions;
    private boolean installed;
    private boolean authEnabled;
    private boolean send;
    private boolean vanished;
    private long showStart;

    private final CommandSource commandSource;
    private final Map<Long, CompletableFuture<Suggestions>> pendingRequests;
    private CommandDispatcher<CommandSource> commandDispatcher;

    public EthanolMod() {
        this.installed = false;
        this.send = false;
        this.vanished = false;
        this.showStart = 0L;
        this.commandSource = new ClientCommandSource(null, MinecraftClient.getInstance());
        this.pendingRequests = new HashMap<>();
    }

    @Override
    public void onInitializeClient() {
        EthanolMod.instance = this;

        final Path directory = this.mc.runDirectory.toPath().resolve(EthanolMod.ID);
        if (!Files.isDirectory(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (final Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        { // configuration
            this.configuration = new Configuration(directory.resolve("config.json"));

            try {
                this.configuration.load();
            } catch (final Exception exception) {
                EthanolMod.LOGGER.error("Failed to load config!", exception);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    this.configuration.save();
                } catch (final Exception exception) {
                    EthanolMod.LOGGER.error("Failed to save config!", exception);
                }
            }));
        }

        { // auth
            final Path authDirectory = directory.resolve("auth");
            if (!Files.isDirectory(authDirectory)) {
                try {
                    Files.createDirectories(authDirectory);
                } catch (final Exception exception) {
                    throw new RuntimeException(exception);
                }
            }

            { // auth key pairs
                final Path authKeyPairsPath = authDirectory.resolve("key-pairs");
                if (!Files.isDirectory(authKeyPairsPath)) {
                    try {
                        Files.createDirectories(authKeyPairsPath);
                    } catch (final Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }

                this.authKeyPairs = new AuthKeyPairs(authKeyPairsPath);

                try {
                    this.authKeyPairs.load();
                } catch (final Exception exception) {
                    EthanolMod.LOGGER.error("Failed to load auth key pairs!", exception);
                }

                try {
                    this.authKeyPairs.watch();
                } catch (final Exception exception) {
                    EthanolMod.LOGGER.error("Failed to start auth key pair watcher!", exception);
                }
            }

            { // configuration
                this.authOptions = new AuthOptions(authDirectory.resolve("options.json"));

                try {
                    this.authOptions.load();
                } catch (final Exception exception) {
                    EthanolMod.LOGGER.error("Failed to load auth options!", exception);
                }

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        this.authOptions.save();
                    } catch (final Exception exception) {
                        EthanolMod.LOGGER.error("Failed to save auth options!", exception);
                    }
                }));
            }

        }

        EventInitializer.init();
        PayloadInitializer.init();
    }

    public static EthanolMod getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public final AuthOptions getAuthOptions() {
        return this.authOptions;
    }

    public final AuthKeyPairs getAuthKeyPairs() {
        return this.authKeyPairs;
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public void setInstalled(final boolean installed) {
        this.installed = installed;
    }

    public boolean hasSend() {
        return this.send;
    }

    public void setSend(final boolean send) {
        this.send = send;
    }

    public boolean isVanished() {
        return this.vanished;
    }

    public void setVanished(final boolean vanished) {
        this.vanished = vanished;
    }

    public long getShowStart() {
        return this.showStart;
    }

    public void setShowStart(final long showStart) {
        this.showStart = showStart;
    }

    public CommandSource getCommandSource() {
        return this.commandSource;
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public void updateCommandDispatcher(final CommandDispatcher<CommandSource> dispatcher) {
        this.commandDispatcher = dispatcher;
    }

    public void resetCommandDispatcher() {
        this.commandDispatcher = null;
    }

    public Map<Long, CompletableFuture<Suggestions>> getPendingRequests() {
        return this.pendingRequests;
    }

    public final boolean isAuthEnabled() {
        return this.authEnabled;
    }

    public final void setAuthEnabled(final boolean authEnabled) {
        this.authEnabled = authEnabled;
    }
}
