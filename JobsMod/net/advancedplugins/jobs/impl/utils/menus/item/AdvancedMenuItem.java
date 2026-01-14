package net.advancedplugins.jobs.impl.utils.menus.item;

import java.util.Arrays;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.VanillaEnchants;
import net.advancedplugins.jobs.impl.utils.items.ConfigItemCreator;
import net.advancedplugins.jobs.impl.utils.items.ItemBuilder;
import net.advancedplugins.jobs.impl.utils.items.ItemFlagFix;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdvancedMenuItem {
   private ConfigurationSection section;
   private Replace replace;
   private int[] slots;
   private String action = null;
   private ItemStack item;
   private boolean glow = false;
   private int amount = 0;
   private String data;

   public AdvancedMenuItem(String var1, ConfigurationSection var2, Replace var3) {
      this.slots = ASManager.getSlots(var1);
      this.replace = var3;
      this.section = var2;
      if (var2.contains("action")) {
         this.action = var2.getString("action");
      }
   }

   public AdvancedMenuItem(int var1, ConfigurationSection var2, Replace var3) {
      this.slots = new int[]{var1};
      this.replace = var3;
      this.section = var2;
      if (var2.contains("action")) {
         this.action = var2.getString("action");
      }
   }

   public AdvancedMenuItem(int[] var1, ConfigurationSection var2, Replace var3) {
      this.slots = var1;
      this.replace = var3;
      this.section = var2;
      if (var2.contains("action")) {
         this.action = var2.getString("action");
      }
   }

   public AdvancedMenuItem(ItemStack var1) {
      this.item = var1;
   }

   public void addToInventory(Inventory var1) {
      ItemStack var2 = this.getItem();
      boolean var3 = this.slots.length > 1;

      for (int var7 : this.slots) {
         if (!var3) {
            var1.setItem(var7, var2);
         } else if (ASManager.isAir(var1.getItem(var7))) {
            var1.setItem(var7, var2);
         }
      }
   }

   public AdvancedMenuItem setGlow() {
      this.glow = true;
      return this;
   }

   public AdvancedMenuItem setAmount(int var1) {
      this.amount = var1;
      return this;
   }

   public ItemStack getItem() {
      if (this.item != null) {
         return this.item;
      } else {
         ItemStack var1 = ConfigItemCreator.fromConfigSection(
            this.section, "", this.replace == null ? null : this.replace.apply(new Replacer()).getPlaceholders(), null
         );
         if (this.glow) {
            ItemBuilder var2 = new ItemBuilder(var1);
            var2.addUnsafeEnchantment(VanillaEnchants.displayNameToEnchant("FORTUNE"), 1);
            var2.addItemFlag(ItemFlagFix.hideAllAttributes());
            var1 = var2.toItemStack();
         }

         if (this.amount != 0) {
            var1.setAmount(this.amount);
         }

         return var1;
      }
   }

   public AdvancedMenuItem setData(String var1) {
      this.data = var1;
      return this;
   }

   public String getSlots() {
      return Arrays.toString(this.slots);
   }

   public AdvancedMenuItem setSlots(int... var1) {
      this.slots = var1;
      return this;
   }

   public String getAction() {
      return this.action;
   }

   public String getData() {
      return this.data;
   }
}
