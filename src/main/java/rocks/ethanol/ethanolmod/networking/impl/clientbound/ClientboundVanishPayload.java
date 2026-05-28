package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ClientboundVanishPayload implements EthanolPayload {

    public static final Type<ClientboundVanishPayload> ID = new Type<>(EthanolPayload.createIdentifier("vanish"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundVanishPayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            ClientboundVanishPayload::new
    );

    private final boolean vanished;

    public ClientboundVanishPayload(final RegistryFriendlyByteBuf buf) {
        this.vanished = buf.readByte() == 1;
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundVanishPayload.class);
    }

    public final boolean isVanished() {
        return this.vanished;
    }

    @Override
    public final Type<ClientboundVanishPayload> type() {
        return ClientboundVanishPayload.ID;
    }

}
