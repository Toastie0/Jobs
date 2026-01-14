package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileQuest extends ActionContainer {
   public ProjectileQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onProjectileLaunch(ProjectileLaunchEvent var1) {
      ProjectileSource var2 = var1.getEntity().getShooter();
      EntityType var3 = var1.getEntityType();
      if (var2 instanceof Player) {
         this.executionBuilder("throw-projectile").player((Player)var2).canBeAsync().root(var3.toString()).progressSingle().buildAndExecute();
      }
   }
}
