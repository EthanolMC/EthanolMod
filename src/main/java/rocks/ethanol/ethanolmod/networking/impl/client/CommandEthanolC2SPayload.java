package rocks.ethanol.ethanolmod.networking.impl.client;

import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.nio.charset.StandardCharsets;

public class CommandEthanolC2SPayload extends EthanolPayload {

    public static final Id<CommandEthanolC2SPayload> ID = new Id<>(createIdentifier("command"));

    public static final PacketCodec<PacketByteBuf, CommandEthanolC2SPayload> CODEC = CustomPayload.codecOf(
            CommandEthanolC2SPayload::write,
            buf -> {
                throw createWriteOnlyException("CommandEthanolC2SPayload");
            }
    );

    private final String command;

    public CommandEthanolC2SPayload(final String command) {
        this.command = command;
    }

    @Override
    protected void write(final PacketByteBuf buf) {
        buf.writeBytes(this.command.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Id<CommandEthanolC2SPayload> getId() {
        return ID;
    }

}
