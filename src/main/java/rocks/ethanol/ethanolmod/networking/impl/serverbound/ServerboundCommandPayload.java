package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.nio.charset.StandardCharsets;

public class ServerboundCommandPayload implements EthanolPayload {

    public static final Id<ServerboundCommandPayload> ID = new Id<>(EthanolPayload.createIdentifier("command"));

    public static final PacketCodec<PacketByteBuf, ServerboundCommandPayload> CODEC = CustomPayload.codecOf(
            ServerboundCommandPayload::write,
            buf -> {
                throw EthanolPayload.createWriteOnlyException(ServerboundCommandPayload.class);
            }
    );

    private final String command;

    public ServerboundCommandPayload(final String command) {
        this.command = command;
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        buf.writeBytes(this.command.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public final Id<ServerboundCommandPayload> getId() {
        return ID;
    }

}
