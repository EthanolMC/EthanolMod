package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ServerboundAuthResponsePacket implements EthanolPayload {

    public static final Id<ServerboundAuthResponsePacket> ID = new Id<>(EthanolPayload.createIdentifier("auth_res"));

    public static final PacketCodec<PacketByteBuf, ServerboundAuthResponsePacket> CODEC = CustomPayload.codecOf(
            ServerboundAuthResponsePacket::write,
            buf -> {
                throw EthanolPayload.createWriteOnlyException(ServerboundAuthResponsePacket.class);
            }
    );

    private final byte[] verifyToken;

    public ServerboundAuthResponsePacket(final byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        buf.writeBytes(this.verifyToken);
    }

    public final byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public final Id<ServerboundAuthResponsePacket> getId() {
        return ServerboundAuthResponsePacket.ID;
    }

}
