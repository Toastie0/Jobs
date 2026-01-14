package net.advancedplugins.jobs.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

public class CropUtils {
   private static final Map<String, String> materialConversions = new HashMap<>();

   public static Material convertToMaterial(String var0) {
      if (MinecraftVersion.isNew()) {
         try {
            return Material.valueOf(var0);
         } catch (IllegalArgumentException var3) {
            return Material.AIR;
         }
      } else {
         for (Entry var2 : materialConversions.entrySet()) {
            if (var0.equals(var2.getKey())) {
               return Material.valueOf(var0.replace((CharSequence)var2.getKey(), (CharSequence)var2.getValue()));
            }
         }

         return Material.matchMaterial(var0);
      }
   }

   public static int getDropAmount(Block var0, Material var1, ItemStack var2) {
      if (!isFullyGrown(var0)) {
         return 1;
      } else {
         int var3 = var2.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("FORTUNE"));
         int var5 = new BinomialDistribution(Math.max(3, 3 + var3), 0.57).sample();
         String var6 = var0.getType().name().replace("LEGACY_", "");

         return switch (var6) {
            case "CARROT", "CARROTS", "POTATO", "POTATOES" -> 2 + var5;
            case "BEETROOTS", "BEETROOT" -> {
               if (var1.name().contains("SEEDS")) {
                  yield MathUtils.randomBetween(2, 4) + var5;
               } else {
                  yield 1;
               }
            }
            case "COCOA" -> 3;
            case "NETHER_WART" -> new UniformIntegerDistribution(2, 4 + var3).sample();
            case "CROPS", "WHEAT" -> {
               if (var1.name().contains("SEEDS")) {
                  yield MathUtils.randomBetween(0, 3) + var5;
               } else {
                  yield 1;
               }
            }
            default -> 1;
         };
      }
   }

   public static boolean isCrop(Material var0) {
      if (!ASManager.isValid(var0)) {
         return false;
      } else {
         String var1 = var0.name();
         switch (var1) {
            case "CROPS":
            case "WHEAT":
            case "CARROTS":
            case "POTATOES":
            case "SUGAR_CANE":
            case "BEETROOTS":
            case "CARROT":
            case "POTATO":
            case "BEETROOT":
            case "COCOA":
            case "NETHER_WART":
            case "NETHER_WARTS":
            case "TORCHFLOWER":
            case "PITCHER_CROP":
               return true;
            default:
               return false;
         }
      }
   }

   public static boolean isFullyGrown(Block var0) {
      if (ASManager.isValid(var0) && isCrop(var0.getType())) {
         if (var0.getType().name().equals("TORCHFLOWER")) {
            return true;
         } else if (!(var0.getBlockData() instanceof Ageable)) {
            return false;
         } else {
            Ageable var1 = (Ageable)var0.getBlockData();
            return var1.getAge() == var1.getMaximumAge();
         }
      } else {
         return false;
      }
   }

   public static boolean isSeeded(Material var0) {
      return getSeed(var0) != var0;
   }

   public static Material getSeed(Material var0) {
      if (!ASManager.isValid(var0)) {
         return var0;
      } else {
         String var1 = var0.name();
         switch (var1) {
            case "WHEAT":
            case "CROPS":
               if (MinecraftVersion.isNew()) {
                  return Material.WHEAT_SEEDS;
               }

               return Material.matchMaterial("SEEDS");
            case "BEETROOTS":
            case "BEETROOT":
               return Material.BEETROOT_SEEDS;
            default:
               return var0;
         }
      }
   }

   public static boolean isWheat(Material var0) {
      if (!ASManager.isValid(var0)) {
         return false;
      } else {
         String var1 = var0.name();
         return var1.equals("WHEAT") || var1.equals("CROPS");
      }
   }

   public static boolean isBeetroot(Material var0) {
      if (!ASManager.isValid(var0)) {
         return false;
      } else {
         String var1 = var0.name();
         return var1.equals("BEETROOT") || var1.equals("BEETROOTS");
      }
   }

   public static int getCropAmount() {
      return MathUtils.randomBetween(2, 5);
   }

   public static int getSeedAmount() {
      return MathUtils.randomBetween(1, 3);
   }

   static {
      materialConversions.put("FARMLAND", "SOIL");
      materialConversions.put("WHEAT_SEEDS", "SEEDS");
      materialConversions.put("WHEAT", "CROPS");
      materialConversions.put("CARROTS", "CARROT");
      materialConversions.put("CARROT", "CARROT_ITEM");
      materialConversions.put("POTATOES", "POTATO");
      materialConversions.put("POTATO", "POTATO_ITEM");
      materialConversions.put("NETHER_WART", "NETHER_WARTS");
   }
}
