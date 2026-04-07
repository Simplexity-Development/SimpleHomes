package simplexity.simplehomes.commands.brigstuff;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.LocaleMessage;

public class Exceptions {

    public static final SimpleCommandExceptionType MUST_BE_PLAYER = new SimpleCommandExceptionType(serialize(LocaleMessage.MUST_BE_PLAYER));
    public static final SimpleCommandExceptionType PROVIDE_HOME_NAME = new SimpleCommandExceptionType(serialize(LocaleMessage.PROVIDE_HOME_NAME));
    public static final SimpleCommandExceptionType HOME_ALREADY_EXISTS = new SimpleCommandExceptionType(serialize(LocaleMessage.HOME_ALREADY_EXISTS));
    public static final DynamicCommandExceptionType HOME_NOT_FOUND = new DynamicCommandExceptionType(name -> serialize(LocaleMessage.HOME_NOT_FOUND, name.toString()));
    public static final DynamicCommandExceptionType NULL_HOME = new DynamicCommandExceptionType(name -> serialize(LocaleMessage.NULL_HOME, name.toString()));
    public static final DynamicCommandExceptionType CANNOT_SET_MORE = new DynamicCommandExceptionType(count -> serialize(LocaleMessage.CANNOT_SET_MORE_HOMES, count.toString()));
    public static final DynamicCommandExceptionType CANNOT_USE_COMMAND = new DynamicCommandExceptionType(count -> serialize(LocaleMessage.CANNOT_USE_COMMAND, count.toString()));


    private static Message serialize(LocaleMessage localeMessage) {
        return MessageComponentSerializer.message().serialize(
                SimpleHomes.getMiniMessage().deserialize(
                        localeMessage.getMessage()
                ));
    }

    private static Message serialize(LocaleMessage localeMessage, String value) {
        return MessageComponentSerializer.message().serialize(
                SimpleHomes.getMiniMessage().deserialize(
                        localeMessage.getMessage(),
                        Placeholder.parsed("value", value),
                        Placeholder.parsed("name", value)
                ));
    }



}
