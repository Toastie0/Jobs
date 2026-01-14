package net.advancedplugins.jobs.impl.actions.internal;

import java.util.Arrays;
import java.util.List;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.actions.utils.BlockCache;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class HarvestCropsQuest extends ActionContainer {
   private final List<Material> notAgeable;
   private final JavaPlugin plugin;

   public HarvestCropsQuest(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
      this.notAgeable = Arrays.asList(Material.PUMPKIN, Material.MELON, Material.SUGAR_CANE);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onHarvest(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      Material var3 = var1.getBlock().getType();
      boolean var4 = ActionRegistry.getRegistry().isBreakProtection() && !ActionRegistry.isIgnoreBlockCache();
      if (MinecraftVersion.isNew()) {
         if (var3 != Material.AIR) {
            BlockData var5 = var1.getBlock().getBlockData();
            if (var5 instanceof Ageable && var3 != Material.SUGAR_CANE) {
               Ageable var6 = (Ageable)var5;
               if (var6.getAge() != var6.getMaximumAge()) {
                  return;
               }
            } else {
               if (!this.notAgeable.contains(var3)) {
                  return;
               }

               if (var4 && BlockCache.removeIfExists(var1.getBlock(), this.plugin)) {
                  var1.getBlock().setMetadata("fakeBreak", new FixedMetadataValue(this.plugin, 1));
                  if (var3 != Material.SUGAR_CANE) {
                     return;
                  }
               }
            }

            int var8 = var3 == Material.SUGAR_CANE && var1.getBlock().hasMetadata("fakeBreak") ? 0 : 1;
            if (var3 == Material.SUGAR_CANE) {
               for (Block var7 = var1.getBlock().getRelative(BlockFace.UP); var7.getType() == Material.SUGAR_CANE; var7 = var7.getRelative(BlockFace.UP)) {
                  if (!var4 || !BlockCache.removeIfExists(var7, this.plugin)) {
                     var8++;
                  }
               }
            }

            this.executionBuilder("harvest-crops").player(var2).root(var1.getBlock()).progress(var8).buildAndExecute();
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onHarvestBlock(PlayerHarvestBlockEvent var1) {
      Player var2 = var1.getPlayer();
      var1.getItemsHarvested().forEach(var2x -> {
         if (var2x != null && var2x.getType() != Material.AIR) {
            if (var2x.getType().name().contains("BERRIES")) {
               this.executionBuilder("harvest-crops").player(var2).root(var2x).progressSingle().buildAndExecute();
            }
         }
      });
   }
}
