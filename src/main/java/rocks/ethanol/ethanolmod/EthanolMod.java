package rocks.ethanol.ethanolmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.ethanol.ethanolmod.config.Configuration;
import rocks.ethanol.ethanolmod.eventhandler.EventInitializer;
import rocks.ethanol.ethanolmod.networking.PayloadInitializer;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EthanolMod implements ClientModInitializer, MinecraftWrapper {

    public static final String NAME = "Ethanol Mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(EthanolMod.class);

    private static EthanolMod instance;

    private Configuration configuration;
    private boolean installed;
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

        {
            final Path configDir = this.mc.runDirectory.toPath().resolve("config");
            if (!Files.exists(configDir)) {
                try {
                    Files.createDirectories(configDir);
                } catch (final IOException exception) {
                    throw new RuntimeException(exception);
                }
            }

            this.configuration = new Configuration(configDir.resolve("ethanolmod.json"));
        }

        try {
            this.configuration.load();
        } catch (final IOException exception) {
            EthanolMod.LOGGER.error("Failed to load config!", exception);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.configuration.save();
            } catch (final Exception exception) {
                EthanolMod.LOGGER.error("Failed to save config!", exception);
            }
        }));

        EventInitializer.init();
        PayloadInitializer.init();
    }

    public static EthanolMod getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return this.configuration;
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

}
