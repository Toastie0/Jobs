package net.advancedplugins.jobs.impl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BreakWholeTree {
   private int leavesScanned = 0;
   private final Set<Block> foundBlocks;
   private final Set<Block> scannedBlocks = new HashSet<>();
   private final List<Location> logs = new ArrayList<>();
   private final List<Location> leaves = new ArrayList<>();
   private final int maxLogs;
   private final int maxLeaves;
   private final List<BlockFace> dirs = Arrays.asList(
      BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST
   );

   public BreakWholeTree(Block var1, int var2, int var3) {
      this.foundBlocks = new HashSet<>();
      this.maxLogs = var2 > 0 ? var2 : 1000;
      this.maxLeaves = var3 > 0 ? var3 : 300;
      Location var4 = var1.getLocation();
      this.findLog(var1, true, true);
      this.findLog(var1, false, true);

      for (Location var6 : this.logs) {
         if (var6.getY() < var4.getY()) {
            var4 = var6;
         }
      }

      this.foundBlocks.addAll(this.logs.stream().map(Location::getBlock).collect(Collectors.toSet()));
      this.foundBlocks.remove(var1);
   }

   private boolean findLog(Block var1, boolean var2, boolean var3) {
      if (var1 != null && !this.foundBlocks.contains(var1)) {
         if (this.foundBlocks.size() >= this.maxLogs || this.leavesScanned >= this.maxLeaves) {
            return false;
         } else if (this.scannedBlocks.contains(var1)) {
            return false;
         } else {
            boolean var4 = false;
            if (ASManager.isLog(var1.getType()) && this.foundBlocks.add(var1)) {
               this.logs.add(var1.getLocation());
               var4 = true;
            }

            if (var1.getType().name().endsWith("LEAVES") && !this.leaves.contains(var1.getLocation())) {
               this.leaves.add(var1.getLocation());
               this.leavesScanned++;
            }

            this.scannedBlocks.add(var1);
            if (var4) {
               for (BlockFace var6 : this.dirs) {
                  this.findLog(var1.getRelative(var6), var2, true);
               }
            }

            if (var4 || var3) {
               Block var7 = var1.getRelative(var2 ? BlockFace.UP : BlockFace.DOWN);
               if (!this.foundBlocks.contains(var7)) {
                  this.findLog(var7, var2, var4);
               }
            }

            return var4;
         }
      } else {
         return false;
      }
   }

   public Set<Block> get() {
      return this.foundBlocks;
   }
}
