package rocks.ethanol.ethanolmod.networking.impl.server;

import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class VanishEthanolS2CPayload extends EthanolPayload {

    public static final Id<VanishEthanolS2CPayload> ID = new Id<>(createIdentifier("vanish"));

    public static final PacketCodec<PacketByteBuf, VanishEthanolS2CPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw createReadOnlyException("VanishEthanolS2CPayload");
    }, VanishEthanolS2CPayload::new);

    public VanishEthanolS2CPayload(final PacketByteBuf buf) {
        EthanolMod.getInstance().setVanished(buf.readByte() == 1);
    }

    @Override
    public Id<VanishEthanolS2CPayload> getId() {
        return ID;
    }

}
