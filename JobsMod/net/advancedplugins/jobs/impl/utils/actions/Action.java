package net.advancedplugins.jobs.impl.utils.actions;

import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Action {
   protected final String condition;
   protected final String value;
   protected final double delay;

   public Action(String var1, String var2, double var3) {
      this.condition = var1;
      this.value = var2;
      this.delay = var3;
   }

   public abstract void accept(Player var1, Replacer var2);

   public void execute(JavaPlugin var1, Player var2, Replacer var3) {
      if (this.delay > 0.0) {
         FoliaScheduler.runTaskLater(var1, () -> this.accept(var2, var3), Math.round(this.delay * 20.0));
      } else {
         this.accept(var2, var3);
      }
   }

   public String getCondition() {
      return this.condition;
   }

   public String getValue() {
      return this.value;
   }

   public double getDelay() {
      return this.delay;
   }
}
