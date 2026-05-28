package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;

public class ClientboundMessagePayload implements EthanolPayload {

    public static final Type<ClientboundMessagePayload> ID = new Type<>(EthanolPayload.createIdentifier("message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMessagePayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            ClientboundMessagePayload::new
    );

    private final String message;

    public ClientboundMessagePayload(final RegistryFriendlyByteBuf buf) {
        this.message = new String(EthanolPayload.readBuffer(buf), StandardCharsets.UTF_8);
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundMessagePayload.class);
    }

    public final String getMessage() {
        return this.message;
    }

    @Override
    public final Type<ClientboundMessagePayload> type() {
        return ClientboundMessagePayload.ID;
    }

}
