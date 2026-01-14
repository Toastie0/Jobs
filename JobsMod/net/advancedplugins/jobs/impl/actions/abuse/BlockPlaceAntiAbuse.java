package net.advancedplugins.jobs.impl.actions.abuse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPlaceAntiAbuse implements Listener {
   private final Map<UUID, Map<Material, Integer>> brokenBlocks = new HashMap<>();

   public BlockPlaceAntiAbuse(JavaPlugin var1) {
      Bukkit.getPluginManager().registerEvents(this, var1);
      FoliaScheduler.runTaskTimerAsynchronously(var1, this.brokenBlocks::clear, 1L, 300L);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      Material var3 = var1.getBlock().getType();
      if (var3 != Material.AIR) {
         if (var1.getBlock().getBlockData() instanceof Ageable) {
            Ageable var4 = (Ageable)var1.getBlock().getBlockData();
            if (var4.getAge() == var4.getMaximumAge()) {
               return;
            }
         }

         this.brokenBlocks.putIfAbsent(var2, new HashMap<>());
         this.brokenBlocks.get(var2).putIfAbsent(var3, 0);
         this.brokenBlocks.get(var2).compute(var3, (var0, var1x) -> var1x + 1);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      Material var3 = var1.getBlock().getType();
      if (var3 != Material.AIR) {
         if (this.brokenBlocks.containsKey(var2)) {
            if (this.brokenBlocks.get(var2).containsKey(var3)) {
               this.brokenBlocks.get(var2).compute(var3, (var0, var1x) -> Math.max(var1x - 1, 0));
            }
         }
      }
   }

   public boolean shouldProgress(UUID var1, Material var2) {
      if (ActionRegistry.isIgnoreBlockCache()) {
         return true;
      } else if (!this.brokenBlocks.containsKey(var1)) {
         return true;
      } else {
         return !this.brokenBlocks.get(var1).containsKey(var2) ? true : this.brokenBlocks.get(var1).get(var2) == 0;
      }
   }
}
