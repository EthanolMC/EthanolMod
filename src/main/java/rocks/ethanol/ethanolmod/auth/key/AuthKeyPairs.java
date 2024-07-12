package rocks.ethanol.ethanolmod.auth.key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class AuthKeyPairs {

    private List<AuthKeyPair> keyPairs;
    private final Path directory;
    private final ExecutorService service;
    private boolean watching;

    public AuthKeyPairs(final Path directory) {
        this.keyPairs = new CopyOnWriteArrayList<>();
        this.directory = directory;
        this.service = Executors.newSingleThreadExecutor();
        this.watching = false;
    }

    public final void watch() throws IOException {
        if (this.watching) {
            throw new IllegalStateException("Already watching!");
        }
        this.watching = true;

        final WatchService watchService = this.directory.getFileSystem().newWatchService();
        this.directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

        this.service.execute(() -> {
            while (Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted() && !this.service.isShutdown()) {
                final WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    return;
                }
                if (!key.pollEvents().isEmpty()) {
                    try {
                        this.load();
                    } catch (final IOException ignored) { }
                }

                if (!key.reset()) {
                    break;
                }
            }
        });
    }

    public final void load() throws IOException {
        final List<AuthKeyPair> newKeyPairs = new CopyOnWriteArrayList<>();
        try (final Stream<Path> stream = Files.walk(this.directory, 1)) {
            for (final Path keyFile : stream.filter(Files::isRegularFile).toList()) {
                try {
                    newKeyPairs.add(AuthKeyPair.fromFile(keyFile));
                } catch (final IOException ignored) { }
            }
        }
        this.keyPairs = newKeyPairs;
    }

    public final AuthKeyPair getByHash(final byte[] hash) {
        return this.keyPairs.stream().filter(keyPair -> Arrays.equals(keyPair.hash(), hash)).findFirst().orElse(null);
    }

    public final Path getDirectory() {
        return this.directory;
    }

    public final List<AuthKeyPair> getKeyPairs() {
        return this.keyPairs;
    }
}
