package rocks.ethanol.ethanolmod.command.argumenttypes.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import rocks.ethanol.ethanolmod.command.CommandExceptions;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypes;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;

import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<String>, MinecraftWrapper {

    private final boolean original;

    public PlayerArgumentType(final boolean original) {
        this.original = original;
    }

    public static String get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public final <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ArgumentTypes.requestServerSuggestions(builder);
    }

    @Override
    public final String parse(final StringReader reader) throws CommandSyntaxException {
        final String string = ArgumentTypes.readRawWord(reader);
        this.checkParse(string);
        return string;
    }

    public final boolean isOriginal() {
        return this.original;
    }

    private void checkParse(final String string) throws CommandSyntaxException {
        final char[] chars = string.toCharArray();
        final boolean single = chars.length == 0 || chars[0] != '*';
        if (!single) {
            throw CommandExceptions.EXPECTED_SINGLE_PLAYER.create();
        }
    }

}
