package net.advancedplugins.jobs.impl.utils;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ItemsAdderHook;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class ItemDurability {
   @Nullable
   private final LivingEntity itemHolder;
   private ItemStack item;
   private int dealtDamage = 0;
   private final boolean itemsAdder;

   public ItemDurability(ItemStack var1) {
      this(null, var1);
   }

   public ItemDurability(@Nullable LivingEntity var1, ItemStack var2) {
      this.itemHolder = var1;
      this.item = var2 == null ? new ItemStack(Material.AIR) : var2;
      this.itemsAdder = HooksHandler.getHook(HookPlugin.ITEMSADDER) != null
         && ((ItemsAdderHook)HooksHandler.getHook(HookPlugin.ITEMSADDER)).isCustomItem(this.item);
   }

   public ItemStack getItemStack() {
      if (this.item.getAmount() == 0) {
         this.item.setAmount(1);
      }

      if (this.item.getType().getMaxDurability() == 0) {
         return this.item;
      } else {
         return this.isBroken() && !ASManager.isUnbreakable(this.item) ? new ItemStack(Material.AIR) : this.item;
      }
   }

   public int getUnbreakingLevel() {
      return this.item.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("unbreaking"));
   }

   public ItemDurability damageItem(int var1) {
      try {
         if (!ASManager.isDamageable(this.item.getType()) || this.item.getType().name().contains("SKULL") || ASManager.isUnbreakable(this.item)) {
            return this;
         }

         if (var1 < 0) {
            this.healItem(var1);
            return this;
         }

         if (this.itemHolder instanceof Player) {
            PlayerItemDamageEvent var2 = new PlayerItemDamageEvent((Player)this.itemHolder, this.item, var1);
            Bukkit.getPluginManager().callEvent(var2);
            if (var2.isCancelled()) {
               return this;
            }

            var1 = var2.getDamage();
         }

         int var4 = this.getMaxDurability();
         if (this.getDurability() + var1 > var4) {
            this.setDurability(var4);
            return this;
         }

         this.setDurability(this.getDurability() + var1);
         this.dealtDamage += var1;
      } catch (Exception var3) {
      }

      return this;
   }

   public boolean isBroken() {
      return this.getDurability() >= this.getMaxDurability();
   }

   public ItemDurability healItem(int var1) {
      var1 = (short)Math.abs(var1);
      if (!ASManager.isDamageable(this.item.getType())) {
         return this;
      } else if (this.item.getType().name().contains("SKULL")) {
         return this;
      } else {
         if (this.itemHolder instanceof Player) {
            PlayerItemDamageEvent var2 = new PlayerItemDamageEvent((Player)this.itemHolder, this.item, var1);
            Bukkit.getPluginManager().callEvent(var2);
            if (var2.isCancelled()) {
               return this;
            }

            var1 = var2.getDamage();
         }

         if (this.getDurability() - var1 < 0) {
            this.repairItem();
            return this;
         } else {
            this.setDurability(this.getDurability() - var1);
            return this;
         }
      }
   }

   public ItemDurability handleDurabilityChange(int var1) {
      return var1 < 0 ? this.damageItem((short)(-var1)) : this.healItem((short)var1);
   }

   public int getMaxDurability() {
      return this.itemsAdder
         ? ((ItemsAdderHook)HooksHandler.getHook(HookPlugin.ITEMSADDER)).getCustomItemMaxDurability(this.item)
         : this.item.getType().getMaxDurability();
   }

   public int getDurability() {
      return this.itemsAdder
         ? this.getMaxDurability() - ((ItemsAdderHook)HooksHandler.getHook(HookPlugin.ITEMSADDER)).getCustomItemDurability(this.item)
         : this.item.getDurability();
   }

   public ItemDurability setDurability(int var1) {
      if (this.itemsAdder) {
         this.item = ((ItemsAdderHook)HooksHandler.getHook(HookPlugin.ITEMSADDER))
            .setCustomItemDurability(this.item, var1 < this.getMaxDurability() ? this.getMaxDurability() - var1 : -1);
         return this;
      } else {
         if (var1 >= this.getMaxDurability() && this.itemHolder != null && this.itemHolder instanceof Player && this.item.getItemMeta() instanceof Damageable) {
            Bukkit.getPluginManager().callEvent(new PlayerItemBreakEvent((Player)this.itemHolder, this.item));
         }

         this.setDurabilityVersionSave(var1);
         return this;
      }
   }

   private int getDurabilityVersionSafe() {
      if (MinecraftVersion.getVersionNumber() >= 1130) {
         return this.item.getItemMeta() instanceof Damageable var2 ? var2.getDamage() : -1;
      } else {
         return this.item.getDurability();
      }
   }

   private void setDurabilityVersionSave(int var1) {
      if (MinecraftVersion.getVersionNumber() >= 1130) {
         ItemMeta var2 = this.item.getItemMeta();
         if (var2 instanceof Damageable var3) {
            var3.setDamage(var1);
            this.item.setItemMeta(var2);
         }
      } else {
         this.item.setDurability((short)var1);
      }
   }

   public ItemDurability repairItem() {
      this.setDurability(0);
      return this;
   }

   public int getDealtDamage() {
      return this.dealtDamage;
   }
}
