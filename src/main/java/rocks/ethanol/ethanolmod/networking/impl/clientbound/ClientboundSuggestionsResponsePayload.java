package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientboundSuggestionsResponsePayload implements EthanolPayload {

    public static final Id<ClientboundSuggestionsResponsePayload> ID = new Id<>(EthanolPayload.createIdentifier("suggest"));

    public static final PacketCodec<PacketByteBuf, ClientboundSuggestionsResponsePayload> CODEC = CustomPayload.codecOf(ClientboundSuggestionsResponsePayload::write, ClientboundSuggestionsResponsePayload::new);

    private final long nonce;
    private final Suggestions suggestions;

    public ClientboundSuggestionsResponsePayload(final PacketByteBuf buf) {
        this.nonce = buf.readLong();

        if (!buf.readBoolean()) {
            this.suggestions = null;
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

        this.suggestions = new Suggestions(range, suggestions);
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundSuggestionsResponsePayload.class);
    }

    public final long getNonce() {
        return this.nonce;
    }

    public final Suggestions getSuggestions() {
        return this.suggestions;
    }

    @Override
    public final Id<ClientboundSuggestionsResponsePayload> getId() {
        return ClientboundSuggestionsResponsePayload.ID;
    }

}
