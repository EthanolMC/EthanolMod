package rocks.ethanol.ethanolmod.command.argumenttypes;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.buffer.ByteBuf;
import rocks.ethanol.ethanolmod.command.argumenttypes.impl.IntVector3ArgumentType;
import rocks.ethanol.ethanolmod.command.argumenttypes.impl.OptionArgumentType;
import rocks.ethanol.ethanolmod.command.argumenttypes.impl.PlayerLookupArgumentType;
import rocks.ethanol.ethanolmod.command.argumenttypes.impl.ServersideArgumentType;
import rocks.ethanol.ethanolmod.command.argumenttypes.impl.UUIDArgumentType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArgumentTypeRegistry {

    private final Map<String, ArgumentSerializer<?>> identifierMap;

    public ArgumentTypeRegistry() {
        this.identifierMap = new HashMap<>();

        this.register(ArgumentSerializer.of(StringArgumentType.class, "string", buf -> {
            return switch (buf.readByte()) {
                case 0 -> StringArgumentType.string();
                case 1 -> StringArgumentType.greedyString();
                case 2 -> StringArgumentType.word();
                default -> throw new IllegalStateException();
            };
        }, (type, buf) -> {
            switch (type.getType()) {
                case QUOTABLE_PHRASE:
                    buf.writeByte(0);
                    break;
                case GREEDY_PHRASE:
                    buf.writeByte(1);
                    break;
                case SINGLE_WORD:
                    buf.writeByte(2);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }, (type) -> {
            return switch (type.getType()) {
                case QUOTABLE_PHRASE -> StringArgumentType.string();
                case GREEDY_PHRASE -> StringArgumentType.greedyString();
                case SINGLE_WORD -> StringArgumentType.word();
                default -> throw new IllegalStateException();
            };
        }));
        this.register(ArgumentSerializer.of(IntegerArgumentType.class, "integer", buf -> {
            return IntegerArgumentType.integer(buf.readInt(), buf.readInt());
        }, (type, buf) -> {
            buf.writeInt(type.getMinimum());
            buf.writeInt(type.getMaximum());
        }, (type) -> IntegerArgumentType.integer(type.getMinimum(), type.getMaximum())));
        this.register(ArgumentSerializer.of(FloatArgumentType.class, "float", buf -> {
            return FloatArgumentType.floatArg(buf.readFloat(), buf.readFloat());
        }, (type, buf) -> {
            buf.writeFloat(type.getMinimum());
            buf.writeFloat(type.getMaximum());
        }, (type) -> FloatArgumentType.floatArg(type.getMinimum(), type.getMaximum())));
        this.register(ArgumentSerializer.of(BoolArgumentType.class, "bool", BoolArgumentType::bool, (type) -> BoolArgumentType.bool()));
        this.register(ArgumentSerializer.of(DoubleArgumentType.class, "double", buf -> {
            return DoubleArgumentType.doubleArg(buf.readDouble(), buf.readDouble());
        }, (type, buf) -> {
            buf.writeDouble(type.getMinimum());
            buf.writeDouble(type.getMaximum());
        }, (type) -> DoubleArgumentType.doubleArg(type.getMinimum(), type.getMaximum())));
        this.register(ArgumentSerializer.of(LongArgumentType.class, "long", buf -> {
            return LongArgumentType.longArg(buf.readLong(), buf.readLong());
        }, (type, buf) -> {
            buf.writeLong(type.getMinimum());
            buf.writeLong(type.getMaximum());
        }, (type) -> LongArgumentType.longArg(type.getMinimum(), type.getMaximum())));
        this.register(ArgumentSerializer.of(IntVector3ArgumentType.class, "int_vector_3", IntVector3ArgumentType::create, (type) -> IntVector3ArgumentType.create()));
        this.register(ArgumentSerializer.of(OptionArgumentType.class, "option", buf -> {
            final int length = buf.readInt();
            final String[] options = new String[length];
            for (int i = 0; i < length; i++) {
                final byte[] optionBytes = new byte[buf.readUnsignedShort()];
                buf.readBytes(optionBytes);
                options[i] = new String(optionBytes, StandardCharsets.UTF_8);
            }
            return new OptionArgumentType(options);
        }, (type, buf) -> {
            final List<String> options = type.getList();
            buf.writeInt(options.size());
            for (final String option : options) {
                final byte[] optionBytes = option.getBytes(StandardCharsets.UTF_8);
                buf.writeShort(optionBytes.length);
                buf.writeBytes(optionBytes);
            }
        }, (type) -> type));
        this.register(ArgumentSerializer.of(PlayerLookupArgumentType.class, "player_lookup", (buf) -> {
            return new PlayerLookupArgumentType(buf.readBoolean(), buf.readBoolean());
        }, (type, buf) -> {
            buf.writeBoolean(type.isSingleOnly());
            buf.writeBoolean(type.isOriginal());
        }, (type) -> new PlayerLookupArgumentType(type.isSingleOnly(), type.isOriginal())));

        this.register(ArgumentSerializer.of(ServersideArgumentType.class, "serverside", ServersideArgumentType::create, (type) -> ServersideArgumentType.create()));
        {
            this.register(ArgumentSerializer.of(ServersideArgumentType.class, "plugin", ServersideArgumentType::create, (type) -> ServersideArgumentType.create()));
            this.register(ArgumentSerializer.of(ServersideArgumentType.class, "world", ServersideArgumentType::create, (type) -> ServersideArgumentType.create()));
        }

        this.register(ArgumentSerializer.of(UUIDArgumentType.class, "uuid", UUIDArgumentType::create, (type) -> UUIDArgumentType.create()));
    }

    public final void register(final ArgumentSerializer<?> serializer) {
        this.identifierMap.put(serializer.getIdentifier(), serializer);
    }

    public final ArgumentSerializer<?> getByIdentifier(final String identifier) throws NoSuchElementException {
        final ArgumentSerializer<?> entry = this.identifierMap.get(identifier);
        if (entry == null)
            throw new NoSuchElementException();

        return entry;
    }

    public interface ArgumentSerializer<T extends ArgumentType<?>> {

        static <T extends ArgumentType<?>> ArgumentSerializer<T> of(final Class<T> ownClass, final String identifier, final Supplier<T> reader, final Function<T, T> from) {
            return new ArgumentSerializer<T>() {
                @Override
                public Properties<T> read(final ByteBuf buf) {
                    return Properties.of(reader.get(), this);
                }

                @Override
                public Properties<T> from(final T object) {
                    return ArgumentSerializer.propertiesFrom(from, object, this);
                }

                @Override
                public void write(final T object, final ByteBuf buf) {

                }

                @Override
                public Class<T> getOwnClass() {
                    return ownClass;
                }

                @Override
                public String getIdentifier() {
                    return identifier;
                }
            };
        }

        static <T extends ArgumentType<?>> ArgumentSerializer<T> of(final Class<T> ownClass, final String identifier, final Function<ByteBuf, T> reader, final BiConsumer<T, ByteBuf> writer, final Function<T, T> from) {
            return new ArgumentSerializer<T>() {
                @Override
                public Properties<T> read(final ByteBuf buf) {
                    return Properties.of(reader.apply(buf), this);
                }

                @Override
                public Properties<T> from(final T object) {
                    return ArgumentSerializer.propertiesFrom(from, object, this);
                }

                @Override
                public void write(final T object, final ByteBuf buf) {
                    writer.accept(object, buf);
                }

                @Override
                public Class<T> getOwnClass() {
                    return ownClass;
                }

                @Override
                public String getIdentifier() {
                    return identifier;
                }
            };
        }

        static <T extends ArgumentType<?>> Properties<T> propertiesFrom(final Function<T, T> function, final T object, final ArgumentSerializer<T> serializer) {
            return new Properties<T>() {
                @Override
                public T createType() {
                    return function.apply(object);
                }

                @Override
                public ArgumentSerializer<T> getSerializer() {
                    return serializer;
                }
            };
        }

        Properties<T> read(final ByteBuf buf);

        Properties<T> from(final T object);

        void write(T object, final ByteBuf buf);

        Class<T> getOwnClass();

        String getIdentifier();

        interface Properties<T extends ArgumentType<?>> {

            static <T extends ArgumentType<?>> Properties<T> of(final T value, final ArgumentSerializer<T> serializer) {
                return new Properties<T>() {
                    @Override
                    public T createType() {
                        return value;
                    }

                    @Override
                    public ArgumentSerializer<T> getSerializer() {
                        return serializer;
                    }
                };
            }

            static <T extends ArgumentType<?>> Properties<T> of(final Supplier<T> function, final ArgumentSerializer<T> serializer) {
                return new Properties<T>() {
                    @Override
                    public T createType() {
                        return function.get();
                    }

                    @Override
                    public ArgumentSerializer<T> getSerializer() {
                        return serializer;
                    }
                };
            }

            T createType();

            ArgumentSerializer<T> getSerializer();

        }

    }

}