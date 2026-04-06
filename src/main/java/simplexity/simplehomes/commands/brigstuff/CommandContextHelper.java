package simplexity.simplehomes.commands.brigstuff;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class CommandContextHelper {
    /**
     * Gets player sender from command context if one exists, null if none exists
     * @param ctx Command Context
     * @return Player
     */
    @Nullable
    public static Player getPlayer(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getSender() instanceof Player player) return player;
        return null;
    }

    /**
     * Gets command sender from command context
     * @param ctx Command Context
     * @return CommandSender
     */
    public static CommandSender getCommandSender(CommandContext<CommandSourceStack> ctx) {
        return ctx.getSource().getSender();
    }
}
