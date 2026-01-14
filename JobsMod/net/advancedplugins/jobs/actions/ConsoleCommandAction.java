package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.Bukkit;

public class ConsoleCommandAction extends Action {
   public ConsoleCommandAction(String var1, String var2) {
      super(var1, var2);
   }

   public void accept(Replacer var1) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var1 == null ? this.value : var1.applyTo(this.value));
   }
}
