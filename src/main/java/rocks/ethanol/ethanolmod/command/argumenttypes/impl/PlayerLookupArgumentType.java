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

public class PlayerLookupArgumentType implements ArgumentType<String>, MinecraftWrapper {

    private final boolean original;

    public PlayerLookupArgumentType(final boolean original) {
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

        if (single) return;

        final boolean includeEthanolUsers = chars.length != 1 && chars[1] == '*';

        int ignoreOffset = (includeEthanolUsers ? 2 : 1);
        if (ignoreOffset >= chars.length) {
            ignoreOffset = -1;
        }

        if (ignoreOffset != -1) {
            if (chars[ignoreOffset] != '!' || ignoreOffset + 1 >= chars.length) {
                throw CommandExceptions.PLAYER_LOOKUP_PARSE_FAIL.create();
            }
        }
    }

}
