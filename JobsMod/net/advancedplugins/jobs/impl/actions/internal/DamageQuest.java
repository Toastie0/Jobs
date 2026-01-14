package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageQuest extends ActionContainer {
   public DamageQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onDamage(EntityDamageByEntityEvent var1) {
      if (var1.getDamager() instanceof Player && var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getDamager();
         int var3 = (int)Math.round(var1.getDamage());
         if (!Bukkit.getPluginManager().isPluginEnabled("Citizens") || !CitizensAPI.getNPCRegistry().isNPC(var2)) {
            this.executionBuilder("damage-player").player(var2).progress(var3).canBeAsync().buildAndExecute();
         }
      }
   }
}
