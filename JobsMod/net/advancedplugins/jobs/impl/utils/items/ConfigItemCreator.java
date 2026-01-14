package net.advancedplugins.jobs.impl.utils.items;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.ColorUtils;
import net.advancedplugins.jobs.impl.utils.MathUtils;
import net.advancedplugins.jobs.impl.utils.Pair;
import net.advancedplugins.jobs.impl.utils.SkullCreator;
import net.advancedplugins.jobs.impl.utils.VanillaEnchants;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ItemsAdderHook;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class ConfigItemCreator {
   private static HashMap<String, String> defaultPaths = null;

   public static ItemStack fromConfigSection(String var0, String var1, Map<String, String> var2, Map<String, String> var3, JavaPlugin var4) {
      File var5 = new File(var4.getDataFolder().getAbsolutePath() + File.separator + var0);
      if (!var5.exists()) {
         sendError("Unknown file!", var0, null, null);
         return new ItemStack(Material.AIR);
      } else {
         YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
         return fromConfigSection(var6, var1, var2, var3);
      }
   }

   public static ItemStack fromConfigSection(FileConfiguration var0, ItemStack var1, String var2, Map<String, String> var3, Map<String, String> var4) {
      return fromConfigSection(var0.getConfigurationSection(""), var1, var2, var3, var4, null);
   }

   public static ItemStack fromConfigSection(
      ConfigurationSection var0, ItemStack var1, String var2, Map<String, String> var3, Map<String, String> var4, Player var5
   ) {
      return fromConfigSection(var0, null, var1, var2, null, var3, var4, null, var5);
   }

   public static ItemStack fromConfigSection(
      ConfigurationSection var0,
      @Nullable ConfigurationSection var1,
      ItemStack var2,
      String var3,
      String var4,
      Map<String, String> var5,
      Map<String, String> var6,
      @Nullable Map<String, String> var7,
      Player var8
   ) {
      Map var9 = (Map)defaultPaths.clone();
      String var10 = "config";
      Map var11 = (Map)defaultPaths.clone();
      if (var7 != null && !var7.isEmpty()) {
         var11.putAll(var7);
      }

      if (var6 != null && !var6.isEmpty()) {
         var9.putAll(var6);
      }

      ItemBuilder var12 = new ItemBuilder(var2);
      String var13 = var12.toItemStack().getType().name();
      if (var0.contains(var3 + "." + (String)var9.get("name"))) {
         String var14 = format(var0.getString(var3 + "." + (String)var9.get("name"), null), var5, var8);
         var12.setName(var14);
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("name"))) {
         String var24 = format(var1.getString(var4 + "." + (String)var11.get("name"), null), var5, var8);
         var12.setName(var24);
      }

      if (var0.contains(var3 + "." + (String)var9.get("lore"))) {
         List var25 = format(var0.getStringList(var3 + "." + (String)var9.get("lore")), var5, var8);
         var12.setLore(var25);
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("lore"))) {
         List var26 = format(var1.getStringList(var4 + "." + (String)var11.get("lore")), var5, var8);
         var12.setLore(var26);
      }

      if (var0.contains(var3 + "." + (String)var9.get("item-flags")) || var1 != null && var1.contains(var4 + "." + (String)var11.get("item-flags"))) {
         Object var27 = var0.contains(var3 + "." + (String)var9.get("item-flags"))
            ? var0.getStringList(var3 + "." + (String)var9.get("item-flags"))
            : (var1 != null ? var1.getStringList(var4 + "." + (String)var11.get("item-flags")) : new ArrayList());
         List var15 = format((List<String>)var27, var5, var8);
         if (!var15.isEmpty() && ((String)var15.get(0)).equalsIgnoreCase("all")) {
            var12.addItemFlag(ItemFlagFix.hideAllAttributes());
         } else {
            for (String var17 : var15) {
               boolean var18 = false;
               String var19 = var17.toUpperCase(Locale.ROOT);

               for (ItemFlag var23 : ItemFlagFix.hideAllAttributes()) {
                  if (var23.name().equals(var19)) {
                     var18 = true;
                     break;
                  }
               }

               if (!var18) {
                  sendError("Specified ItemFlag doesn't exist!", var10, var3, var17);
               } else {
                  var12.addItemFlag(ItemFlag.valueOf(var17));
               }
            }
         }
      }

      if (var0.contains(var3 + "." + (String)var9.get("armor-trim"))) {
         String[] var28 = var0.getString(var3 + "." + (String)var9.get("armor-trim")).split(";");
         String var37 = var28[0];
         String var42 = var28[1];
         var12.setArmorTrim(var37, var42);
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("armor-trim"))) {
         String[] var29 = var1.getString(var4 + "." + (String)var11.get("armor-trim")).split(";");
         String var38 = var29[0];
         String var43 = var29[1];
         var12.setArmorTrim(var38, var43);
      }

      if (var0.contains(var3 + "." + (String)var9.get("custom-model-data"))) {
         if (MinecraftVersion.getVersionNumber() >= 1140) {
            int var30 = var0.getInt(var3 + "." + (String)var9.get("custom-model-data"));
            var12.setCustomModelData(var30);
         }
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("custom-model-data")) && MinecraftVersion.getVersionNumber() >= 1140) {
         int var31 = var1.getInt(var4 + "." + (String)var11.get("custom-model-data"));
         var12.setCustomModelData(var31);
      }

      if (var0.contains(var3 + "." + (String)var9.get("unbreakable"))) {
         var12.setUnbreakable(var0.getBoolean(var3 + "." + (String)var9.get("unbreakable")));
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("unbreakable"))) {
         var12.setUnbreakable(var1.getBoolean(var4 + "." + (String)var11.get("unbreakable")));
      }

      if (var0.contains(var3 + "." + (String)var9.get("enchantments")) || var1 != null && var1.contains(var4 + "." + (String)var11.get("enchantments"))) {
         Object var32 = var0.contains(var3 + "." + (String)var9.get("enchantments"))
            ? var0.getStringList(var3 + "." + (String)var9.get("enchantments"))
            : (var1 != null ? var1.getStringList(var4 + "." + (String)var11.get("enchantments")) : new ArrayList());

         for (String var47 : format((List<String>)var32, var5, var8)) {
            Pair var50 = ASManager.parseEnchantment(var47);
            if (var50 != null) {
               Enchantment var53 = VanillaEnchants.displayNameToEnchant((String)var50.getKey());
               if (var53 == null) {
                  sendError("Specified vanilla enchantment doesn't exist!", var10, var3, var50.getKey());
               } else {
                  var12.addUnsafeEnchantment(var53, (Integer)var50.getValue());
               }
            }
         }
      }

      if (var0.contains(var3 + "." + (String)var9.get("custom-enchantments"))
         || var1 != null && var1.contains(var4 + "." + (String)var11.get("custom-enchantments"))) {
         Object var33 = var0.contains(var3 + "." + (String)var9.get("custom-enchantments"))
            ? var0.getStringList(var3 + "." + (String)var9.get("custom-enchantments"))
            : (var1 != null ? var1.getStringList(var4 + "." + (String)var11.get("custom-enchantments")) : new ArrayList());

         for (String var48 : format((List<String>)var33, var5, var8)) {
            Pair var51 = ASManager.parseEnchantment(var48);
            if (var51 != null) {
               var12.addCustomEnchantment((String)var51.getKey(), (Integer)var51.getValue());
            }
         }
      }

      if ((var13.contains("LEATHER_") || var13.contains("FIREWORK_STAR"))
         && (var0.contains(var3 + "." + (String)var9.get("rgb-color")) || var1 != null && var1.contains(var4 + "." + (String)var11.get("rgb-color")))) {
         String var34 = var0.contains(var3 + "." + (String)var9.get("rgb-color"))
            ? var0.getString(var3 + "." + (String)var9.get("rgb-color"))
            : (var1 != null ? var1.getString(var4 + "." + (String)var11.get("rgb-color")) : null);
         String var41 = format(var34, var5, var8);
         String[] var46 = var41.split(";");
         if (var46.length != 3) {
            sendError("RGB color must contain 3 values in the format \"255;255;255\"!", var10, var3, var41);
            return new ItemStack(Material.AIR);
         }

         if (!MathUtils.isInteger(var46[0]) || !MathUtils.isInteger(var46[1]) || !MathUtils.isInteger(var46[2])) {
            sendError("RGB values must be between 0-255!", var10, var3, var46[0]);
            return new ItemStack(Material.AIR);
         }

         int var49 = MathUtils.clamp(Integer.parseInt(var46[0]), 0, 255);
         int var52 = MathUtils.clamp(Integer.parseInt(var46[1]), 0, 255);
         int var54 = MathUtils.clamp(Integer.parseInt(var46[2]), 0, 255);
         Color var55 = Color.fromRGB(var49, var52, var54);
         if (var13.contains("LEATHER_")) {
            var12.setColor(var55);
         } else if (var13.contains("FIREWORK_STAR")) {
            FireworkEffectMeta var56 = (FireworkEffectMeta)var12.getItemMeta();
            var56.setEffect(FireworkEffect.builder().withColor(var55).build());
         }
      }

      if (var0.contains(var3 + "." + (String)var9.get("force-glow"))) {
         boolean var35 = var0.getBoolean(var3 + "." + (String)var9.get("force-glow"));
         if (var35) {
            var12.setGlowing(true);
         }
      } else if (var1 != null && var1.contains(var4 + "." + (String)var11.get("force-glow"))) {
         boolean var36 = var1.getBoolean(var4 + "." + (String)var11.get("force-glow"));
         if (var36) {
            var12.setGlowing(true);
         }
      }

      return var12.toItemStack();
   }

   public static ItemStack fromConfigSection(FileConfiguration var0, String var1, Map<String, String> var2, Map<String, String> var3) {
      return fromConfigSection(var0.getConfigurationSection(""), var1, var2, var3);
   }

   public static ItemStack fromConfigSection(ConfigurationSection var0, String var1, Map<String, String> var2, Map<String, String> var3) {
      return fromConfigSection(var0, var1, var2, var3, null);
   }

   public static ItemStack fromConfigSection(
      ConfigurationSection var0,
      ConfigurationSection var1,
      String var2,
      String var3,
      Map<String, String> var4,
      Map<String, String> var5,
      Map<String, String> var6
   ) {
      return fromConfigSection(var0, var1, var2, var3, var4, var5, var6, null);
   }

   public static ItemStack fromConfigSection(ConfigurationSection var0, String var1, Map<String, String> var2, Map<String, String> var3, Player var4) {
      return fromConfigSection(var0, null, var1, null, var2, var3, null, var4);
   }

   public static ItemStack fromConfigSection(
      ConfigurationSection var0,
      @Nullable ConfigurationSection var1,
      String var2,
      @Nullable String var3,
      Map<String, String> var4,
      Map<String, String> var5,
      @Nullable Map<String, String> var6,
      Player var7
   ) {
      String var8 = "config";
      Map var9 = (Map)defaultPaths.clone();
      Map var10 = (Map)defaultPaths.clone();
      if (var6 != null && !var6.isEmpty()) {
         var10.putAll(var6);
      }

      if (var5 != null && !var5.isEmpty()) {
         var9.putAll(var5);
      }

      String var11 = var0.contains(var2 + "." + (String)var9.get("type"))
         ? var0.getString(var2 + "." + (String)var9.get("type"), null)
         : (var1 != null ? var1.getString(var3 + "." + (String)var10.get("type")) : null);
      String var12 = var11 != null ? format(var11, var4, var7) : null;
      byte var13 = (byte)(
         var0.contains(var2 + "." + (String)var9.get("id"))
            ? var0.getInt(var2 + "." + (String)var9.get("id"))
            : (var1 != null ? var1.getInt(var3 + "." + (String)var10.get("id")) : 0)
      );
      int var14 = MathUtils.clamp(
         ASManager.parseInt(
            var0.contains(var2 + "." + (String)var9.get("amount"))
               ? var0.getString(var2 + "." + (String)var9.get("amount"), "1")
               : (var1 != null ? var1.getString(var3 + "." + (String)var10.get("amount"), "1") : "1"),
            1
         ),
         1,
         64
      );
      if (var0.contains(var2 + ".advanced-heads")) {
         var0.get(var2 + ".advanced-heads");
      } else if (var1 != null) {
         var1.get(var3 + ".advanced-heads");
      } else {
         Object var10000 = null;
      }

      String var16 = var0.contains(var2 + "." + (String)var9.get("head"))
         ? var0.getString(var2 + "." + (String)var9.get("head"))
         : (var1 != null ? var1.getString(var3 + "." + (String)var10.get("head")) : null);
      String var17 = var0.contains(var2 + "." + (String)var9.get("itemsadder"))
         ? var0.getString(var2 + "." + (String)var9.get("itemsadder"))
         : (var1 != null ? var1.getString(var3 + "." + (String)var10.get("itemsadder")) : null);
      ItemStack var18;
      if (var17 != null) {
         var18 = ((ItemsAdderHook)HooksHandler.getHook(HookPlugin.ITEMSADDER)).getByName(var17);
      } else if (var16 != null) {
         var18 = SkullCreator.itemFromBase64(var16);
      } else {
         var18 = ASManager.matchMaterial(var12, var14, var13);
      }

      if (var18 == null) {
         sendError("Specified material doesn't exist!", var8, var2, var12);
         return new ItemStack(Material.AIR);
      } else {
         return fromConfigSection(var0, var1, var18, var2, var3, var4, var5, var10, var7);
      }
   }

   private static String format(String var0, Map<String, String> var1, Player var2) {
      var0 = placeholders(var0, var1, var2);
      return ColorUtils.format(var0);
   }

   private static List<String> format(List<String> var0, Map<String, String> var1, Player var2) {
      return ColorUtils.format(placeholders(var0, var1, var2));
   }

   private static String placeholders(String var0, Map<String, String> var1, Player var2) {
      if (var1 != null && var0 != null) {
         for (Entry var4 : var1.entrySet()) {
            if (var0.contains("%description%") && ((String)var4.getKey()).contains("%description%") && ((String)var4.getValue()).contains("\n")) {
               String var11 = var0;
               var0 = "";
               String[] var12 = ((String)var4.getValue()).split("\n");
               StringBuilder var13 = new StringBuilder(var0);

               for (int var14 = 0; var14 < var12.length; var14++) {
                  var13.append(var11.replace((CharSequence)var4.getKey(), var12[var14]));
                  if (var14 + 1 != var12.length) {
                     var13.append("\n");
                  }
               }

               var0 = var13.toString();
            } else if (var0.contains("%level-description%")
               && ((String)var4.getKey()).contains("%level-description%")
               && ((String)var4.getValue()).contains("\n")) {
               String var5 = var0;
               var0 = "";
               String[] var6 = ((String)var4.getValue()).split("\n");
               StringBuilder var7 = new StringBuilder(var0);

               for (int var8 = 0; var8 < var6.length; var8++) {
                  var7.append(var5.replace((CharSequence)var4.getKey(), var6[var8]));
                  if (var8 + 1 != var6.length) {
                     var7.append("\n");
                  }
               }

               var0 = var7.toString();
            } else if (var4.getValue() != null) {
               var0 = var0.replace((CharSequence)var4.getKey(), (CharSequence)var4.getValue());
            }
         }
      }

      return var0 != null && var2 != null ? Text.parsePapi(var0, var2) : var0;
   }

   private static List<String> placeholders(List<String> var0, Map<String, String> var1, Player var2) {
      ArrayList var3 = new ArrayList();
      if (var1 == null) {
         return (List<String>)(var2 != null ? new ArrayList<>(var0.stream().map(var1x -> Text.parsePapi(var1x, var2)).toList()) : var0);
      } else {
         for (String var5 : var0) {
            var5 = placeholders(var5, var1, var2);
            if (var5.contains("\n")) {
               String[] var6 = var5.split("\\n");
               String var7 = "";

               for (String var11 : var6) {
                  var3.add(var7 + var11);
                  var7 = ColorUtils.getLastColor(var11);
               }
            } else {
               var3.add(var5);
            }
         }

         return var3;
      }
   }

   private static String addPunctuation(String var0) {
      boolean var1 = var0.endsWith(".") || var0.endsWith("!") || var0.endsWith("?");
      if (!var1) {
         var0 = var0 + ".";
      }

      return var0;
   }

   private static void sendError(String var0, String var1, String var2, Object var3) {
      Bukkit.getLogger()
         .severe("Something went wrong while creating an item! " + addPunctuation(var0) + " File: " + var1 + "  Config Path: " + var2 + "  Value: " + var3);
   }

   public static void setDefaultPaths(HashMap<String, String> var0) {
      defaultPaths = var0;
   }

   public static HashMap<String, String> getDefaultPaths() {
      return defaultPaths;
   }

   static {
      HashMap var0 = new HashMap();
      var0.put("type", "type");
      var0.put("id", "id");
      var0.put("amount", "amount");
      var0.put("name", "name");
      var0.put("lore", "lore");
      var0.put("item-flags", "item-flags");
      var0.put("custom-model-data", "custom-model-data");
      var0.put("force-glow", "force-glow");
      var0.put("enchantments", "enchantments");
      var0.put("custom-enchantments", "custom-enchantments");
      var0.put("rgb-color", "rgb-color");
      var0.put("itemsadder", "itemsadder");
      var0.put("armor-trim", "armor-trim");
      var0.put("unbreakable", "unbreakable");
      var0.put("head", "head");
      var0.put("owner", "head");
      setDefaultPaths(var0);
   }
}
