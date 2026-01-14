package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.actions.utils.BlockCache;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakAction extends ActionContainer {
   private final JavaPlugin plugin;

   public BlockBreakAction(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onBlockBreak(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getBlock();
      if (!var3.hasMetadata("fakeBreak")) {
         if (var3.getType() != Material.FIRE
            && (MinecraftVersion.getVersion().getVersionId() < MinecraftVersion.MC1_16_R1.getVersionId() || var3.getType() != Material.SOUL_FIRE)) {
            if (!ActionRegistry.getRegistry().isBreakProtection() || ActionRegistry.isIgnoreBlockCache() || !BlockCache.removeIfExists(var3, this.plugin)) {
               this.executionBuilder("block-break").player(var2).root(var3).progressSingle().buildAndExecute();
            }
         }
      }
   }
}
