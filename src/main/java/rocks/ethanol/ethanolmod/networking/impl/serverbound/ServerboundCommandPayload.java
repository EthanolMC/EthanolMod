package rocks.ethanol.ethanolmod.networking.impl.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

import java.nio.charset.StandardCharsets;

public class ServerboundCommandPayload implements EthanolPayload {

    public static final Type<ServerboundCommandPayload> ID = new Type<>(EthanolPayload.createIdentifier("command"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCommandPayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            buf -> { throw EthanolPayload.createWriteOnlyException(ServerboundCommandPayload.class); }
    );

    private final String command;

    public ServerboundCommandPayload(final String command) {
        this.command = command;
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        buf.writeBytes(this.command.getBytes(StandardCharsets.UTF_8));
    }

    public final String getCommand() {
        return this.command;
    }

    @Override
    public final Type<ServerboundCommandPayload> type() {
        return ServerboundCommandPayload.ID;
    }

}
