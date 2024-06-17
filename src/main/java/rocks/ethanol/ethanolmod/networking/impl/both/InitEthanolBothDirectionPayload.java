package rocks.ethanol.ethanolmod.networking.impl.both;

import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class InitEthanolBothDirectionPayload extends EthanolPayload {

    public static final Id<InitEthanolBothDirectionPayload> ID = new Id<>(createIdentifier("init"));

    public static final PacketCodec<PacketByteBuf, InitEthanolBothDirectionPayload> CODEC = CustomPayload.codecOf(
            InitEthanolBothDirectionPayload::write,
            InitEthanolBothDirectionPayload::new
    );

    public InitEthanolBothDirectionPayload() {
    }

    public InitEthanolBothDirectionPayload(final PacketByteBuf buf) {
        EthanolMod.getInstance().setInstalled(true);
        EthanolMod.getInstance().setSend(false);
        EthanolMod.getInstance().setShowStart(System.currentTimeMillis());
    }

    @Override
    public Id<InitEthanolBothDirectionPayload> getId() {
        return ID;
    }

}
