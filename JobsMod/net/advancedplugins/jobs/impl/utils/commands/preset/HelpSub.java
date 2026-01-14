package net.advancedplugins.jobs.impl.utils.commands.preset;

import net.advancedplugins.jobs.impl.utils.commands.SimpleCommand;
import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpSub extends SubCommand<CommandSender> {
   public HelpSub(JavaPlugin var1, SimpleCommand<?> var2) {
      super(var1, "", true);
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
   }
}
