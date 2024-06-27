package rocks.ethanol.ethanolmod.command.argumenttypes.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class IntVector3ArgumentType implements ArgumentType<IntVector3ArgumentType.IntVector3> {

    public static IntVector3ArgumentType create() {
        return new IntVector3ArgumentType();
    }

    public static IntVector3 get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, IntVector3.class);
    }

    @Override
    public final IntVector3 parse(final StringReader stringReader) throws CommandSyntaxException {
        final int x = stringReader.readInt();
        stringReader.skipWhitespace();

        final int y = stringReader.readInt();
        stringReader.skipWhitespace();

        final int z = stringReader.readInt();

        return new IntVector3(x, y, z);
    }

    public static class IntVector3 {

        private final int x;
        private final int y;
        private final int z;

        public IntVector3(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public final int getX() {
            return this.x;
        }

        public final int getY() {
            return this.y;
        }

        public final int getZ() {
            return this.z;
        }

    }

}
