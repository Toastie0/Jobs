package net.advancedplugins.jobs.impl.actions.external;

import com.badbones69.crazyenvoys.api.events.EnvoyOpenEvent;
import com.badbones69.crazyenvoys.api.events.FlareUseEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoyQuests extends ActionQuestExecutor {
   public CrazyEnvoyQuests(JavaPlugin var1) {
      super(var1, "crazyenvoy");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onOpenEnvoy(EnvoyOpenEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getTier().getName();
      if (var3 != null) {
         this.executionBuilder("open_envoy").player(var2).root(var3).progressSingle().buildAndExecute();
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onUseFlare(FlareUseEvent var1) {
      Player var2 = var1.getPlayer();
      this.executionBuilder("use_flare").player(var2).progressSingle().buildAndExecute();
   }
}
