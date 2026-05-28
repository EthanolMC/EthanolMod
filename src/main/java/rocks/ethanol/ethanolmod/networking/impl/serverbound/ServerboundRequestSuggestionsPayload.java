package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;

public class ServerboundRequestSuggestionsPayload implements EthanolPayload {

    public static final Type<ServerboundRequestSuggestionsPayload> ID = new Type<>(EthanolPayload.createIdentifier("suggest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestSuggestionsPayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            buf -> { throw EthanolPayload.createWriteOnlyException(ServerboundRequestSuggestionsPayload.class); }
    );

    private final long nonce;
    private final int partialCommandOffset;
    private final String partialCommand;

    public ServerboundRequestSuggestionsPayload(final long nonce, final int partialCommandOffset, final String partialCommand) {
        this.nonce = nonce;
        this.partialCommandOffset = partialCommandOffset;
        this.partialCommand = partialCommand;
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        buf.writeLong(this.nonce);
        buf.writeInt(this.partialCommandOffset);
        buf.writeBytes(this.partialCommand.getBytes(StandardCharsets.UTF_8));
    }

    public final long getNonce() {
        return this.nonce;
    }

    public final int getPartialCommandOffset() {
        return this.partialCommandOffset;
    }

    public final String getPartialCommand() {
        return this.partialCommand;
    }

    @Override
    public final Type<ServerboundRequestSuggestionsPayload> type() {
        return ServerboundRequestSuggestionsPayload.ID;
    }

}
