package rocks.ethanol.ethanolmod.networking.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

public abstract class EthanolPayload implements CustomPayload, MinecraftWrapper {

    protected static Identifier createIdentifier(final String name) {
        return Identifier.of("ethanol", name);
    }

    protected static UnsupportedOperationException createWriteOnlyException(final String name) {
        return new UnsupportedOperationException(name + " is a write-only packet!");
    }

    protected void write(final PacketByteBuf buf) {
    }

    protected static UnsupportedOperationException createReadOnlyException(final String name) {
        return new UnsupportedOperationException(name + " is a read-only packet!");
    }

    protected static byte[] readBuffer(final ByteBuf buf) {
        final byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

}
