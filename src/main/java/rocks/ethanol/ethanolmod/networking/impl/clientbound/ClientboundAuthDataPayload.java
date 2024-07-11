package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import rocks.ethanol.ethanolmod.utils.HashUtil;

public class ClientboundAuthDataPayload implements EthanolPayload {

    public static final Id<ClientboundAuthDataPayload> ID = new Id<>(EthanolPayload.createIdentifier("auth_data"));

    public static final PacketCodec<PacketByteBuf, ClientboundAuthDataPayload> CODEC = CustomPayload.codecOf(ClientboundAuthDataPayload::write, ClientboundAuthDataPayload::new);

    private final byte[] publicKeyHash;
    private final byte[] encryptedVerifyToken;

    public ClientboundAuthDataPayload(final PacketByteBuf buf) {
        this.publicKeyHash = new byte[HashUtil.SHA_256_SIZE];
        buf.readBytes(this.publicKeyHash);
        this.encryptedVerifyToken = new byte[buf.readShort()];
        buf.readBytes(this.encryptedVerifyToken);
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundAuthDataPayload.class);
    }

    public final byte[] getPublicKeyHash() {
        return this.publicKeyHash;
    }

    public final byte[] getEncryptedVerifyToken() {
        return this.encryptedVerifyToken;
    }

    @Override
    public final Id<ClientboundAuthDataPayload> getId() {
        return ClientboundAuthDataPayload.ID;
    }

}
