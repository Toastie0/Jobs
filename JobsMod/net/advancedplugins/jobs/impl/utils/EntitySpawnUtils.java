package net.advancedplugins.jobs.impl.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EntitySpawnUtils implements Listener {
   public static String metaDataPrefix = "advanced";
   private final Plugin plugin;

   public EntitySpawnUtils(Plugin var1) {
      this.plugin = var1;
   }

   public static Entity spawnEntity(@NotNull Plugin var0, @NotNull World var1, @NotNull Location var2, @NotNull EntityType var3) {
      Entity var4 = var1.spawnEntity(var2, var3);
      var4.setMetadata(metaDataPrefix + "-entity", new FixedMetadataValue(var0, true));
      return var4;
   }

   @EventHandler
   private void onEntityDeath(EntityDeathEvent var1) {
      var1.getEntity().removeMetadata(metaDataPrefix + "-entity", this.plugin);
   }
}
