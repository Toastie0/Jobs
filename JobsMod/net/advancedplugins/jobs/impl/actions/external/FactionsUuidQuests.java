package net.advancedplugins.jobs.impl.actions.external;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Relation;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionsUuidQuests extends ActionQuestExecutor {
   public FactionsUuidQuests(JavaPlugin var1) {
      super(var1, "factions");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDeath(EntityDeathEvent var1) {
      if (var1.getEntity() instanceof Player) {
         FPlayers var2 = FPlayers.getInstance();
         Player var3 = (Player)var1.getEntity();
         Player var4 = var3.getKiller();
         FPlayer var5 = var2.getByPlayer(var4);
         FPlayer var6 = var2.getByPlayer(var3);
         if (var5.getRelationTo(var6) == Relation.ENEMY) {
            this.execute("kill_enemy", var4, ActionResult::none);
         }
      }
   }
}
