package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ServerboundAuthResponsePacket implements EthanolPayload {

    public static final Type<ServerboundAuthResponsePacket> ID = new Type<>(EthanolPayload.createIdentifier("auth_res"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAuthResponsePacket> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            buf -> { throw EthanolPayload.createWriteOnlyException(ServerboundAuthResponsePacket.class); }
    );

    private final byte[] verifyToken;

    public ServerboundAuthResponsePacket(final byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        buf.writeBytes(this.verifyToken);
    }

    public final byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public final Type<ServerboundAuthResponsePacket> type() {
        return ServerboundAuthResponsePacket.ID;
    }

}
