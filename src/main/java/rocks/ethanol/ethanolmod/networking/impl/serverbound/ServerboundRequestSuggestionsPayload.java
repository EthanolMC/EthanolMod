package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;

public class ServerboundRequestSuggestionsPayload implements EthanolPayload {

    public static final Id<ServerboundRequestSuggestionsPayload> ID = new Id<>(EthanolPayload.createIdentifier("suggest"));

    public static final PacketCodec<PacketByteBuf, ServerboundRequestSuggestionsPayload> CODEC = CustomPayload.codecOf(
            ServerboundRequestSuggestionsPayload::write,
            buf -> {
                throw EthanolPayload.createWriteOnlyException(ServerboundRequestSuggestionsPayload.class);
            }
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
    public final void write(final PacketByteBuf buf) {
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
    public final Id<ServerboundRequestSuggestionsPayload> getId() {
        return ServerboundRequestSuggestionsPayload.ID;
    }

}
