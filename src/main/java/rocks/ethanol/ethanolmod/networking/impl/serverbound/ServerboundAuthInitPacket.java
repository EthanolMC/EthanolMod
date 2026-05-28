package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ServerboundAuthInitPacket implements EthanolPayload {

    public static final Type<ServerboundAuthInitPacket> ID = new Type<>(EthanolPayload.createIdentifier("auth_init"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAuthInitPacket> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            buf -> { throw EthanolPayload.createWriteOnlyException(ServerboundAuthInitPacket.class); }
    );

    private final byte[][] publicKeyHashes;

    public ServerboundAuthInitPacket(final byte[][] publicKeyHashes) {
        this.publicKeyHashes = publicKeyHashes;
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        for (final byte[] publicKeyHash : publicKeyHashes) {
            buf.writeBytes(publicKeyHash);
        }
    }

    public final byte[][] getPublicKeyHashes() {
        return this.publicKeyHashes;
    }

    @Override
    public final Type<ServerboundAuthInitPacket> type() {
        return ServerboundAuthInitPacket.ID;
    }

}
