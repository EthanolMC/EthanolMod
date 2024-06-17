package rocks.ethanol.ethanolmod.networking.impl.server;

import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;

public class MessageEthanolS2CPayload extends EthanolPayload {

    public static final Id<MessageEthanolS2CPayload> ID = new Id<>(createIdentifier("message"));

    public static final PacketCodec<PacketByteBuf, MessageEthanolS2CPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw createReadOnlyException("MessageEthanolS2CPayload");
    }, MessageEthanolS2CPayload::new);

    public MessageEthanolS2CPayload(final PacketByteBuf buf) {
        this.mc.inGameHud.getChatHud().addMessage(Text.of(new String(readBuffer(buf))));
    }

    @Override
    public Id<MessageEthanolS2CPayload> getId() {
        return ID;
    }

}
