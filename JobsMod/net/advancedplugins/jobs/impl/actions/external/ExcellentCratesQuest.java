package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

public class ExcellentCratesQuest extends ActionQuestExecutor {
   public ExcellentCratesQuest(JavaPlugin var1) {
      super(var1, "excellentcrates");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCrateOpen(CrateOpenEvent var1) {
      this.executionBuilder("open").progressSingle().root(var1.getCrate().getId()).player(var1.getPlayer()).buildAndExecute();
   }
}
