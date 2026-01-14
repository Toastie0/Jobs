package net.advancedplugins.jobs.impl.actions.internal;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class KillMobQuest extends ActionContainer {
   private final NamespacedKey spawnReasonKey;

   public KillMobQuest(JavaPlugin var1) {
      super(var1);
      this.spawnReasonKey = new NamespacedKey(var1, "spawn_reason");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEntityKill(EntityDeathEvent var1) {
      LivingEntity var2 = var1.getEntity();
      Player var3 = var2.getKiller();
      if (!(var2 instanceof Player)) {
         int var4 = 1;
         if (var1.getEntity().getType() != EntityType.ARMOR_STAND && Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
            var4 = WildStackerAPI.getEntityAmount(var1.getEntity());
         }

         String var5 = var2.getCustomName();
         if (var5 == null) {
            var5 = "";
         }

         this.executionBuilder("kill-mob")
            .player(var3)
            .root(var2)
            .progress(var4)
            .subRoot(
               "spawn-reason",
               (String)var2.getPersistentDataContainer().getOrDefault(this.spawnReasonKey, PersistentDataType.STRING, SpawnReason.DEFAULT.name())
            )
            .subRoot("custom-name", var5)
            .buildAndExecute();
      }
   }

   @EventHandler
   public void onSpawner(CreatureSpawnEvent var1) {
      var1.getEntity().getPersistentDataContainer().set(this.spawnReasonKey, PersistentDataType.STRING, var1.getSpawnReason().name());
   }
}
