package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class BrewingQuest extends ActionContainer {
   private NamespacedKey key;

   public BrewingQuest(JavaPlugin var1) {
      super(var1);
      this.key = new NamespacedKey(var1, "battlepass");
   }

   @EventHandler
   public void on(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null && var1.getClickedInventory() instanceof BrewerInventory) {
         Player var2 = (Player)var1.getWhoClicked();
         this.handleItem(var2, var1.getCurrentItem());
      }
   }

   private void handleItem(Player var1, ItemStack var2) {
      if (var2 != null && var2.getType() != Material.AIR) {
         if (var2.getType().name().contains("POTION")) {
            boolean var3 = ActionRegistry.getRegistry().isBrewingProtection();
            ItemMeta var4 = var2.getItemMeta();
            PersistentDataContainer var5 = var4.getPersistentDataContainer();
            if (!var3 || !var5.has(this.key, PersistentDataType.STRING)) {
               PotionMeta var10 = (PotionMeta)var4;
               boolean var7;
               boolean var8;
               String var9;
               if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R2)) {
                  PotionType var6 = var10.getBasePotionType();
                  var9 = var6 == null ? ActionRegistry.NONE_ROOT : var6.name();
                  var7 = var9.startsWith("LONG_");
                  var8 = var9.startsWith("STRONG_");
               } else {
                  PotionData var11 = var10.getBasePotionData();
                  PotionType var12 = var11.getType();
                  var9 = var12 == null ? ActionRegistry.NONE_ROOT : var12.name();
                  var7 = var11.isExtended();
                  var8 = var11.isUpgraded();
               }

               if (!var9.equalsIgnoreCase(PotionType.WATER.name())) {
                  this.executionBuilder("brew")
                     .player(var1)
                     .root(var9)
                     .subRoot("isExtended", String.valueOf(var7))
                     .subRoot("isUpgraded", String.valueOf(var8))
                     .subRoot(var2)
                     .progressSingle()
                     .buildAndExecute();
                  if (var3) {
                     var5.set(this.key, PersistentDataType.STRING, "claimed");
                  }

                  var2.setItemMeta(var4);
               }
            }
         }
      }
   }
}
