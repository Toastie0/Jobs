package net.advancedplugins.jobs.impl.actions.utils;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockCache {
   private static final String CACHE_META = "aa-player-placed";

   public static void addBlock(Block var0, JavaPlugin var1) {
      var0.setMetadata("aa-player-placed", new FixedMetadataValue(var1, true));
   }

   public static boolean removeIfExists(Block var0, JavaPlugin var1) {
      if (contains(var0)) {
         var0.removeMetadata("aa-player-placed", var1);
         return true;
      } else {
         return false;
      }
   }

   public static boolean contains(Block var0) {
      return var0.hasMetadata("aa-player-placed");
   }
}
