package net.advancedplugins.jobs.impl.actions.external;

import com.hazebyte.crate.api.event.CrateRewardEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateReloadedQuests extends ActionQuestExecutor {
   public CrateReloadedQuests(JavaPlugin var1) {
      super(var1, "cratereloaded");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onRewardReceive(CrateRewardEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getCrate().getCrateName();
      this.execute("open", var2, var1x -> var1x.root(var3));
   }
}
