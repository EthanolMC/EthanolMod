package rocks.ethanol.ethanolmod.networking.impl.server;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SuggestionsResponseEthanolS2CPayload extends EthanolPayload {

    public static final Id<SuggestionsResponseEthanolS2CPayload> ID = new Id<>(createIdentifier("suggest"));

    public static final PacketCodec<PacketByteBuf, SuggestionsResponseEthanolS2CPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw createReadOnlyException("SuggestionsResponseEthanolS2CPayload");
    }, SuggestionsResponseEthanolS2CPayload::new);

    public SuggestionsResponseEthanolS2CPayload(final PacketByteBuf buf) {
        final Map<Long, CompletableFuture<Suggestions>> pendingRequests = EthanolMod.getInstance().getPendingRequests();
        final long nonce = buf.readLong();
        final CompletableFuture<Suggestions> future = pendingRequests.get(nonce);
        if (future == null) {
            return;
        }
        pendingRequests.remove(nonce);
        if (!buf.readBoolean()) {
            future.completeExceptionally(new IllegalStateException("Server did not provide suggestions"));
            return;
        }
        final StringRange range = StringRange.between(buf.readInt(), buf.readInt());
        final int length = buf.readInt();
        final List<Suggestion> suggestions = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            final short textLength = buf.readShort();
            final byte[] textBytes = new byte[textLength];
            buf.readBytes(textBytes);
            final String text = new String(textBytes, StandardCharsets.UTF_8);
            final String tooltip;
            if (buf.readBoolean()) {
                final short tooltipLength = buf.readShort();
                final byte[] tooltipBytes = new byte[tooltipLength];
                buf.readBytes(tooltipBytes);
                tooltip = new String(tooltipBytes, StandardCharsets.UTF_8);
            } else {
                tooltip = null;
            }
            suggestions.add(new Suggestion(range, text, Text.of(tooltip)));
        }
        future.complete(new Suggestions(range, suggestions));
    }

    @Override
    public Id<SuggestionsResponseEthanolS2CPayload> getId() {
        return ID;
    }

}
