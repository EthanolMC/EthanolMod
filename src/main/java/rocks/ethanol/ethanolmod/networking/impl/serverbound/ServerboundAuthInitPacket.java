package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ServerboundAuthInitPacket implements EthanolPayload {

    public static final CustomPayload.Id<ServerboundAuthInitPacket> ID = new CustomPayload.Id<>(EthanolPayload.createIdentifier("auth_init"));

    public static final PacketCodec<PacketByteBuf, ServerboundAuthInitPacket> CODEC = CustomPayload.codecOf(
            ServerboundAuthInitPacket::write,
            buf -> {
                throw EthanolPayload.createWriteOnlyException(ServerboundAuthInitPacket.class);
            }
    );

    private final byte[][] publicKeyHashes;

    public ServerboundAuthInitPacket(final byte[][] publicKeyHashes) {
        this.publicKeyHashes = publicKeyHashes;
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        for (final byte[] publicKeyHash : publicKeyHashes) {
            buf.writeBytes(publicKeyHash);
        }
    }

    public final byte[][] getPublicKeyHashes() {
        return this.publicKeyHashes;
    }

    @Override
    public final CustomPayload.Id<ServerboundAuthInitPacket> getId() {
        return ServerboundAuthInitPacket.ID;
    }

}
