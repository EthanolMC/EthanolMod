package rocks.ethanol.ethanolmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.ethanol.ethanolmod.command.CommandTreeReader;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypeRegistry;
import rocks.ethanol.ethanolmod.config.ConfigManager;
import rocks.ethanol.ethanolmod.eventhandler.EventInitializer;
import rocks.ethanol.ethanolmod.networking.PayloadInitializer;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EthanolMod implements ClientModInitializer, MinecraftWrapper {

    private static EthanolMod instance;

    private String name, id;
    private Logger logger;
    private ConfigManager configManager;

    private boolean installed = false, send = false, vanished = false;
    private long showStart = 0L;

    private final CommandSource commandSource = new ClientCommandSource(null, MinecraftClient.getInstance());
    private final String commandSecret = UUID.randomUUID().toString();
    private final ArgumentTypeRegistry argumentTypeRegistry = new ArgumentTypeRegistry();
    private final Map<Long, CompletableFuture<Suggestions>> pendingRequests = new HashMap<>();

    private CommandDispatcher<CommandSource> commandDispatcher;

    @Override
    public void onInitializeClient() {
        instance = this;

        this.name = "Ethanol Mod";
        this.id = this.name.toLowerCase().replace(" ", "");
        this.logger = LoggerFactory.getLogger(this.name);

        final File configDir = new File(this.mc.runDirectory, "config");
        if (!configDir.exists()) configDir.mkdirs();

        this.configManager = new ConfigManager(configDir);

        try {
            this.configManager.load();
        } catch (final Exception e) {
            this.logger.error("Failed to load config!", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.configManager.save();
            } catch (final Exception e) {
                this.logger.error("Failed to save config!", e);
            }
        }));

        EventInitializer.onInit();
        PayloadInitializer.onInit();
    }

    public static EthanolMod getInstance() {
        return instance;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
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

    public String getCommandSecret() {
        return this.commandSecret;
    }

    public ArgumentTypeRegistry getArgumentTypeRegistry() {
        return this.argumentTypeRegistry;
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public void updateCommandDispatcher(final PacketByteBuf buf) {
        this.commandDispatcher = new CommandDispatcher<>(new CommandTreeReader(this.argumentTypeRegistry, buf).getCommandTree(this.argumentTypeRegistry));
    }

    public void resetCommandDispatcher() {
        this.commandDispatcher = null;
    }

    public Map<Long, CompletableFuture<Suggestions>> getPendingRequests() {
        return this.pendingRequests;
    }

}
