package net.advancedplugins.jobs.impl.utils.commands.compact;

import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SubChain<T extends CommandSender> {
   private LinkedList<SubCommand<? extends CommandSender>> subCommands = new LinkedList<>();

   public LinkedList<SubCommand<? extends CommandSender>> getSubCommands() {
      return this.subCommands;
   }

   public SubChain<T> newSub(JavaPlugin var1, String var2, boolean var3, UnaryOperator<ArgumentBuilder> var4, final Executor var5) {
      List var6 = var4.apply(new ArgumentBuilder()).getArguments();
      SubCommand var7 = new SubCommand<T>(var1, var2, var3) {
         @Override
         public void onExecute(CommandSender var1, String[] var2x) {
            var5.execute(var1, var2x, null);
         }
      };
      var7.setArguments(var6);
      this.subCommands.add(var7);
      return this;
   }

   public SubChain<T> newSub(JavaPlugin var1, UnaryOperator<ArgumentBuilder> var2, Executor var3) {
      return this.newSub(var1, "", true, var2, var3);
   }

   public SubChain<T> newSub(JavaPlugin var1, String var2, UnaryOperator<ArgumentBuilder> var3, Executor var4) {
      return this.newSub(var1, var2, true, var3, var4);
   }

   public SubChain<T> newSub(JavaPlugin var1, boolean var2, UnaryOperator<ArgumentBuilder> var3, Executor var4) {
      return this.newSub(var1, "", var2, var3, var4);
   }
}
