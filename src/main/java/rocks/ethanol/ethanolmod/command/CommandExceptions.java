package rocks.ethanol.ethanolmod.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandExceptions {

    public static final StaticCommandExceptionType PLAYER_LOOKUP_PARSE_FAIL = StaticCommandExceptionType.of("Failed to parse player lookup!");
    public static final StaticCommandExceptionType EXPECTED_SINGLE_PLAYER = StaticCommandExceptionType.of("Expected a single player!");
    public static final StaticCommandExceptionType INVALID_UUID = StaticCommandExceptionType.of("Invalid UUID!");
    public static final StaticCommandExceptionType OPTION_NOT_FOUND = StaticCommandExceptionType.of("Option not found!");

    public static class StaticCommandExceptionType implements CommandExceptionType {

        private final Message message;

        public StaticCommandExceptionType(final Message message) {
            this.message = message;
        }

        public static StaticCommandExceptionType of(final String literal) {
            return new StaticCommandExceptionType(LiteralMessage.of(literal));
        }

        public CommandSyntaxException create() {
            return new CommandSyntaxException(this, this.message);
        }

    }

}