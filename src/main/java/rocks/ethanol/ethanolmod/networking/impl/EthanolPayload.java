package rocks.ethanol.ethanolmod.networking.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

public interface EthanolPayload extends CustomPayload, MinecraftWrapper {

    void write(final PacketByteBuf buf);

    static Identifier createIdentifier(final String name) {
        return Identifier.of("ethanol", name);
    }

    static UnsupportedOperationException createWriteOnlyException(final Class<?> clazz) {
        return EthanolPayload.createWriteOnlyException(clazz.getSimpleName());
    }

    static UnsupportedOperationException createWriteOnlyException(final String name) {
        return new UnsupportedOperationException(name.concat("is a write-only packet!"));
    }

    static UnsupportedOperationException createReadOnlyException(final Class<?> clazz) {
        return EthanolPayload.createReadOnlyException(clazz.getSimpleName());
    }

    static UnsupportedOperationException createReadOnlyException(final String name) {
        return new UnsupportedOperationException(name.concat(" is a read-only packet!"));
    }

    static byte[] readBuffer(final ByteBuf buf) {
        final byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

}
