package rocks.ethanol.ethanolmod.command.argumenttypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.client.RequestSuggestionsEthanolC2SPayload;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface ArgumentTypes extends MinecraftWrapper {

    static CompletableFuture<Suggestions> suggestMatching(final Collection<String> suggestions, final SuggestionsBuilder builder) {
        final String input = builder.getRemaining();
        for (final String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                builder.suggest(suggestion);
            }
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    static String readRawWord(final StringReader reader) {
        final int start = reader.getCursor();

        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    static CompletableFuture<Suggestions> requestServerSuggestions(final SuggestionsBuilder builder) {
        final long nonce = System.nanoTime();
        final CompletableFuture<Suggestions> future = new CompletableFuture<>();
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        ethanolMod.getPendingRequests().put(nonce, future);
        final int partialCommandOffset = ethanolMod.getConfigManager().getCommandPrefix().length();
        mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new RequestSuggestionsEthanolC2SPayload(nonce, partialCommandOffset, builder.getInput())));
        return future;
    }

}