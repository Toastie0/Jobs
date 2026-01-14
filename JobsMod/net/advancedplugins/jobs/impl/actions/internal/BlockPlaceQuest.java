package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.actions.utils.BlockCache;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPlaceQuest extends ActionContainer {
   private final JavaPlugin plugin;

   public BlockPlaceQuest(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getBlock();
      if (var3.getType() != Material.FIRE
         && (MinecraftVersion.getVersion().getVersionId() < MinecraftVersion.MC1_16_R1.getVersionId() || var3.getType() != Material.SOUL_FIRE)) {
         ItemStack var4 = var1.getItemInHand();
         if (var4 == null || var4.getType() != Material.DEBUG_STICK) {
            boolean var5 = false;
            if (MinecraftVersion.isNew() && var3.getBlockData() instanceof Ageable && var3.getType() != Material.SUGAR_CANE) {
               var5 = true;
            }

            if (!var5 && !ActionRegistry.isIgnoreBlockCache()) {
               BlockCache.addBlock(var1.getBlock(), this.plugin);
               if (!ActionRegistry.getBlockPlaceAntiAbuse().shouldProgress(var2.getUniqueId(), var3.getType())) {
                  return;
               }
            }

            if (var3.getType() != Material.DIRT_PATH && !var3.getType().name().contains("STRIPPED")) {
               this.executionBuilder("block-place").player(var2).root(var3).progressSingle().buildAndExecute();
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onPiston(BlockPistonExtendEvent var1) {
      var1.getBlocks()
         .forEach(
            var2 -> {
               if (BlockCache.removeIfExists(var2, this.plugin)) {
                  FoliaScheduler.runTaskLater(
                     this.plugin, () -> BlockCache.addBlock(var2.getLocation().add(var1.getDirection().getDirection()).getBlock(), this.plugin), 15L
                  );
               }
            }
         );
   }
}
