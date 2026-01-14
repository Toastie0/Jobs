package net.advancedplugins.jobs.impl.actions.external;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicMobsQuests extends ActionQuestExecutor {
   public MythicMobsQuests(JavaPlugin var1) {
      super(var1, "mythicmobs");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onMobDeath(MythicMobDeathEvent var1) {
      if (var1.getKiller() instanceof Player) {
         Player var2 = (Player)var1.getKiller();
         String var3 = var1.getMobType().getInternalName();
         this.execute("kill_mob", var2, var1x -> var1x.root(var3));
      }
   }
}
