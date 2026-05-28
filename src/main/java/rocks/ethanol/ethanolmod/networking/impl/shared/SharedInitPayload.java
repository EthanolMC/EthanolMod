package rocks.ethanol.ethanolmod.networking.impl.shared;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class SharedInitPayload implements EthanolPayload {

    public static final Type<SharedInitPayload> ID = new Type<>(EthanolPayload.createIdentifier("init"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SharedInitPayload> CODEC = StreamCodec.unit(new SharedInitPayload());

    public SharedInitPayload() { }

    public SharedInitPayload(final RegistryFriendlyByteBuf buf) { }

    @Override
    public void write(final RegistryFriendlyByteBuf buf) { }

    @Override
    public Type<SharedInitPayload> type() {
        return ID;
    }

}
