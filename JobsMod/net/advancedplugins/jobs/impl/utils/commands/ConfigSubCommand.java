package net.advancedplugins.jobs.impl.utils.commands;

import net.advancedplugins.jobs.impl.utils.commands.argument.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConfigSubCommand<T extends CommandSender> extends ConfigCommand<T> {
   private final boolean endless;

   public ConfigSubCommand(JavaPlugin var1, ConfigCommand.Config var2, boolean var3, boolean var4) {
      super(var1, var2, var3);
      this.endless = var4;
   }

   public String getFormatted(String var1) {
      StringBuilder var2 = new StringBuilder().append("/").append(var1).append(" ");

      for (Argument var4 : this.getArguments()) {
         if (var4.getType() == null) {
            var2.append(var4.getArgument());
         } else if (var4.isOptional()) {
            var2.append("&9[").append(var4.getArgument()).append("]&r");
         } else {
            var2.append("&2<").append(var4.getArgument()).append(">&r");
         }

         var2.append(" ");
      }

      var2.append("&8-&e ");
      var2.append(this.getConfig().getDescription());
      return var2.toString();
   }

   @Override
   public boolean isArgumentValid(String[] var1, int var2) {
      return this.getArgumentsSizeReal() - 1 < var2 ? this.endless : super.isArgumentValid(var1, var2);
   }

   public boolean isEndless() {
      return this.endless;
   }
}
