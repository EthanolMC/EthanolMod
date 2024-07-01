package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import rocks.ethanol.ethanolmod.command.CommandTreeReader;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypeRegistry;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ClientboundCommandTreePayload implements EthanolPayload {

    private static final ArgumentTypeRegistry ARGUMENT_TYPE_REGISTRY = new ArgumentTypeRegistry();

    public static final Id<ClientboundCommandTreePayload> ID = new Id<>(EthanolPayload.createIdentifier("command_tree"));

    public static final PacketCodec<PacketByteBuf, ClientboundCommandTreePayload> CODEC = CustomPayload.codecOf(ClientboundCommandTreePayload::write, ClientboundCommandTreePayload::new);

    private final RootCommandNode<CommandSource> root;

    public ClientboundCommandTreePayload(final PacketByteBuf buf) {
        this.root = CommandTreeReader.read(buf, ClientboundCommandTreePayload.ARGUMENT_TYPE_REGISTRY);
    }

    @Override
    public final void write(final PacketByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundCommandTreePayload.class);
    }

    public final RootCommandNode<CommandSource> getRoot() {
        return this.root;
    }

    @Override
    public final Id<ClientboundCommandTreePayload> getId() {
        return ID;
    }

}
