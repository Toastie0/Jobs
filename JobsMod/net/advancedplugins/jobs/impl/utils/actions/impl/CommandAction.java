package net.advancedplugins.jobs.impl.utils.actions.impl;

import net.advancedplugins.jobs.impl.utils.actions.Action;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CommandAction extends Action {
   public CommandAction(String var1, String var2, double var3) {
      super(var1, var2, var3);
   }

   @Override
   public void accept(Player var1, @Nullable Replacer var2) {
      String var3 = this.value;
      var1.performCommand(var2 == null ? var3 : var2.applyTo(var3));
   }
}
