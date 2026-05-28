package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientboundSuggestionsResponsePayload implements EthanolPayload {

    public static final Type<ClientboundSuggestionsResponsePayload> ID = new Type<>(EthanolPayload.createIdentifier("suggest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSuggestionsResponsePayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            ClientboundSuggestionsResponsePayload::new
    );

    private final long nonce;
    private final Suggestions suggestions;

    public ClientboundSuggestionsResponsePayload(final RegistryFriendlyByteBuf buf) {
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
            suggestions.add(new Suggestion(range, text, tooltip == null ? null : Component.literal(tooltip)));
        }

        this.suggestions = new Suggestions(range, suggestions);
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundSuggestionsResponsePayload.class);
    }

    public final long getNonce() {
        return this.nonce;
    }

    public final Suggestions getSuggestions() {
        return this.suggestions;
    }

    @Override
    public final Type<ClientboundSuggestionsResponsePayload> type() {
        return ClientboundSuggestionsResponsePayload.ID;
    }

}
