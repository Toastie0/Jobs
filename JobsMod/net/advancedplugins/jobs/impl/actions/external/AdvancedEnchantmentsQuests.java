package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.ae.api.EnchantApplyEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedEnchantmentsQuests extends ActionQuestExecutor {
   public AdvancedEnchantmentsQuests(JavaPlugin var1) {
      super(var1, "advancedenchantments");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCustomEnchantApply(EnchantApplyEvent var1) {
      Player var2 = var1.getPlayer();
      int var3 = var1.getLevel();
      String var4 = var1.getEnchantment().getPath();
      this.executionBuilder("enchant").player(var2).root(var4).subRoot("level", String.valueOf(var3)).progress(1).buildAndExecute();
   }
}
