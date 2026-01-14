package net.advancedplugins.jobs.impl.actions.external;

import me.rivaldev.harvesterhoes.api.events.RivalBlockBreakEvent;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class RivalHoeQuests extends ExternalActionContainer {
   public RivalHoeQuests(JavaPlugin var1) {
      super(var1, "rivalharvesterhoes");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBreak(RivalBlockBreakEvent var1) {
      this.executionBuilder("harvest").player(var1.getPlayer()).root(var1.getCrop()).subRoot(var1.getHoeItem()).progress(var1.getAmount()).buildAndExecute();
   }
}
