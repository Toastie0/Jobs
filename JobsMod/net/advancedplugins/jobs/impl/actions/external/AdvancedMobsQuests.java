package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import net.advancedplugins.mobs.AdvancedMobsAPI;
import net.advancedplugins.mobs.impl.customMobs.entityInstance.CustomEntityInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedMobsQuests extends ExternalActionContainer {
   public AdvancedMobsQuests(JavaPlugin var1) {
      super(var1, "advancedmobs");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDeath(EntityDeathEvent var1) {
      LivingEntity var2 = var1.getEntity();
      Player var3 = var2.getKiller();
      if (var3 != null) {
         CustomEntityInstance var4 = AdvancedMobsAPI.matchEntityToCustomMob(var2.getUniqueId());
         if (var4 != null) {
            this.executionBuilder("kill-mob").player(var3).root(var4.getEntityMeta().getEntityType()).progressSingle().buildAndExecute();
         }
      }
   }
}
