package rocks.ethanol.ethanolmod.networking.impl.shared;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class SharedInitPayload implements EthanolPayload {

    public static final Id<SharedInitPayload> ID = new Id<>(EthanolPayload.createIdentifier("init"));

    public static final PacketCodec<PacketByteBuf, SharedInitPayload> CODEC = CustomPayload.codecOf(
            SharedInitPayload::write,
            SharedInitPayload::new
    );

    public SharedInitPayload() { }

    public SharedInitPayload(final PacketByteBuf buf) { }

    @Override
    public void write(final PacketByteBuf buf) { }

    @Override
    public Id<SharedInitPayload> getId() {
        return ID;
    }

}
