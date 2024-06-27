package rocks.ethanol.ethanolmod.command.argumenttypes.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import rocks.ethanol.ethanolmod.command.CommandExceptions;

import java.util.UUID;

public class UUIDArgumentType implements ArgumentType<UUID> {

    public static UUIDArgumentType create() {
        return new UUIDArgumentType();
    }

    public static UUID get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, UUID.class);
    }

    @Override
    public final UUID parse(final StringReader reader) throws CommandSyntaxException {
        try {
            return UUID.fromString(reader.readString());
        } catch (final IllegalArgumentException ignored) {
            throw CommandExceptions.INVALID_UUID.create();
        }
    }

}
