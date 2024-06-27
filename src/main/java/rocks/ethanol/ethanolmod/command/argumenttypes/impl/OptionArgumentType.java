package rocks.ethanol.ethanolmod.command.argumenttypes.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import rocks.ethanol.ethanolmod.command.CommandExceptions;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OptionArgumentType implements ArgumentType<String> {

    private final List<String> list;

    public OptionArgumentType(final String[] values) {
        this.list = Arrays.asList(values);
    }

    public static String get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public final <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ArgumentTypes.suggestMatching(this.list, builder);
    }

    @Override
    public final String parse(final StringReader stringReader) throws CommandSyntaxException {
        final String string = stringReader.readString();
        if (!this.list.contains(string)) {
            throw CommandExceptions.OPTION_NOT_FOUND.create();
        }
        return string;
    }

    public List<String> getList() {
        return this.list;
    }

}
