package rocks.ethanol.ethanolmod.command.argumenttypes.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import rocks.ethanol.ethanolmod.command.argumenttypes.ArgumentTypes;

import java.util.concurrent.CompletableFuture;

public class PluginArgumentType implements ArgumentType<String> {

    public static PluginArgumentType create() {
        return new PluginArgumentType();
    }

    public static String get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ArgumentTypes.requestServerSuggestions(builder);
    }

    @Override
    public final String parse(final StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readString();
    }

}
