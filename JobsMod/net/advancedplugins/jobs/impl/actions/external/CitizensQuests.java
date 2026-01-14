package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class CitizensQuests extends ActionQuestExecutor {
   public CitizensQuests(JavaPlugin var1) {
      super(var1, "citizens");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onNpcRightClick(NPCRightClickEvent var1) {
      Player var2 = var1.getClicker();
      String var3 = var1.getNPC().getName();
      this.execute("click", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onNpcDeath(NPCDamageByEntityEvent var1) {
      if (var1.getDamager() instanceof Player) {
         Player var2 = (Player)var1.getDamager();
         String var3 = var1.getNPC().getName();
         int var4 = (int)var1.getDamage();
         this.execute("damage", var2, var4, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onNpcDeath(NPCDeathEvent var1) {
      if (var1.getEvent().getEntity().getKiller() != null) {
         Player var2 = var1.getEvent().getEntity().getKiller();
         String var3 = var1.getNPC().getName();
         this.execute("kill", var2, var1x -> var1x.root(var3));
      }
   }
}
