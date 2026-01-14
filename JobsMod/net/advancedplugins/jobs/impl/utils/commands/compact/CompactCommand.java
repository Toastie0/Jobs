package net.advancedplugins.jobs.impl.utils.commands.compact;

import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.utils.commands.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CompactCommand<T extends CommandSender> extends SimpleCommand<T> {
   public CompactCommand(JavaPlugin var1, String var2, String var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public CompactCommand(JavaPlugin var1, String var2, boolean var3) {
      super(var1, var2, var3);
   }

   public CompactCommand(JavaPlugin var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public CompactCommand(JavaPlugin var1, String var2) {
      super(var1, var2);
   }

   public void subChain(UnaryOperator<SubChain<? extends CommandSender>> var1) {
      SubChain var2 = var1.apply(new SubChain());
      this.setSubCommands(var2.getSubCommands());
   }
}
