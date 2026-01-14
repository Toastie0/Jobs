package net.advancedplugins.jobs.commands.jobs;

import java.util.List;
import net.advancedplugins.jobs.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AliasCommand extends Command {
   private Core plugin;

   public AliasCommand(String var1, Core var2) {
      super(var1);
      this.plugin = var2;
   }

   public boolean execute(@NotNull CommandSender var1, @NotNull String var2, @NotNull String[] var3) {
      if (!(var1 instanceof Player var4)) {
         return false;
      } else {
         String var5 = String.join(" ", var3);
         if (!var5.isEmpty()) {
            var5 = " " + var5;
         }

         var4.performCommand("advancedjobs".concat(var5));
         return true;
      }
   }

   @NotNull
   public List<String> tabComplete(@NotNull CommandSender var1, @NotNull String var2, @NotNull String[] var3) {
      return this.plugin.getCommand("advancedjobs").tabComplete(var1, var2, var3);
   }
}
