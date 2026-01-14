package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import plus.crates.Events.CrateOpenEvent;

public class CratesPlusQuests extends ActionQuestExecutor {
   public CratesPlusQuests(JavaPlugin var1) {
      super(var1, "cratesplus");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCrateOpen(CrateOpenEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getCrate().getName();
      Location var4 = var1.getBlockLocation();
      if (var3 != null && var4 != null) {
         this.execute("open", var2, var1x -> var1x.root(var3));
      }
   }
}
