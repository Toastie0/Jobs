package net.advancedplugins.jobs.impl.actions.internal;

import java.util.Arrays;
import java.util.List;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlantCropsQuest extends ActionContainer {
   private final List<Material> notCropsButProgress = Arrays.asList(Material.NETHER_WART, Material.SUGAR_CANE);

   public PlantCropsQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler
   public void onCropsPlanted(BlockPlaceEvent var1) {
      Material var2 = var1.getBlockPlaced().getType();
      if (MinecraftVersion.isNew()) {
         if (Tag.CROPS.isTagged(var2) || this.notCropsButProgress.contains(var2)) {
            Player var3 = var1.getPlayer();
            if (ActionRegistry.getBlockPlaceAntiAbuse().shouldProgress(var3.getUniqueId(), var2)) {
               this.executionBuilder("crops-planted").player(var3).root(var2).canBeAsync().progressSingle().buildAndExecute();
            }
         }
      }
   }
}
