package net.advancedplugins.jobs.impl.utils.actions.impl;

import net.advancedplugins.jobs.impl.utils.actions.Action;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TitleAction extends Action {
   public TitleAction(String var1, String var2, double var3) {
      super(var1, var2, var3);
   }

   @Override
   public void accept(Player var1, @Nullable Replacer var2) {
      String var3 = Text.modify(var2 == null ? this.value : var2.applyTo(this.value));
      var1.sendTitle(var3, null, 5, 60, 5);
   }
}
