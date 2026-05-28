package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import rocks.ethanol.ethanolmod.utils.HashUtil;

public class ClientboundAuthDataPayload implements EthanolPayload {

    public static final Type<ClientboundAuthDataPayload> ID = new Type<>(EthanolPayload.createIdentifier("auth_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAuthDataPayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            ClientboundAuthDataPayload::new
    );

    private final byte[] publicKeyHash;
    private final byte[] encryptedVerifyToken;

    public ClientboundAuthDataPayload(final RegistryFriendlyByteBuf buf) {
        this.publicKeyHash = new byte[HashUtil.SHA_256_SIZE];
        buf.readBytes(this.publicKeyHash);
        this.encryptedVerifyToken = new byte[buf.readShort()];
        buf.readBytes(this.encryptedVerifyToken);
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundAuthDataPayload.class);
    }

    public final byte[] getPublicKeyHash() {
        return this.publicKeyHash;
    }

    public final byte[] getEncryptedVerifyToken() {
        return this.encryptedVerifyToken;
    }

    @Override
    public final Type<ClientboundAuthDataPayload> type() {
        return ClientboundAuthDataPayload.ID;
    }

}
