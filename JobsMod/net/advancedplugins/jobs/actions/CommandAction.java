package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.entity.Player;

public class CommandAction extends Action {
   public CommandAction(String var1, String var2) {
      super(var1, var2);
   }

   public void accept(Player var1, Replacer var2) {
      String var3 = this.value;
      var1.performCommand(var2 == null ? var3 : var2.applyTo(var3));
   }
}
