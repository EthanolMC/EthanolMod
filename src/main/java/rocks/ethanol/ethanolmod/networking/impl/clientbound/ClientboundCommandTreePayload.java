package rocks.ethanol.ethanolmod.networking.impl.clientbound;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rocks.ethanol.ethanolmod.command.CommandTreeReader;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypeRegistry;
import rocks.ethanol.ethanolmod.networking.impl.EthanolPayload;

public class ClientboundCommandTreePayload implements EthanolPayload {

    private static final ArgumentTypeRegistry ARGUMENT_TYPE_REGISTRY = new ArgumentTypeRegistry();

    public static final Type<ClientboundCommandTreePayload> ID = new Type<>(EthanolPayload.createIdentifier("command_tree"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandTreePayload> CODEC = StreamCodec.of(
            (buf, value) -> value.write(buf),
            ClientboundCommandTreePayload::new
    );

    private final RootCommandNode<SharedSuggestionProvider> root;

    public ClientboundCommandTreePayload(final RegistryFriendlyByteBuf buf) {
        this.root = CommandTreeReader.read(buf, ClientboundCommandTreePayload.ARGUMENT_TYPE_REGISTRY);
    }

    @Override
    public final void write(final RegistryFriendlyByteBuf buf) {
        throw EthanolPayload.createReadOnlyException(ClientboundCommandTreePayload.class);
    }

    public final RootCommandNode<SharedSuggestionProvider> getRoot() {
        return this.root;
    }

    @Override
    public final Type<ClientboundCommandTreePayload> type() {
        return ClientboundCommandTreePayload.ID;
    }

}
