package rocks.ethanol.ethanolmod.networking.impl.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class CommandTreeEthanolS2CPayload extends EthanolPayload {

    public static final Id<CommandTreeEthanolS2CPayload> ID = new Id<>(createIdentifier("command_tree"));

    public static final PacketCodec<PacketByteBuf, CommandTreeEthanolS2CPayload> CODEC = CustomPayload.codecOf((value, buf) -> {
        throw createReadOnlyException("CommandTreeEthanolS2CPayload");
    }, CommandTreeEthanolS2CPayload::new);

    public CommandTreeEthanolS2CPayload(final PacketByteBuf buf) {
        EthanolMod.getInstance().updateCommandDispatcher(buf);
    }

    @Override
    public Id<CommandTreeEthanolS2CPayload> getId() {
        return ID;
    }

}
