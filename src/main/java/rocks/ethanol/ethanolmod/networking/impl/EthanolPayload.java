package rocks.ethanol.ethanolmod.networking.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public interface EthanolPayload extends CustomPacketPayload, MinecraftWrapper {

    void write(final RegistryFriendlyByteBuf buf);

    static Identifier createIdentifier(final String name) {
        return Identifier.fromNamespaceAndPath("ethanol", name);
    }

    static UnsupportedOperationException createWriteOnlyException(final Class<?> clazz) {
        return EthanolPayload.createWriteOnlyException(clazz.getSimpleName());
    }

    static UnsupportedOperationException createWriteOnlyException(final String name) {
        return new UnsupportedOperationException(name.concat(" is a write-only packet!"));
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
