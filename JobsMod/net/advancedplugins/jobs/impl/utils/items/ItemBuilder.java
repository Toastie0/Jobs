package net.advancedplugins.jobs.impl.utils.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.SkullCreator;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.pdc.PDCHandler;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ItemBuilder {
   private ItemStack is;
   private ItemMeta im;
   private ConfigurationSection section;
   private boolean glow = false;

   public ItemBuilder(Material var1) {
      this(var1, 1);
   }

   public ItemBuilder(ItemStack var1) {
      this.is = var1;
      this.im = var1.getItemMeta();
   }

   public ItemBuilder(ConfigurationSection var1) {
      this.section = var1;
      ItemStack var2 = ASManager.matchMaterial(var1.getString("type"), 1, 0);
      if (var2 == null) {
         throw new IllegalArgumentException("Could create item from config section: " + var1.getCurrentPath() + " because the type was null.");
      } else {
         String var3 = var1.isString("name") ? Text.modify(var1.getString("name")) : "";
         Object var4 = var1.isList("lore") ? var1.getStringList("lore") : new ArrayList();
         int var5 = var1.isInt("custom-model-data") ? var1.getInt("custom-model-data") : 0;
         int var6 = var1.isInt("amount") ? var1.getInt("amount") : 1;
         int var7 = var1.isInt("advanced-heads-id") ? var1.getInt("advanced-heads-id") : 0;
         boolean var8 = var1.isBoolean("force-glow") && var1.getBoolean("force-glow");
         String var9 = var1.isString("owner") ? var1.getString("owner") : null;
         ItemStack var10 = new ItemStack(var2);
         ItemMeta var11 = var10.getItemMeta();

         assert var11 != null;

         var11.setDisplayName(var3);
         var11.setLore(Text.modify((List<String>)var4));
         if (var5 != 0) {
            var11.setCustomModelData(var5);
         }

         var10.setAmount(var6);
         var10.setItemMeta(var11);
         this.glow = var8;
         this.is = var10;
         this.im = this.is.getItemMeta();
         if (var7 != 0 && Bukkit.getServer().getPluginManager().isPluginEnabled("AdvancedHeads")) {
            this.im = this.is.getItemMeta();
         }

         if (var9 != null) {
            this.is = SkullCreator.itemWithBase64(this.is, var9);
            this.im = this.is.getItemMeta();
         }
      }
   }

   public ItemBuilder(Material var1, int var2) {
      this.is = new ItemStack(var1, var2);
      this.im = this.is.getItemMeta();
   }

   public ItemBuilder(Material var1, int var2, byte var3) {
      this.is = new ItemStack(var1, var2, var3);
      this.im = this.is.getItemMeta();
   }

   public Optional<ConfigurationSection> getConfigSection() {
      return this.section == null ? Optional.empty() : Optional.of(this.section);
   }

   public ItemBuilder setDurability(short var1) {
      this.is.setDurability(var1);
      return this;
   }

   public ItemBuilder setType(Material var1) {
      this.is.setType(var1);
      return this;
   }

   public ItemBuilder setName(String var1) {
      this.im.setDisplayName(var1);
      return this;
   }

   public ItemBuilder addUnsafeEnchantment(Enchantment var1, int var2) {
      if (this.im instanceof EnchantmentStorageMeta var3) {
         var3.addStoredEnchant(var1, var2, true);
         this.im = var3;
      } else {
         this.im.addEnchant(var1, var2, true);
      }

      return this;
   }

   public ItemBuilder removeEnchantment(Enchantment var1) {
      if (this.im instanceof EnchantmentStorageMeta var2) {
         var2.removeStoredEnchant(var1);
      } else {
         this.im.removeEnchant(var1);
      }

      return this;
   }

   public ItemBuilder setSkullOwner(String var1) {
      try {
         SkullMeta var2 = (SkullMeta)this.im;
         var2.setOwner(var1);
      } catch (ClassCastException var3) {
      }

      return this;
   }

   public ItemBuilder addEnchantment(Enchantment var1, int var2) {
      this.im.addEnchant(var1, var2, true);
      return this;
   }

   public ItemBuilder addEnchantments(Map<Enchantment, Integer> var1) {
      this.is.addEnchantments(var1);
      return this;
   }

   public ItemBuilder setInfinityDurability() {
      this.is.setDurability((short)32767);
      return this;
   }

   public ItemBuilder setLore(String... var1) {
      this.im.setLore(Arrays.asList(var1));
      return this;
   }

   public ItemBuilder setLore(List<String> var1) {
      this.im.setLore(var1);
      this.is.setItemMeta(this.im);
      return this;
   }

   public ItemBuilder removeLoreLine(String var1) {
      ArrayList var2 = new ArrayList(this.im.getLore());
      if (!var2.contains(var1)) {
         return this;
      } else {
         var2.remove(var1);
         this.im.setLore(var2);
         return this;
      }
   }

   public ItemBuilder removeLoreLine(int var1) {
      ArrayList var2 = new ArrayList(this.im.getLore());
      if (var1 >= 0 && var1 <= var2.size()) {
         var2.remove(var1);
         this.im.setLore(var2);
         return this;
      } else {
         return this;
      }
   }

   public ItemBuilder addLoreLine(String var1) {
      ArrayList var2 = new ArrayList();
      if (this.im.hasLore()) {
         var2 = new ArrayList(this.im.getLore());
      }

      var2.add(var1);
      this.im.setLore(var2);
      return this;
   }

   public ItemBuilder addLoreLine(String var1, int var2) {
      ArrayList var3 = new ArrayList(this.im.getLore());
      var3.set(var2, var1);
      this.im.setLore(var3);
      return this;
   }

   public ItemBuilder setColor(Color var1) {
      if (this.im instanceof LeatherArmorMeta var2) {
         var2.setColor(var1);
      } else if (this.im instanceof FireworkEffectMeta var3) {
         var3.setEffect(FireworkEffect.builder().withColor(var1).build());
      }

      return this;
   }

   public ItemBuilder setItemFlags(ItemFlag... var1) {
      this.im.addItemFlags(var1);
      return this;
   }

   public ItemBuilder setArmorTrim(String var1, String var2) {
      ArmorMeta var3 = (ArmorMeta)this.im;
      TrimMaterial var4 = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var1.toLowerCase()));
      TrimPattern var5 = (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(var2.toLowerCase()));
      var3.setTrim(new ArmorTrim(var4, var5));
      return this;
   }

   public ItemBuilder addAttribute(Attribute var1, AttributeModifier var2) {
      this.im.addAttributeModifier(var1, var2);
      return this;
   }

   public ItemBuilder setAmount(int var1) {
      this.is.setAmount(var1);
      return this;
   }

   public ItemBuilder addItemFlag(ItemFlag... var1) {
      ItemFlagFix.fix(this.im);
      this.im.addItemFlags(var1);
      return this;
   }

   public ItemBuilder setCustomModelData(Integer var1) {
      if (MinecraftVersion.getVersionNumber() >= 1140) {
         this.im.setCustomModelData(var1);
      }

      return this;
   }

   public ItemBuilder setItemModel(String var1) {
      if (MinecraftVersion.getVersionNumber() >= 1212) {
         this.im.setItemModel(NamespacedKey.fromString(var1));
      }

      return this;
   }

   public ItemBuilder addNBTTag(String var1, String var2) {
      this.is.setItemMeta(this.im);
      this.is = NBTapi.addNBTTag(var1, var2, this.is);
      this.im = this.is.getItemMeta();
      return this;
   }

   public ItemBuilder addPDC(String var1, String var2) {
      PDCHandler.setString(this.im, var1, var2);
      return this;
   }

   public ItemBuilder setGlowing(boolean var1) {
      this.glow = var1;
      return this;
   }

   public ItemMeta getItemMeta() {
      return this.im;
   }

   public ItemStack toItemStack() {
      this.is.setItemMeta(this.im);
      if (this.glow) {
         this.is = ASManager.makeItemGlow(this.is);
      }

      return this.is;
   }

   public ItemBuilder setUnbreakable(boolean var1) {
      if (MinecraftVersion.getVersionNumber() >= 1110) {
         assert this.im != null;

         this.im.setUnbreakable(var1);
      }

      return this;
   }

   public ItemBuilder addCustomEnchantment(String var1, int var2) {
      this.is.setItemMeta(this.im);
      if (!HooksHandler.isEnabled(HookPlugin.ADVANCEDENCHANTMENTS)) {
         return this;
      } else {
         this.is = AEAPI.applyEnchant(var1, var2, this.is);
         this.im = this.is.getItemMeta();
         return this;
      }
   }

   public ItemBuilder setCustomNBT(String var1) {
      Bukkit.getUnsafe().modifyItemStack(this.is, var1);
      return this;
   }

   public void addLoreLines(List<String> var1) {
      ArrayList var2 = new ArrayList();
      if (this.im.hasLore()) {
         var2 = new ArrayList(this.im.getLore());
      }

      var2.addAll(Text.modify(var1));
      this.im.setLore(var2);
   }
}
