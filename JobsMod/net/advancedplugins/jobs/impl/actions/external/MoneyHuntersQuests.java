package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import su.nightexpress.moneyhunters.basic.api.event.PlayerJobExpGainEvent;
import su.nightexpress.moneyhunters.basic.api.event.PlayerJobLevelUpEvent;

public class MoneyHuntersQuests extends ActionQuestExecutor {
   public MoneyHuntersQuests(JavaPlugin var1) {
      super(var1, "moneyhunters");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLevelUp(PlayerJobLevelUpEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getJob().getName();
      this.execute("level_up", var2, var1.getNewLevel(), var1x -> var1x.root(var3), var0 -> var0, true);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onExperienceGain(PlayerJobExpGainEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getJob().getName();
      this.execute("gain_exp", var2, var1.getExp(), var1x -> var1x.root(var3));
   }
}
