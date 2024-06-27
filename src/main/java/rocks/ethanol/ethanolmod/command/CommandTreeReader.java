package rocks.ethanol.ethanolmod.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.CommandSource;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypeRegistry;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CommandTreeReader {

    private final int rootSize;
    private final List<CommandTreeReader.CommandNodeData> nodes;

    public CommandTreeReader(final ArgumentTypeRegistry argumentTypeRegistry, final ByteBuf buf) {
        this.nodes = readList(buf, buf2 -> CommandTreeReader.readCommandNode(argumentTypeRegistry, buf));
        this.rootSize = buf.readInt();
        CommandTreeReader.validate(this.nodes);
    }

    public final RootCommandNode<CommandSource> getCommandTree(final ArgumentTypeRegistry argumentTypeRegistry) {
        return (RootCommandNode<CommandSource>) new CommandTreeReader.CommandTree(argumentTypeRegistry, this.nodes).getNode(this.rootSize);
    }

    private static void validate(final List<CommandTreeReader.CommandNodeData> nodeData, final BiPredicate<CommandTreeReader.CommandNodeData, IntSet> validator) {
        IntSet intSet = new IntOpenHashSet(IntSets.fromTo(0, nodeData.size()));

        while (!intSet.isEmpty()) {
            boolean bl = intSet.removeIf(i -> validator.test(nodeData.get(i), intSet));
            if (!bl) {
                throw new IllegalStateException("Server sent an impossible command tree");
            }
        }
    }

    private static void validate(final List<CommandTreeReader.CommandNodeData> nodeData) {
        validate(nodeData, CommandTreeReader.CommandNodeData::validateRedirectNodeIndex);
        validate(nodeData, CommandTreeReader.CommandNodeData::validateChildNodeIndices);
    }

    private static CommandTreeReader.CommandNodeData readCommandNode(final ArgumentTypeRegistry argumentTypeRegistry, ByteBuf buf) {
        return CommandTreeReader.CommandNodeData.read(argumentTypeRegistry, buf);
    }

    private static CommandTreeReader.SuggestableNode readArgumentBuilder(ByteBuf buf, final ArgumentTypeRegistry argumentTypeRegistry, byte flags) {
        int i = flags & 3;
        if (i == 2) {
            return ArgumentNode.read(argumentTypeRegistry, buf);
        } else if (i == 1) {
            return new CommandTreeReader.LiteralNode(readString(buf));
        } else {
            return null;
        }
    }

    private static <T> List<T> readList(final ByteBuf buf, final Function<ByteBuf, T> reader) {
        final List<T> out = new ArrayList<>();
        final int length = buf.readInt();
        for (int i = 0; i < length; i++) {
            out.add(reader.apply(buf));
        }

        return out;
    }

    private static int[] readIntArray(final ByteBuf buf) {
        final int length = buf.readInt();
        final int[] out = new int[length];
        for (int i = 0; i < length; i++) {
            out[i] = buf.readInt();
        }

        return out;
    }

    private static String readString(final ByteBuf buf) {
        final int length = buf.readUnsignedShort();
        final byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void writeString(final ByteBuf buf, final String string) {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    static class ArgumentNode<O, T extends ArgumentType<O>> implements CommandTreeReader.SuggestableNode {

        private final String name;
        private final ArgumentTypeRegistry.ArgumentSerializer.Properties<T> properties;

        ArgumentNode(final String name, final ArgumentTypeRegistry.ArgumentSerializer.Properties<T> properties) {
            this.name = name;
            this.properties = properties;
        }

        @Override
        public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(final ArgumentTypeRegistry argumentTypeRegistry) {
            return RequiredArgumentBuilder.argument(this.name, this.properties.createType());
        }

        public static <O, T extends ArgumentType<O>> ArgumentNode<O, T> read(final ArgumentTypeRegistry argumentTypeRegistry, final ByteBuf buf) {
            final String name = readString(buf);
            final String argumentTypeIdentifier = readString(buf);
            final ArgumentTypeRegistry.ArgumentSerializer<T> entry = (ArgumentTypeRegistry.ArgumentSerializer<T>) argumentTypeRegistry.getByIdentifier(argumentTypeIdentifier);
            final ArgumentTypeRegistry.ArgumentSerializer.Properties<T> properties = entry.read(buf);
            return new ArgumentNode<>(name, properties);
        }

        @Override
        public void write(ByteBuf buf) {
            CommandTreeReader.writeString(buf, this.name);
            CommandTreeReader.writeString(buf, this.properties.getSerializer().getIdentifier());
            this.properties.getSerializer().write(this.properties.createType(), buf);
        }

    }

    static class CommandNodeData {

        final CommandTreeReader.SuggestableNode suggestableNode;
        final int flags;
        final int redirectNodeIndex;
        final int[] childNodeIndices;

        CommandNodeData(CommandTreeReader.SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
            this.suggestableNode = suggestableNode;
            this.flags = flags;
            this.redirectNodeIndex = redirectNodeIndex;
            this.childNodeIndices = childNodeIndices;
        }

        public static CommandTreeReader.CommandNodeData read(final ArgumentTypeRegistry argumentTypeRegistry, ByteBuf buf) {
            final byte flags = buf.readByte();
            final int[] indices = readIntArray(buf);
            final int redirectNodeIndex = (flags & 8) != 0 ? buf.readInt() : 0;
            final CommandTreeReader.SuggestableNode suggestableNode = readArgumentBuilder(buf, argumentTypeRegistry, flags);
            return new CommandTreeReader.CommandNodeData(suggestableNode, flags, redirectNodeIndex, indices);
        }

        public boolean validateRedirectNodeIndex(final IntSet indices) {
            return (this.flags & 8) == 0 || !indices.contains(this.redirectNodeIndex);
        }

        public boolean validateChildNodeIndices(final IntSet indices) {
            for (int i : this.childNodeIndices) {
                if (indices.contains(i)) {
                    return false;
                }
            }

            return true;
        }

    }

    static class CommandTree {

        private final ArgumentTypeRegistry argumentTypeRegistry;
        private final List<CommandTreeReader.CommandNodeData> nodeData;
        private final List<CommandNode<CommandSource>> nodes;

        CommandTree(final ArgumentTypeRegistry argumentTypeRegistry, final List<CommandTreeReader.CommandNodeData> nodeData) {
            this.argumentTypeRegistry = argumentTypeRegistry;
            this.nodeData = nodeData;
            ObjectArrayList<CommandNode<CommandSource>> objectArrayList = new ObjectArrayList<>();
            objectArrayList.size(nodeData.size());
            this.nodes = objectArrayList;
        }

        public CommandNode<CommandSource> getNode(final int index) {
            final CommandNode<CommandSource> commandNode = this.nodes.get(index);
            if (commandNode != null) {
                return commandNode;
            } else {
                final CommandTreeReader.CommandNodeData commandNodeData = this.nodeData.get(index);
                final CommandNode<CommandSource> commandNode2;
                if (commandNodeData.suggestableNode == null) {
                    commandNode2 = new RootCommandNode<>();
                } else {
                    ArgumentBuilder<CommandSource, ?> argumentBuilder = commandNodeData.suggestableNode.createArgumentBuilder(this.argumentTypeRegistry);
                    if ((commandNodeData.flags & 8) != 0) {
                        argumentBuilder.redirect(this.getNode(commandNodeData.redirectNodeIndex));
                    }

                    if ((commandNodeData.flags & 4) != 0) {
                        argumentBuilder.executes(context -> 0);
                    }

                    commandNode2 = argumentBuilder.build();
                }

                this.nodes.set(index, commandNode2);

                for (int i : commandNodeData.childNodeIndices) {
                    final CommandNode<CommandSource> commandNode3 = this.getNode(i);
                    if (!(commandNode3 instanceof RootCommandNode)) {
                        commandNode2.addChild(commandNode3);
                    }
                }

                return commandNode2;
            }
        }

    }

    static class LiteralNode implements CommandTreeReader.SuggestableNode {

        private final String literal;

        LiteralNode(final String literal) {
            this.literal = literal;
        }

        @Override
        public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(final ArgumentTypeRegistry argumentTypeRegistry) {
            return LiteralArgumentBuilder.literal(this.literal);
        }

        @Override
        public void write(ByteBuf buf) {
            writeString(buf, this.literal);
        }

    }

    interface SuggestableNode {

        ArgumentBuilder<CommandSource, ?> createArgumentBuilder(final ArgumentTypeRegistry argumentTypeRegistry);

        void write(ByteBuf buf);

    }

}