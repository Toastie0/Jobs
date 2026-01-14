package net.advancedplugins.jobs.impl.utils.actions.impl;

import javax.annotation.Nullable;
import net.advancedplugins.jobs.impl.utils.actions.Action;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCommandAction extends Action {
   public ConsoleCommandAction(String var1, String var2, double var3) {
      super(var1, var2, var3);
   }

   @Override
   public void accept(Player var1, @Nullable Replacer var2) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var2 == null ? this.value : var2.applyTo(this.value));
   }
}
