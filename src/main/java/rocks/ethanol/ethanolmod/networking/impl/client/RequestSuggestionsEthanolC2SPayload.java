package rocks.ethanol.ethanolmod.networking.impl.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;

public class RequestSuggestionsEthanolC2SPayload extends EthanolPayload {

    public static final Id<RequestSuggestionsEthanolC2SPayload> ID = new Id<>(createIdentifier("suggest"));

    public static final PacketCodec<PacketByteBuf, RequestSuggestionsEthanolC2SPayload> CODEC = CustomPayload.codecOf(
            RequestSuggestionsEthanolC2SPayload::write,
            buf -> {
                throw createWriteOnlyException("RequestSuggestionsEthanolC2SPayload");
            }
    );

    private final long nonce;
    private final int partialCommandOffset;
    private final String partialCommand;

    public RequestSuggestionsEthanolC2SPayload(final long nonce, final int partialCommandOffset, final String partialCommand) {
        this.nonce = nonce;
        this.partialCommandOffset = partialCommandOffset;
        this.partialCommand = partialCommand;
    }

    @Override
    protected void write(final PacketByteBuf buf) {
        buf.writeLong(this.nonce);
        buf.writeInt(this.partialCommandOffset);
        buf.writeBytes(this.partialCommand.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Id<RequestSuggestionsEthanolC2SPayload> getId() {
        return ID;
    }

}
