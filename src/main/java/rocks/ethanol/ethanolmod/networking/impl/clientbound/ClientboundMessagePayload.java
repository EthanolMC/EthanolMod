package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.nio.charset.StandardCharsets;

public class ClientboundMessagePayload implements EthanolPayload {

    public static final Id<ClientboundMessagePayload> ID = new Id<>(EthanolPayload.createIdentifier("message"));

    public static final PacketCodec<PacketByteBuf, ClientboundMessagePayload> CODEC = CustomPayload.codecOf(ClientboundMessagePayload::write, ClientboundMessagePayload::new);

    private final String message;

    public ClientboundMessagePayload(final PacketByteBuf buf) {
        this.message = new String(EthanolPayload.readBuffer(buf), StandardCharsets.UTF_8);
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundMessagePayload.class);
    }

    public final String getMessage() {
        return this.message;
    }

    @Override
    public final Id<ClientboundMessagePayload> getId() {
        return ID;
    }

}
