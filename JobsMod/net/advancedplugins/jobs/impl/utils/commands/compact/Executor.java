package net.advancedplugins.jobs.impl.utils.commands.compact;

import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import org.bukkit.command.CommandSender;

public interface Executor {
   void execute(CommandSender var1, String[] var2, SubCommand<CommandSender> var3);
}
