package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.entity.Player;

public class SubtitleAction extends Action {
   public SubtitleAction(String var1, String var2) {
      super(var1, var2);
   }

   public void accept(Player var1, Replacer var2) {
      String var3 = Text.modify(var2 == null ? this.value : var2.applyTo(this.value));
      var1.sendTitle(null, var3, 5, 60, 5);
   }
}
