package net.advancedplugins.jobs.impl.actions.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftQuest extends ActionContainer {
   private final JavaPlugin plugin;
   private final Map<UUID, Map<CraftQuest.CraftAntiAbuseType, CraftQuest.AntiAbuseCounter>> antiAbuseCache;

   public CraftQuest(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
      this.antiAbuseCache = new HashMap<>();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCraftItem(CraftItemEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      ItemStack var3 = var1.getCurrentItem();
      if (var3 != null) {
         Material var4 = Arrays.stream(var1.getInventory().getMatrix())
            .filter(var0 -> var0 != null && var0.getType() != Material.AIR)
            .findFirst()
            .<Material>map(ItemStack::getType)
            .orElse(Material.AIR);
         this.getCraftedAmount(var1).thenAccept(var4x -> {
            Recipe var5 = var1.getRecipe();
            ItemStack var6 = var5.getResult();
            Material var7 = var6.getType();
            String var8 = "";
            if (var7 == Material.AIR && var5 instanceof ComplexRecipe) {
               var8 = ((ComplexRecipe)var5).getKey().getKey();
            }

            int var9 = var4x;
            CraftQuest.CraftAntiAbuseType var10 = this.usAntiAbuse(var4, var7);
            if (var10 != null) {
               var9 = 0;
               this.antiAbuseCache.putIfAbsent(var2.getUniqueId(), new HashMap<>());
               this.antiAbuseCache.get(var2.getUniqueId()).putIfAbsent(var10, new CraftQuest.AntiAbuseCounter());
               CraftQuest.AntiAbuseCounter var11 = this.antiAbuseCache.get(var2.getUniqueId()).get(var10);
               if (var10.first == var7) {
                  int var12 = var11.second - var4x / var10.points;
                  if (var12 < 0) {
                     var9 = Math.min(var4x, Math.abs(var12) * var10.points);
                  }

                  var11.first = var11.first + var4x;
                  var11.second = Math.max(0, var12);
               } else {
                  int var13 = var11.first - var4x * var10.points;
                  if (var13 < 0) {
                     var9 = Math.min(var4x, Math.abs(var13) / var10.points);
                  }

                  var11.first = Math.max(0, var13);
                  var11.second = var11.second + var4x;
               }
            }

            if (var9 > 0) {
               this.executionBuilder("craft").player(var2).progress(var9).rootFrom(var8.isEmpty() ? var6 : var8).buildAndExecute();
            }
         });
      }
   }

   private CompletableFuture<Integer> getCraftedAmount(CraftItemEvent var1) {
      ClickType var2 = var1.getClick();
      if (var2 != ClickType.SHIFT_LEFT && var2 != ClickType.SHIFT_RIGHT) {
         ItemStack var10 = var1.getCurrentItem();
         ItemStack var11 = var1.getCursor();
         ItemStack var12 = var1.getRecipe().getResult();
         if (var10 != null
            && (var11 == null || var11.getAmount() <= 0 || var11.isSimilar(var12) && var11.getAmount() + var12.getAmount() <= var11.getMaxStackSize())) {
            if (var1.getClick() == ClickType.NUMBER_KEY) {
               int var14 = var1.getHotbarButton();
               ItemStack var15 = var1.getWhoClicked().getInventory().getItem(var14);
               if (var15 != null && var15.getType() != Material.AIR) {
                  CompletableFuture var16 = new CompletableFuture();
                  var16.cancel(false);
                  return var16;
               }
            }

            return CompletableFuture.completedFuture(var10.getAmount());
         } else {
            CompletableFuture var13 = new CompletableFuture();
            var13.cancel(false);
            return var13;
         }
      } else {
         CompletableFuture var3 = new CompletableFuture();
         Player var4 = (Player)var1.getWhoClicked();
         Recipe var5 = var1.getRecipe();
         Material var6 = var5.getResult().getType();
         if (var6 == Material.AIR && var5 instanceof ComplexRecipe) {
            try {
               var6 = Material.valueOf(((ComplexRecipe)var5).getKey().getKey().toUpperCase());
            } catch (Exception var9) {
            }
         }

         int var7 = this.calculateAmount(var6, var4.getInventory().getStorageContents());
         Material var8 = var6;
         FoliaScheduler.runTaskLater(this.plugin, () -> {
            int var5x = this.calculateAmount(var8, var4.getInventory().getStorageContents());
            int var6x = var5x - var7;
            if (var6x < 1) {
               var3.cancel(false);
            } else {
               var3.complete(var6x);
            }
         }, 1L);
         return var3;
      }
   }

   private int calculateAmount(Material var1, ItemStack[] var2) {
      int var3 = 0;

      for (ItemStack var7 : var2) {
         if (var7 != null && var7.getType() == var1) {
            var3 += var7.getAmount();
         }
      }

      return var3;
   }

   private CraftQuest.CraftAntiAbuseType usAntiAbuse(Material var1, Material var2) {
      for (CraftQuest.CraftAntiAbuseType var6 : CraftQuest.CraftAntiAbuseType.values()) {
         if (var6.first == var1 && var6.second == var2 || var6.first == var2 && var6.second == var1) {
            return var6;
         }
      }

      return null;
   }

   class AntiAbuseCounter {
      private int first = 0;
      private int second = 0;

      public int getFirst() {
         return this.first;
      }

      public int getSecond() {
         return this.second;
      }

      public void setFirst(int var1) {
         this.first = var1;
      }

      public void setSecond(int var1) {
         this.second = var1;
      }

      public AntiAbuseCounter() {
      }
   }

   static enum CraftAntiAbuseType {
      IRON_NUGGETS(Material.IRON_NUGGET, Material.IRON_INGOT, 9),
      GOLD_NUGGETS(Material.GOLD_NUGGET, Material.GOLD_INGOT, 9),
      COAL(Material.COAL, Material.COAL_BLOCK, 9),
      COPPER(Material.COPPER_INGOT, Material.COPPER_BLOCK, 9),
      IRON(Material.IRON_INGOT, Material.IRON_BLOCK, 9),
      GOLD(Material.GOLD_INGOT, Material.GOLD_BLOCK, 9),
      REDSTONE(Material.REDSTONE, Material.REDSTONE_BLOCK, 9),
      LAPIS(Material.LAPIS_LAZULI, Material.LAPIS_BLOCK, 9),
      DIAMOND(Material.DIAMOND, Material.DIAMOND_BLOCK, 9),
      EMERALD(Material.EMERALD, Material.EMERALD_BLOCK, 9),
      NETHERITE(Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK, 9),
      RAW_COPPER(Material.RAW_COPPER, Material.RAW_COPPER_BLOCK, 9),
      RAW_IRON(Material.RAW_IRON, Material.RAW_IRON_BLOCK, 9),
      RAW_GOLD(Material.RAW_GOLD, Material.RAW_GOLD_BLOCK, 9),
      BONE(Material.BONE_MEAL, Material.BONE_BLOCK, 9),
      HAY(Material.WHEAT, Material.HAY_BLOCK, 9);

      private final Material first;
      private final Material second;
      private final int points;

      private CraftAntiAbuseType(final Material nullxx, final Material nullxxx, final int nullxxxx) {
         this.first = nullxx;
         this.second = nullxxx;
         this.points = nullxxxx;
      }

      public Material getFirst() {
         return this.first;
      }

      public Material getSecond() {
         return this.second;
      }

      public int getPoints() {
         return this.points;
      }
   }
}
