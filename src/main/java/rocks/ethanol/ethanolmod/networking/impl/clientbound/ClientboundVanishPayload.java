package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ClientboundVanishPayload implements EthanolPayload {

    public static final Id<ClientboundVanishPayload> ID = new Id<>(EthanolPayload.createIdentifier("vanish"));

    public static final PacketCodec<PacketByteBuf, ClientboundVanishPayload> CODEC = CustomPayload.codecOf(ClientboundVanishPayload::write, ClientboundVanishPayload::new);

    private final boolean vanished;

    public ClientboundVanishPayload(final PacketByteBuf buf) {
        this.vanished = buf.readByte() == 1;
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundVanishPayload.class);
    }

    public final boolean isVanished() {
        return this.vanished;
    }

    @Override
    public final Id<ClientboundVanishPayload> getId() {
        return ID;
    }

}
