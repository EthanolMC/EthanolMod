package rocks.ethanol.ethanolmod.networking.impl.shared;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class InitEthanolSharedPayload extends EthanolPayload {

    public static final Id<InitEthanolSharedPayload> ID = new Id<>(createIdentifier("init"));

    public static final PacketCodec<PacketByteBuf, InitEthanolSharedPayload> CODEC = CustomPayload.codecOf(
            InitEthanolSharedPayload::write,
            InitEthanolSharedPayload::new
    );

    public InitEthanolSharedPayload() {
    }

    public InitEthanolSharedPayload(final PacketByteBuf buf) {
        EthanolMod.getInstance().setInstalled(true);
        EthanolMod.getInstance().setSend(false);
        EthanolMod.getInstance().setShowStart(System.currentTimeMillis());
    }

    @Override
    public Id<InitEthanolSharedPayload> getId() {
        return ID;
    }

}
