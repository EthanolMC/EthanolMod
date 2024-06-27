package rocks.ethanol.ethanolmod.command;

import com.mojang.brigadier.Message;

public class LiteralMessage implements Message {

    private final String string;

    public LiteralMessage(final String string) {
        this.string = string;
    }

    public static LiteralMessage of(final String literal) {
        return new LiteralMessage(literal);
    }

    @Override
    public final String getString() {
        return this.string;
    }

}