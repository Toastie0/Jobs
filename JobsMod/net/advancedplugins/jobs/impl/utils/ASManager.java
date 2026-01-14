package net.advancedplugins.jobs.impl.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.StringConcatFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.advancedplugins.jobs.impl.utils.annotations.ConfigKey;
import net.advancedplugins.jobs.impl.utils.evalex.Expression;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.impl.utils.nbt.backend.ClassWrapper;
import net.advancedplugins.jobs.impl.utils.nbt.backend.ReflectionMethod;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASManager {
   private static final HashSet<String> silkOnly = new HashSet<>(Arrays.asList("LEAVE", "LEAVES", "MUSHROOM_STEM", "TURTLE_EGG", "CORAL"));
   private static final List<Integer> validSizes = new ArrayList<>(Arrays.asList(9, 18, 27, 36, 45, 54));
   private static final ImmutableList<String> vegetationBlockNames = ImmutableList.builder()
      .addAll(
         Arrays.asList(
            "GRASS",
            "TALL_GRASS",
            "FERN",
            "LARGE_FERN",
            "SEAGRASS",
            "TALL_SEAGRASS",
            "DANDELION",
            "POPPY",
            "BLUE_ORCHID",
            "ALLIUM",
            "AZURE_BLUET",
            "RED_TULIP",
            "ORANGE_TULIP",
            "WHITE_TULIP",
            "PINK_TULIP",
            "OXEYE_DAISY",
            "CORNFLOWER",
            "LILY_OF_THE_VALLEY",
            "WITHER_ROSE",
            "SUNFLOWER",
            "LILAC",
            "ROSE_BUSH",
            "PEONY"
         )
      )
      .build();
   public static boolean debug = false;
   private static JavaPlugin instance;
   private static HashMap<Integer, String> damages = new HashMap<>();
   private static HashMap<String, String> newMaterials = new HashMap<>();

   public static void setInstance(JavaPlugin var0) {
      instance = var0;

      try {
         File var1 = new File(var0.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

         try (ZipFile var2 = new ZipFile(var1)) {
            String var3 = Registry.class.getName().replace('.', '/') + ".class";
            String var4 = getInstance().getDescription().getMain().replace('.', '/') + ".class";
            String var5 = "plugin.yml";
            new Date(var2.getEntry(var3).getTime());
            new Date(var2.getEntry(var4).getTime());
            new Date(var2.getEntry(var5).getTime());
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }
   }

   public static int hexToDecimal(String var0) {
      return Integer.parseInt(var0, 16);
   }

   @Contract("null, _ -> fail")
   public static void notNull(Object var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1.concat(" cannot be null."));
      }
   }

   @Contract("!null, _ -> fail")
   public static void isNull(Object var0, String var1) {
      if (var0 != null) {
         throw new IllegalArgumentException(var1);
      }
   }

   @Nullable
   public static Player getPlayerInsensitive(@NotNull String var0) {
      for (Player var2 : Bukkit.getOnlinePlayers()) {
         if (var2.getName().equalsIgnoreCase(var0)) {
            return var2;
         }
      }

      return null;
   }

   public static int similarityPercentage(String var0, String var1) {
      if (var0 != null && var1 != null && !var0.isEmpty() && !var1.isEmpty()) {
         int var2 = Math.max(var0.length(), var1.length());
         int var3 = StringUtils.getLevenshteinDistance(var0, var1);
         return (int)((1.0 - (double)var3 / var2) * 100.0);
      } else {
         return 0;
      }
   }

   public static int getInvSize(int var0) {
      MathUtils.clamp(var0, 9, 54);
      if (var0 % 9 != 0) {
         var0 = MathUtils.getClosestInt(var0, validSizes);
      }

      return var0;
   }

   public static boolean isSpawner(Material var0) {
      return var0.name().endsWith("SPAWNER");
   }

   public static boolean isSpawner(Block var0) {
      return var0 != null && var0.getType() != null ? isSpawner(var0.getType()) : false;
   }

   public static boolean doesBlockFaceMatch(Block var0, String var1, BlockFace... var2) {
      for (BlockFace var6 : var2) {
         Material var7 = var0.getRelative(var6).getType();
         if (!isAir(var7) && var7.name().endsWith(var1)) {
            return true;
         }
      }

      return false;
   }

   public static Block getOtherHalfOfBed(Block var0) {
      if (!var0.getType().name().endsWith("_BED")) {
         return null;
      } else {
         Bed var1 = (Bed)var0.getBlockData();
         Block var2;
         if (var1.getPart() == Part.HEAD) {
            var2 = var0.getRelative(var1.getFacing().getOppositeFace());
         } else {
            var2 = var0.getRelative(var1.getFacing());
         }

         return !(var2.getBlockData() instanceof Bed) ? null : var2;
      }
   }

   public static boolean isTool(Material var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.name();
         return var1.endsWith("_AXE")
            || var1.endsWith("_PICKAXE")
            || var1.endsWith("_SWORD")
            || var1.endsWith("_SHOVEL")
            || var1.endsWith("_SPADE")
            || var1.endsWith("_HOE")
            || var1.endsWith("SHEARS");
      }
   }

   public static boolean isExcessVelocity(Vector var0) {
      return var0.getX() > 10.0 || var0.getX() < -10.0 || var0.getY() > 10.0 || var0.getY() < -10.0 || var0.getZ() > 10.0 || var0.getZ() < -10.0;
   }

   public static List<Block> getBlocksFlat(Block var0, int var1) {
      if (var1 < 1) {
         return var1 == 0 ? Collections.singletonList(var0) : Collections.emptyList();
      } else {
         int var2 = (var1 << 1) + 1;
         ArrayList var3 = new ArrayList(var2 * var2 * var2);

         for (int var4 = -var1; var4 <= var1; var4++) {
            for (int var5 = -var1; var5 <= var1; var5++) {
               var3.add(var0.getRelative(var4, 0, var5));
            }
         }

         return var3;
      }
   }

   public static int getAmount(Player var0, Material var1) {
      int var2 = 0;

      for (ItemStack var6 : var0.getInventory().getStorageContents()) {
         if (var6 != null && var6.getType() == var1) {
            var2 += var6.getAmount();
         }
      }

      return var2;
   }

   public static boolean hasAmount(Player var0, Material var1, int var2) {
      for (ItemStack var6 : var0.getInventory().getContents()) {
         if (var6 != null && var6.getType() == var1) {
            var2 -= var6.getAmount();
            if (var2 <= 0) {
               return true;
            }
         }
      }

      if (var0.getInventory().getItem(EquipmentSlot.OFF_HAND) != null && var0.getInventory().getItem(EquipmentSlot.OFF_HAND).getType() == var1) {
         var2 -= var0.getInventory().getItem(EquipmentSlot.OFF_HAND).getAmount();
         if (var2 <= 0) {
            return true;
         }
      }

      return false;
   }

   public static boolean removeItems(Inventory var0, Material var1, int var2) {
      if (var1 != null && var0 != null) {
         if (var2 <= 0) {
            return false;
         } else if (var2 == Integer.MAX_VALUE) {
            var0.remove(var1);
            return true;
         } else {
            int var3 = var2;
            if (var0 instanceof PlayerInventory var4) {
               ItemStack var5 = var4.getItemInOffHand();
               if (var5.getType() == var1) {
                  var3 = removeItem(var4, var5, EquipmentSlot.OFF_HAND, var2);
               }
            }

            if (var3 <= 0) {
               return true;
            } else {
               for (int var7 = 0; var7 < var0.getSize(); var7++) {
                  int var8 = var0.first(var1);
                  if (var8 == -1) {
                     return false;
                  }

                  ItemStack var6 = var0.getItem(var8);

                  assert var6 != null;

                  var3 = removeItem(var0, var6, var8, var3);
                  if (var3 <= 0) {
                     break;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean removeItems(Inventory var0, ItemStack var1, int var2) {
      if (var1 != null && var0 != null) {
         if (var2 <= 0) {
            return false;
         } else if (var2 == Integer.MAX_VALUE) {
            var0.remove(var1);
            return true;
         } else {
            int var3 = var2;
            if (var0 instanceof PlayerInventory var4) {
               ItemStack var5 = var4.getItemInOffHand();
               if (var5.isSimilar(var1)) {
                  var3 = removeItem(var0, var5, EquipmentSlot.OFF_HAND.ordinal(), var2);
               }
            }

            if (var3 <= 0) {
               return true;
            } else {
               for (int var7 = 0; var7 < var0.getSize(); var7++) {
                  int var8 = var0.first(var1);
                  if (var8 == -1) {
                     return false;
                  }

                  ItemStack var6 = var0.getItem(var8);

                  assert var6 != null;

                  var3 = removeItem(var0, var6, var8, var3);
                  if (var3 <= 0) {
                     break;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean itemStackEquals(ItemStack var0, ItemStack var1, boolean var2) {
      if (var0 == null || var1 == null) {
         return false;
      } else if (var0 == var1) {
         return true;
      } else {
         ItemMeta var3 = var0.getItemMeta();
         ItemMeta var4 = var1.getItemMeta();
         if (!var2 && MinecraftVersion.isNew()) {
            if (var3 instanceof Damageable) {
               ((Damageable)var3).setDamage(0);
            }

            if (var4 instanceof Damageable) {
               ((Damageable)var4).setDamage(0);
            }
         }

         return var0.getType() == var1.getType()
            && (!var2 || var0.getDurability() == var1.getDurability())
            && var3 != null == (var4 != null)
            && (var3 == null || Bukkit.getItemFactory().equals(var3, var4));
      }
   }

   private static int removeItem(Inventory var0, ItemStack var1, int var2, int var3) {
      if (var1.getAmount() <= var3) {
         var3 -= var1.getAmount();
         if (var2 == 45 && var0 instanceof PlayerInventory) {
            ((PlayerInventory)var0).setItemInOffHand(null);
         } else {
            var0.clear(var2);
         }
      } else {
         var1.setAmount(var1.getAmount() - var3);
         var0.setItem(var2, var1);
         var3 = 0;
      }

      return var3;
   }

   private static int removeItem(PlayerInventory var0, ItemStack var1, EquipmentSlot var2, int var3) {
      if (var1.getAmount() <= var3) {
         var3 -= var1.getAmount();
         var0.setItem(var2, null);
      } else {
         var1.setAmount(var1.getAmount() - var3);
         var0.setItem(var2, var1);
         var3 = 0;
      }

      return var3;
   }

   public static int getEmptySlotOtherThan(int var0, Player var1) {
      PlayerInventory var2 = var1.getInventory();
      byte var3 = -1;
      List var4 = Arrays.asList(36, 37, 38, 39, 40);

      for (int var5 = 0; var5 <= var1.getInventory().getSize() - 1; var5++) {
         if (var0 != var5 && !var4.contains(var5) && (var2.getItem(var5) == null || var2.getItem(var5).getType() == Material.AIR)) {
            return var5;
         }
      }

      return var3;
   }

   public static void giveItemAtSlot(Player var0, ItemStack var1, int var2) {
      if (isValid(var1)) {
         var0.getInventory().setItem(var2, var1);
         var0.updateInventory();
      }
   }

   public static boolean hasPotionEffect(LivingEntity var0, PotionEffectType var1, int var2) {
      for (PotionEffect var4 : var0.getActivePotionEffects()) {
         if (var4.getType() == var1 && var4.getAmplifier() == var2) {
            return true;
         }
      }

      return false;
   }

   public static boolean isLog(Material var0) {
      if (var0 != null && !isAir(var0)) {
         boolean var1 = instance.getConfig().getBoolean("settings.stems-count-as-trees", false);
         boolean var2 = var0.name().endsWith("LOG") || var0.name().endsWith("LOG_2");
         boolean var3 = var0.name().endsWith("STEM");
         return !var2 && !var3 ? false : var1 || !var3;
      } else {
         return false;
      }
   }

   public static String getOrDefault(Replace var0, String var1) {
      return var0 == null ? var1 : var0.toString();
   }

   public static Object getOrDefault(Object var0, Object var1) {
      return var0 == null ? var1 : var0;
   }

   public static boolean doChancesPass(int var0) {
      return var0 > ThreadLocalRandom.current().nextDouble() * 100.0;
   }

   public static void reduceHeldItems(Player var0, EquipmentSlot var1, int var2) {
      ItemStack var3 = var0.getInventory().getItem(var1);
      if (var3.getAmount() - var2 <= 0) {
         var3 = null;
      } else {
         var3.setAmount(var3.getAmount() - var2);
      }

      var0.getInventory().setItem(var1, var3);
   }

   public static String capitalize(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         var0 = var0.replaceAll("_", " ").toLowerCase(Locale.ROOT);
         return var0.substring(0, 1).toUpperCase() + var0.substring(1);
      } else {
         return var0;
      }
   }

   public static String formatMaterialName(Material var0) {
      return formatMaterialName(var0.name());
   }

   public static String formatMaterialName(String var0) {
      String var1 = var0.toLowerCase().replaceAll("_", " ");
      return capitalize(var1);
   }

   public static Material getItemFromBlock(Material var0) {
      if (MinecraftVersion.getVersionNumber() >= 1120 && var0.isItem()) {
         return var0;
      } else if (isWallBlock(var0)) {
         return getItemFromBlock(getItemFromBlock(var0));
      } else {
         String var1 = var0.name();
         switch (var1) {
            case "CARROTS":
               return Material.CARROT;
            case "COCOA":
               return Material.COCOA_BEANS;
            case "KELP_PLANT":
               return Material.KELP;
            case "POTATOES":
               return Material.POTATO;
            case "TRIPWIRE":
               return Material.STRING;
            default:
               return var0;
         }
      }
   }

   public static boolean isWallBlock(Material var0) {
      if (!isValid(var0)) {
         return false;
      } else {
         String var1 = var0.name();
         return !var1.contains("SKULL") && !var1.contains("HEAD")
            ? var1.contains("WALL_")
               || var1.equals("TRIPWIRE_HOOK")
               || var1.equals("LADDER")
               || var1.equals("LEVER")
               || var1.contains("BUTTON")
               || var1.contains("BANNER")
               || var1.equals("COCOA")
            : false;
      }
   }

   public static Object extractFromDataArray(String var0, String var1, String var2, Object var3) {
      for (String var7 : var0.split(" ")) {
         if (var7.startsWith(var1)) {
            return var7.split(var2)[1];
         }
      }

      return var3;
   }

   public static String formatTime(long var0) {
      int var2 = (int)(var0 / 1000L);
      int var3 = var2 % 3600 % 60;
      int var4 = (int)Math.floor(var2 % 3600 / 60);
      int var5 = (int)Math.floor(var2 / 3600);
      String var6 = "";
      if (var5 > 0) {
         var6 = var6 + var5 + "h ";
      }

      if (var4 > 0) {
         var6 = var6 + var4 + "m ";
      }

      return var6 + var3 + "s";
   }

   public static void reportIssue(Exception var0, String var1) {
      StackTraceElement[] var2 = var0.getStackTrace();
      String var3 = "";

      for (StackTraceElement var7 : var2) {
         String var8 = var7 + "";
         if (var8.contains("net.advancedplugins.ae")) {
            var3 = var8;
            break;
         }
      }

      var0.printStackTrace();
      Bukkit.getLogger().info("[" + instance.getDescription().getName() + " ERROR] Could not pass " + ExceptionUtils.getRootCauseMessage(var0));
      Bukkit.getLogger().info("   Class: " + var3);
      Bukkit.getLogger().info("   Extra info: " + var1 + "; mc[" + MinecraftVersion.getVersionNumber() + "];");
      Bukkit.getLogger().info("If you cannot indentify cause of this, contact developer providing this report. ");
   }

   public static Material getMaterial(String var0) {
      try {
         MinecraftVersion.getVersion();
         return MinecraftVersion.getVersionNumber() > 1121 ? Material.matchMaterial(var0, true) : Material.matchMaterial(var0);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static <T, E> Set<T> getKeysByValue(Map<T, E> var0, E var1) {
      HashSet var2 = new HashSet();

      for (Entry var4 : var0.entrySet()) {
         if (Objects.equals(var1, var4.getValue())) {
            var2.add(var4.getKey());
         }
      }

      return var2;
   }

   public static <T, E> T getKeyByValue(Map<T, E> var0, E var1) {
      new HashSet();

      for (Entry var4 : var0.entrySet()) {
         if (Objects.equals(var1, var4.getValue())) {
            return (T)var4.getKey();
         }
      }

      return null;
   }

   public static List<String> replace(List<String> var0, String var1, String var2) {
      var0.replaceAll(var2x -> var2x.replace(var1, var2));
      return var0;
   }

   public static int[] getSlots(String var0) {
      int[] var1 = new int[1];
      if (var0.equalsIgnoreCase("filler")) {
         return var1;
      } else {
         if (var0.contains(",")) {
            var1 = Arrays.stream(var0.split(",")).mapToInt(Integer::parseInt).toArray();
         } else if (var0.contains("-")) {
            var1 = Arrays.stream(var0.split("-")).mapToInt(Integer::parseInt).toArray();
            var1 = IntStream.rangeClosed(var1[0], var1[1]).toArray();
         } else {
            var1[0] = parseInt(var0);
         }

         return var1;
      }
   }

   public static boolean contains(String var0, List<String> var1) {
      for (String var3 : var1) {
         if (var0.toLowerCase(Locale.ROOT).contains(var3.toLowerCase(Locale.ROOT))) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(String var0, String[] var1) {
      for (String var5 : var1) {
         if (var0.toLowerCase(Locale.ROOT).equalsIgnoreCase(var5.toLowerCase(Locale.ROOT))) {
            return true;
         }
      }

      return false;
   }

   public static int parseInt(String var0) {
      return parseInt(var0, 0);
   }

   public static int parseInt(String var0, int var1) {
      try {
         if (var0.split("-").length > 1 && !var0.substring(0, 1).equalsIgnoreCase("-")) {
            int var2 = Integer.parseInt(var0.split("-")[0]);
            int var3 = Integer.parseInt(var0.split("-")[1]);
            return ThreadLocalRandom.current().nextInt(var3 - var2) + var2;
         } else {
            return (int)Double.parseDouble(var0.replaceAll("\"[^0-9.-]\"", "").replaceAll(" ", ""));
         }
      } catch (Exception var4) {
         instance.getLogger().warning("Failed to parse " + var0 + " from String to Integer.");
         var4.printStackTrace();
         return var1;
      }
   }

   public static double parseDouble(String var0, double var1) {
      try {
         if (var0.split("-").length > 1 && !var0.substring(0, 1).equalsIgnoreCase("-")) {
            double var3 = Integer.parseInt(var0.split("-")[0]);
            double var5 = Integer.parseInt(var0.split("-")[1]);
            return ThreadLocalRandom.current().nextDouble(var5 - var3) + var3;
         } else {
            return Double.parseDouble(var0.replaceAll("[^\\\\d.]", ""));
         }
      } catch (Exception var7) {
         instance.getLogger().warning("Failed to parse " + var0 + " from String to Double.");
         var7.printStackTrace();
         return var1;
      }
   }

   public static double round(double var0, int var2) {
      return new BigDecimal(var0).setScale(var2, 3).doubleValue();
   }

   private static int findIfEnd(String var0, int var1) {
      if (var1 > var0.length()) {
         return -1;
      } else {
         int var2 = var0.indexOf("</if>", var1);
         int var3 = var0.indexOf("<if>", var1);
         if (var3 > -1 && var3 < var2) {
            int var4 = findIfEnd(var0, var3 + 4);
            return findIfEnd(var0, var4 + 5);
         } else {
            return var2;
         }
      }
   }

   private static int findResultSplit(String var0, int var1) {
      if (var1 > var0.length()) {
         return -1;
      } else {
         int var2 = var0.indexOf(":", var1);
         int var3 = var0.indexOf("<if>", var1);
         if (var3 > -1 && var3 < var2) {
            int var4 = findIfEnd(var0, var3 + 4);
            return findResultSplit(var0, var4 + 1);
         } else {
            return var2;
         }
      }
   }

   private static String[] splitAtIndex(String var0, int var1) {
      if (var1 >= var0.length() - 1) {
         return new String[]{var1 >= var0.length() ? var0 : var0.substring(0, var1)};
      } else {
         String var2 = var0.substring(0, var1);
         String var3 = var0.substring(var1 + 1);
         return new String[]{var2, var3};
      }
   }

   private static boolean checkStringsEquality(String var0) {
      if (var0.contains("===")) {
         String[] var2 = var0.split("===", 2);
         return var2[0].equals(var2[1]);
      } else if (var0.contains("==")) {
         String[] var1 = var0.split("==", 2);
         return var1[0].equalsIgnoreCase(var1[1]);
      } else {
         return false;
      }
   }

   private static String handleIfExpression(String var0) {
      while (var0.contains("<if>")) {
         int var1 = var0.indexOf("<if>");
         int var2 = var1 + 4;
         int var3 = findIfEnd(var0, var2);
         String var4 = var0.substring(var2, var3);
         String[] var5 = var4.split("\\?", 2);
         String var6 = var5[0];
         int var7 = findResultSplit(var5[1], 0);
         String[] var8 = splitAtIndex(var5[1], var7);
         boolean var9 = parseCondition(var6);
         String var10 = var9 ? var8[0] : var8[1];
         var0 = var0.replace("<if>" + var4 + "</if>", var10);
      }

      return var0;
   }

   public static boolean parseCondition(String var0) {
      var0 = var0.replaceAll(" ", "");
      Expression var2 = new Expression(var0, MathContext.UNLIMITED);

      boolean var1;
      try {
         var1 = var2.eval().intValue() == 1;
      } catch (Exception var4) {
         var1 = checkStringsEquality(var0);
      }

      return var1;
   }

   public static String substringBetween(String var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null) {
         int var3 = var0.indexOf(var1);
         if (var3 != -1) {
            int var4 = var0.indexOf(var2, var3 + var1.length());
            if (var4 != -1) {
               return var0.substring(var3 + var1.length(), var4);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static double parseThroughCalculator(String var0) {
      if (var0.contains("<random>")) {
         String var1 = substringBetween(var0, "<random>", "</random>");
         int var2 = parseInt(var1);
         var0 = var0.replace("<random>" + var1 + "</random>", Integer.toString(var2));
      }

      var0 = var0.replaceAll(" ", "");
      var0 = handleIfExpression(var0);
      Expression var6 = new Expression(var0, MathContext.UNLIMITED);

      try {
         return var6.eval().doubleValue();
      } catch (Exception var3) {
         var3.printStackTrace();
         Bukkit.getLogger().warning("Failed to calculate '" + var0 + "': Invalid syntax or outcome");
         return 0.0;
      }
   }

   public static String parseVariables(String var0, Map<String, String> var1, Replace var2) {
      return parseVariables(var0, "", var1, var2);
   }

   public static String parseVariables(String var0, String var1, Map<String, String> var2, Replace var3) {
      return Text.modify(var0, var3x -> {
         var2.forEach((var3xx, var4) -> {
            double var5 = parseThroughCalculator(Text.modify(var4, var3));
            String var7 = var1 + var3xx;
            if (Math.floor(var5) == var5) {
               var3x.set(var7, (long)var5);
            } else {
               var3x.set(var7, var5);
            }
         });
         return var3x;
      }, false);
   }

   public static void playEffect(String var0, float var1, int var2, Location var3) {
      MinecraftVersion.getVersion();
      if (MinecraftVersion.getVersionNumber() < 1130) {
         try {
            Class var4 = Class.forName("org.bukkit.Effect");
            Enum var5 = Enum.valueOf(var4, var0);
            Method var6 = var3.getWorld()
               .spigot()
               .getClass()
               .getMethod("playEffect", Location.class, var4, int.class, int.class, float.class, float.class, float.class, float.class, int.class, int.class);
            if (!var6.isAccessible()) {
               var6.setAccessible(true);
            }

            var6.invoke(var3.getWorld().spigot(), var3, var5, 0, 0, var1, var1, var1, 0.0F, var2, 32);
         } catch (Exception var8) {
         }
      } else {
         try {
            Class var9 = Class.forName("org.bukkit.Particle");
            Enum var10 = Enum.valueOf(var9, var0);
            Method var11 = var3.getWorld()
               .getClass()
               .getMethod("spawnParticle", var9, Location.class, int.class, double.class, double.class, double.class, double.class);
            if (!var11.isAccessible()) {
               var11.setAccessible(true);
            }

            var11.invoke(var3.getWorld(), var10, var3, var2, var1, var1, var1, 0.0F);
         } catch (Exception var7) {
         }
      }
   }

   private static boolean startsWithColor(String var0) {
      for (String var2 : damages.values()) {
         if (var0.startsWith(var2)) {
            return true;
         }
      }

      return false;
   }

   private static String addColor(String var0, int var1) {
      String var2 = damages.get(var1);
      return var2 == null ? var0 : var2 + "_" + var0;
   }

   private static boolean canAddColor(String var0) {
      return var0.contains("STAINED_GLASS")
         || var0.contains("SHULKER")
         || var0.contains("TERRACOTTA")
         || var0.contains("WOOL")
         || var0.contains("BANNER") && !var0.endsWith("BANNER_PATTERN")
         || var0.contains("DYE")
         || var0.contains("CONCRETE")
         || var0.contains("CARPET")
         || var0.contains("BED");
   }

   public static ItemStack matchMaterial(String var0, int var1, int var2) {
      return matchMaterial(var0, var1, var2, false, true);
   }

   public static ItemStack matchMaterial(String var0, int var1, int var2, boolean var3, boolean var4) {
      MinecraftVersion.getVersion();
      boolean var5 = MinecraftVersion.getVersionNumber() > 1121;
      if (var5) {
         if (var0.startsWith("GOLD_") && !var0.contains("BLOCK") && !var0.contains("NUGGET") && !var0.contains("INGOT") && !var0.contains("ORE")) {
            var0 = var0.replace("GOLD_", "GOLDEN_");
         }

         for (Entry var7 : newMaterials.entrySet()) {
            if (((String)var7.getKey()).equalsIgnoreCase(var0)) {
               var0 = (String)var7.getValue();
               break;
            }
         }

         if (canAddColor(var0) && !startsWithColor(var0)) {
            var0 = addColor(var0, var2);
         }
      }

      try {
         Material var9 = var3 ? Material.matchMaterial(var0, true) : Material.matchMaterial(var0);
         return !var5 ? new ItemStack(var9, var1, (byte)var2) : new ItemStack(var9, var1);
      } catch (Exception var8) {
         if (!var3 && var5) {
            return matchMaterial(var0, var1, var2, true, var4);
         } else {
            if (var4) {
               Bukkit.getLogger()
                  .info(
                     "�cFailed to match '"
                        + var0
                        + "' material, check your configuration or use https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html  to find needed material. �7�oFurther information has been pasted to console..."
                  );
               var8.printStackTrace();
            }

            return null;
         }
      }
   }

   public static String getRoughNumber(long var0) {
      if (var0 <= 999L) {
         return String.valueOf(var0);
      } else {
         String[] var2 = new String[]{"", "K", "M", "B", "P"};
         int var3 = (int)(Math.log10(var0) / Math.log10(1000.0));
         return new DecimalFormat("#,##0.#").format(var0 / Math.pow(1000.0, var3)) + var2[var3];
      }
   }

   public static String format(long var0) {
      return StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(NumberFormat.getInstance(Locale.ROOT).format(var0));
   }

   public static String color(String var0) {
      return ColorUtils.format(var0);
   }

   public static boolean isTall(Material var0) {
      if (var0.name().endsWith("_DOOR")) {
         return true;
      } else {
         return !MinecraftVersion.isNew()
            ? var0.name().equals("DOUBLE_PLANT")
            : var0 == Material.SUNFLOWER || var0 == Material.LILAC || var0 == Material.ROSE_BUSH || var0 == Material.PEONY;
      }
   }

   public static List<Location> removeDuplicateLocations(List<Location> var0) {
      ArrayList var1 = new ArrayList();
      HashSet var2 = new HashSet();

      for (Location var4 : var0) {
         if (var4 != null && var4.getWorld() != null) {
            String var5 = var4.getWorld().getName() + "," + var4.getX() + "," + var4.getY() + "," + var4.getZ();
            if (!var2.contains(var5)) {
               var2.add(var5);
               var1.add(var4);
            }
         }
      }

      return var1;
   }

   public static boolean isValid(Material var0) {
      return var0 != null && !isAir(var0);
   }

   public static boolean isValid(ItemStack var0) {
      return var0 != null && var0.getAmount() > 0 && !isAir(var0.getType());
   }

   public static boolean isValid(Block var0) {
      if (var0 != null && !isAir(var0.getType())) {
         String var1 = var0.getType().name();
         if (var1.endsWith("_PORTAL")) {
            return false;
         } else {
            return var1.contains("PISTON_")
               ? var1.contains("PISTON_BASE") || var1.contains("PISTON_STICKY_BASE")
               : !var1.equals("FIRE")
                  && !var1.equals("SOUL_FIRE")
                  && !var1.equals("TALL_SEAGRASS")
                  && !var1.equals("SWEET_BERRY_BUSH")
                  && !var1.equals("BUBBLE_COLUMN")
                  && !var1.equals("LAVA");
         }
      } else {
         return false;
      }
   }

   public static void giveItem(Player var0, ItemStack... var1) {
      giveItem(var0, Arrays.stream(var1).collect(Collectors.toList()));
   }

   public static void giveItem(Player var0, Collection<ItemStack> var1) {
      for (ItemStack var3 : var1) {
         if (isValid(var3) && !var0.getInventory().addItem(new ItemStack[]{var3}).isEmpty()) {
            if (!Bukkit.isPrimaryThread()) {
               SchedulerUtils.runTaskLater(() -> dropItem(var0.getLocation(), var3));
            } else {
               dropItem(var0.getLocation(), var3);
            }
         }
      }
   }

   public static List<ItemStack> condense(ItemStack[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         if (var0[var1] != null) {
            for (int var2 = var1 + 1; var2 < var0.length; var2++) {
               if (var0[var2] != null && var0[var1].isSimilar(var0[var2]) && var0[var1].getAmount() + var0[var2].getAmount() <= var0[var1].getMaxStackSize()) {
                  var0[var1].setAmount(var0[var1].getAmount() + var0[var2].getAmount());
                  var0[var2] = null;
               }
            }
         }
      }

      return Arrays.stream(var0).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public static void dropItem(Location var0, ItemStack... var1) {
      for (ItemStack var5 : var1) {
         if (var5 != null) {
            var0.getWorld().dropItem(var0, var5);
         }
      }
   }

   public static boolean isAir(Material var0) {
      if (var0 == null) {
         return false;
      } else {
         return MinecraftVersion.getVersionNumber() < 1130
            ? var0 == Material.AIR
            : var0 == Material.AIR || var0 == Material.CAVE_AIR || var0 == Material.VOID_AIR || var0 == Material.LEGACY_AIR;
      }
   }

   public static boolean isAir(Block var0) {
      return var0 == null || isAir(var0.getType());
   }

   public static boolean isAir(ItemStack var0) {
      return var0 == null || isAir(var0.getType());
   }

   public static void sendActionBar(String var0, Player var1) {
      if (MinecraftVersion.getVersionNumber() >= 190) {
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var0));
      } else {
         String var2 = "v1_8_R3";

         try {
            Class var3 = Class.forName("org.bukkit.craftbukkit." + var2 + ".entity.CraftPlayer");
            Object var4 = var3.cast(var1);
            Class var5 = Class.forName("net.minecraft.server." + var2 + ".PacketPlayOutChat");
            Class var6 = Class.forName("net.minecraft.server." + var2 + ".Packet");
            Class var8 = Class.forName("net.minecraft.server." + var2 + (var2.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class var9 = Class.forName("net.minecraft.server." + var2 + ".IChatBaseComponent");
            Method var10 = null;
            if (var2.equalsIgnoreCase("v1_8_R1")) {
               var10 = var8.getDeclaredMethod("a", String.class);
            }

            Object var11 = var2.equalsIgnoreCase("v1_8_R1")
               ? var9.cast(var10.invoke(var8, "{'text': '" + var0 + "'}"))
               : var8.getConstructor(String.class).newInstance(var0);
            Object var7 = var5.getConstructor(var9, byte.class).newInstance(var11, (byte)2);
            Method var12 = var3.getDeclaredMethod("getHandle");
            Object var13 = var12.invoke(var4);
            Field var14 = var13.getClass().getDeclaredField("playerConnection");
            Object var15 = var14.get(var13);
            Method var16 = var15.getClass().getDeclaredMethod("sendPacket", var6);
            var16.invoke(var15, var7);
         } catch (Exception var17) {
            var17.printStackTrace();
         }
      }
   }

   public static int getPages(int var0, int var1) {
      return var0 / var1 + (var0 % var1 == 0 ? 0 : 1);
   }

   public static <T> List<T> getItemsInPage(List<T> var0, int var1, int var2) {
      return var0.subList(var1 * var2, Math.min(var0.size(), var2 * (var1 + 1)));
   }

   public static void deleteFile(File var0) {
      if (var0.isDirectory()) {
         for (File var4 : var0.listFiles()) {
            deleteFile(var4);
         }
      }

      var0.delete();
   }

   public static void unZip(File var0, File var1) {
      if (!var1.exists() || !var1.isDirectory()) {
         var1.mkdirs();
      }

      ZipFile var2 = new ZipFile(var0);
      ZipEntry var3 = null;
      byte[] var4 = new byte[1024];
      Enumeration var5 = var2.entries();

      try {
         while (var5.hasMoreElements()) {
            var3 = (ZipEntry)var5.nextElement();
            if (var3.isDirectory()) {
               File var14 = new File(var1, var3.getName());
               var14.mkdirs();
            } else {
               InputStream var6 = var2.getInputStream(var3);
               File var7 = new File(var1, var3.getName());
               FileOutputStream var8 = new FileOutputStream(var7);
               int var9 = 0;

               while ((var9 = var6.read(var4)) > -1) {
                  var8.write(var4, 0, var9);
               }

               var8.close();
               var6.close();
            }
         }
      } finally {
         var2.close();
      }
   }

   public static Material getNonWallMaterial(Material var0) {
      if (!isValid(var0)) {
         return var0;
      } else {
         String var1 = var0.name();
         var1 = var1.replace("WALL_", "");
         return Material.getMaterial(var1);
      }
   }

   public static Material getFixedMaterial(Material var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.name();
         switch (var1) {
            case "CARROTS":
               if (MinecraftVersion.isNew()) {
                  return Material.CARROT;
               }

               return Material.matchMaterial("CARROT_ITEM");
            case "POTATOES":
               if (MinecraftVersion.isNew()) {
                  return Material.POTATO;
               }

               return Material.matchMaterial("POTATO_ITEM");
            case "BEETROOTS":
               return Material.BEETROOT;
            case "COCOA":
               return Material.COCOA_BEANS;
            case "KELP_PLANT":
               return Material.KELP;
            case "TRIPWIRE":
               return Material.STRING;
            case "WATER_CAULDRON":
            case "LAVA_CAULDRON":
            case "POWDER_SNOW_CAULDRON":
               return Material.CAULDRON;
            default:
               return var0;
         }
      }
   }

   public static boolean isFortuneBlock(Material var0) {
      String var1 = var0.name();
      boolean var2 = var1.endsWith("_ORE");
      boolean var3 = false;
      boolean var4 = var2 && var1.contains("GOLD") || var1.contains("IRON");
      if ((!var2 || var4) && (!var2 || !var4 || !var3)) {
         switch (var1) {
            case "SEEDS":
            case "WHEAT_SEEDS":
            case "GLOWSTONE":
            case "NETHER_WART":
            case "SWEET_BERRIES":
            case "SEA_LANTERN":
            case "NETHER_GOLD_ORE":
            case "MELON":
            case "MELON_BLOCK":
            case "AMETHYST_CLUSTER":
               return true;
            default:
               return false;
         }
      } else {
         return true;
      }
   }

   public static int getDropAmount(Block var0, Material var1, ItemStack var2) {
      Material var3 = var0.getType();
      String var4 = var3.name().replace("LEGACY_", "");
      boolean var5 = var2.getEnchantments().containsKey(Enchantment.SILK_TOUCH);
      int var6 = var2.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("FORTUNE"));
      boolean var7 = silkOnly.contains(var4);
      if (var7 && !var5) {
         return -1;
      } else {
         boolean var8 = true;
         boolean var9 = isFortuneBlock(var3);
         int var10 = 1;
         int var11 = Integer.MAX_VALUE;
         if (!var5) {
            String var12 = var1.name();
            switch (var12) {
               case "IRON_INGOT":
               case "RAW_IRON":
               case "GOLD_INGOT":
               case "RAW_GOLD":
                  var9 = true;
                  var8 = true;
                  break;
               case "ENDER_CHEST":
                  var9 = false;
                  var8 = false;
            }

            switch (var4) {
               case "DEEPSLATE_COPPER_ORE":
               case "COPPER_ORE":
                  var10 = MathUtils.randomBetween(2, 5);
                  break;
               case "DEEPSLATE_LAPIS_ORE":
               case "LAPIS_ORE":
                  var10 = MathUtils.randomBetween(4, 9);
                  break;
               case "SEA_LANTERN":
                  if (var1 != var3) {
                     var11 = 5;
                  }
                  break;
               case "DEEPSLATE_REDSTONE_ORE":
               case "REDSTONE_ORE":
                  var10 = MathUtils.randomBetween(4, 5);
                  break;
               case "NETHER_GOLD_ORE":
                  var10 = MathUtils.randomBetween(2, 6);
                  break;
               case "CLAY":
               case "AMETHYST_CLUSTER":
                  var10 = 4;
                  break;
               case "MELON":
               case "MELON_BLOCK":
                  var10 = MathUtils.randomBetween(3, 7);
                  var11 = 9;
                  break;
               case "GLOWSTONE":
                  var10 = MathUtils.randomBetween(2, 4);
                  var11 = 4;
                  break;
               case "ENDER_CHEST":
                  var11 = 1;
                  var8 = false;
                  var9 = false;
                  break;
               case "BOOKSHELF":
                  var10 = 3;
                  var11 = 3;
                  var8 = false;
            }
         }

         if (CropUtils.isCrop(var3)) {
            var10 = CropUtils.getDropAmount(var0, var1, var2);
         } else {
            boolean var16 = var8 && var1 != var3 && var6 > 0 && !var5 && var9;
            if (var16) {
               if (var3.name().endsWith("_ORE")) {
                  float var20 = ThreadLocalRandom.current().nextFloat();
                  if (var20 > 2.0F / (var6 + 2.0F)) {
                     int var14 = var10;

                     for (int var15 = 0; var15 < var14; var15++) {
                        var10 += new UniformIntegerDistribution(2, var6 + 1).sample();
                     }
                  }
               } else {
                  var10 = new UniformIntegerDistribution(1, Math.min(var11, var10 * var6)).sample();
               }
            }
         }

         if (var3 == Material.TURTLE_EGG) {
            TurtleEgg var17 = (TurtleEgg)var0.getBlockData();
            var10 = var17.getEggs();
         } else if (var3.name().endsWith("CANDLE")) {
            Candle var18 = (Candle)var0.getBlockData();
            var10 = var18.getCandles();
         }

         return MathUtils.clamp(var10, Integer.MIN_VALUE, var11);
      }
   }

   public static int getExpToDrop(@NotNull Material var0, @NotNull ItemStack var1) {
      int var2 = 0;
      String var3 = var0.name();
      switch (var3) {
         case "COAL_ORE":
         case "DEEPSLATE_COAL_ORE":
            var2 = new Random().nextInt(3);
            break;
         case "NETHER_GOLD_ORE":
            var2 = new Random().nextInt(2);
            break;
         case "DIAMOND_ORE":
         case "DEEPSLATE_DIAMOND_ORE":
         case "EMERALD_ORE":
         case "DEEPSLATE_EMERALD_ORE":
            var2 = 3 + new Random().nextInt(5);
            break;
         case "LAPIS_ORE":
         case "DEEPSLATE_LAPIS_ORE":
         case "NETHER_QUARTZ_ORE":
            var2 = 2 + new Random().nextInt(4);
            break;
         case "REDSTONE_ORE":
         case "DEEPSLATE_REDSTONE_ORE":
            var2 = 1 + new Random().nextInt(5);
            break;
         case "SPAWNER":
            var2 = 15 + new Random().nextInt(29);
            break;
         case "SCULK":
            var2 = 1;
            break;
         case "SCULK_SENSOR":
         case "SCULK_SHRIEKER":
         case "SCULK_CATALYST":
         case "CALIBRATED_SCULK_SENSOR":
            var2 = 5;
            break;
         default:
            if (var3.endsWith("_ORE")) {
               var2 = 1 + new Random().nextInt(3);
            }

            return var2;
      }

      return var2;
   }

   public static Set<Material> createMaterialSet(Collection<String> var0) {
      return var0.stream().<Material>map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toSet());
   }

   public static boolean isUnbreakable(ItemStack var0) {
      return var0 != null && var0.hasItemMeta() && var0.getItemMeta().isUnbreakable() || NBTapi.contains("Unbreakable", var0);
   }

   public static Object getNMSEntity(LivingEntity var0) {
      return ReflectionMethod.CRAFT_ENTITY_GET_HANDLE.run(ClassWrapper.CRAFT_ENTITY.getClazz().cast(var0));
   }

   public static boolean isDamageable(Material var0) {
      return var0.getMaxDurability() > 0;
   }

   public static String tryOrElse(TryCatchMethodShort var0, String var1) {
      return TryCatchUtil.tryOrDefault(var0::tryCatch, var1);
   }

   public static void setByMatching(ItemStack var0, ItemStack var1, LivingEntity var2) {
      if (var0.isSimilar(var2.getEquipment().getItemInMainHand())) {
         var2.getEquipment().setItemInMainHand(var1);
      } else {
         if (var0.isSimilar(var2.getEquipment().getItemInOffHand())) {
            var2.getEquipment().setItemInOffHand(var1);
         }
      }
   }

   public static boolean hasTotem(Player var0) {
      ItemStack var1 = var0.getInventory().getItemInOffHand();
      return isValid(var1) && var1.getType() == Material.TOTEM_OF_UNDYING;
   }

   public static void resetPlayerHealth(Player var0, double var1) {
      double var3 = MathUtils.clamp(var1, var0.getHealth(), var0.getMaxHealth());
      var0.setHealth(var3);
   }

   public static String getBlockMaterial(Block var0) {
      return var0 == null ? "AIR" : var0.getType().name();
   }

   public static List<String> getVariables(String var0, String var1, String var2) {
      ArrayList var3 = new ArrayList();
      int var4 = 0;

      for (String var8 : var0.split(var1)) {
         if (++var4 != 1) {
            var3.add(var8.split(var2)[0]);
         }
      }

      return var3;
   }

   public static <T extends Enum<T>> boolean isValidEnum(Class<T> var0, String var1) {
      try {
         Enum.valueOf(var0, var1);
         return true;
      } catch (IllegalArgumentException var3) {
         return false;
      }
   }

   public static boolean isCorrectTool(ItemStack var0, Material var1) {
      Object var2 = ReflectionMethod.CRAFT_ItemStack_asNMSCopy.run(null, var0);
      Object var3 = ReflectionMethod.CRAFT_MagicNumbers_getBlock.run(null, var1);
      Object var4 = ReflectionMethod.NMS_Block_getBlockData.run(var3);
      return (Boolean)ReflectionMethod.NMS_ItemStack_canDestroySpecialBlock.run(var2, var4);
   }

   public static boolean notNullAndTrue(Boolean var0) {
      return var0 == null ? false : var0;
   }

   public static boolean sameBlock(Location var0, Location var1) {
      return var0.getBlockX() == var1.getBlockX() && var0.getBlockY() == var1.getBlockY() && var0.getBlockZ() == var1.getBlockZ();
   }

   public static void debug(String var0) {
      if (debug) {
         Bukkit.getLogger().info(var0);
         Bukkit.getOnlinePlayers()
            .stream()
            .filter(var0x -> var0x.hasPermission("advancedplugins.admin") || var0x.isOp())
            .forEach(var1 -> var1.sendMessage(ColorUtils.format(var0)));
      }
   }

   public static String join(String[] var0, String var1) {
      return join(Arrays.asList(var0), var1);
   }

   public static String join(Collection<String> var0, String var1) {
      if (var0.isEmpty()) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder();

         for (String var4 : var0) {
            var2.append(capitalize(var4)).append(var1);
         }

         return var2.substring(0, var2.length() - var1.length());
      }
   }

   public static String join(Iterable<String> var0, String var1) {
      StringBuilder var2 = new StringBuilder();

      for (String var4 : var0) {
         var2.append(var4).append(var1);
      }

      return var2.substring(0, var2.length() - var1.length());
   }

   public static String getMaterial(ItemStack var0) {
      return var0 == null ? "AIR" : var0.getType().name();
   }

   public static <T> T getFromArray(T[] var0, int var1) {
      if (var1 == -1) {
         var1 = var0.length - 1;
      }

      return (T)var0[var1];
   }

   public static String limit(String var0, int var1, String var2) {
      return var0.length() < var1 ? var0 : var0.substring(0, var1 - 1) + var2;
   }

   public static String join(String[] var0, String var1, int var2, int var3) {
      StringBuilder var4 = new StringBuilder();
      var3 = Math.max(var0.length, var3);

      for (int var5 = var2; var5 < var3; var5++) {
         var4.append(var0[var5]).append(var1);
      }

      return var4.substring(0, var4.length() - var1.length());
   }

   public static int getEmptySlotCountInInventory(@NotNull Player var0) {
      if (var0.getInventory().firstEmpty() == -1) {
         return 0;
      } else {
         int var1 = 0;

         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = var0.getInventory().getItem(var2);
            if (var3 == null || var3.getType().equals(Material.AIR)) {
               var1++;
            }
         }

         return var1;
      }
   }

   public static <K, V> ImmutableMap<K, V> toImmutable(Map<K, V> var0) {
      return ImmutableMap.builder().putAll(var0).build();
   }

   public static <V> ImmutableList<V> toImmutableList(List<V> var0) {
      return new Builder().addAll(var0).build();
   }

   public static boolean isVegetation(Material var0) {
      return vegetationBlockNames.contains(var0.name());
   }

   public static <V> List<String> toStringList(V... var0) {
      ArrayList var1 = new ArrayList();

      for (Object var5 : var0) {
         var1.add(var5.toString());
      }

      return var1;
   }

   public static int[] subarray(int[] var0, int var1, int var2) {
      if (var0 != null && var1 >= 0 && var2 <= var0.length && var1 <= var2) {
         return Arrays.copyOfRange(var0, var1, var2);
      } else {
         throw new IllegalArgumentException("Invalid arguments");
      }
   }

   public static <V> V[] subarray(V[] var0, int var1, int var2) {
      if (var0 != null && var1 >= 0 && var2 <= var0.length && var1 <= var2) {
         return (V[])Arrays.copyOfRange(var0, var1, var2);
      } else {
         throw new IllegalArgumentException("Invalid arguments");
      }
   }

   public static boolean isDay(long var0) {
      return var0 > 0L && var0 < 12300L;
   }

   public static void fillEmptyInventorySlots(Inventory var0, ItemStack var1) {
      IntStream.range(0, var0.getSize()).filter(var1x -> var0.getItem(var1x) == null).forEach(var2 -> var0.setItem(var2, var1));
   }

   public static Location offsetToLookingLocation(Location var0, double var1) {
      Location var3 = var0.clone();
      Vector var4 = var3.getDirection();
      var4.normalize();
      var4.multiply(var1);
      var3.add(var4);
      return var3;
   }

   public static <K, V> ImmutableMap<K, V> configToImmutableMap(FileConfiguration var0, String var1, Function<String, K> var2, Class<V> var3) {
      com.google.common.collect.ImmutableMap.Builder var4 = ImmutableMap.builder();

      for (String var6 : var0.getConfigurationSection(var1).getKeys(false)) {
         Object var7 = var3.cast(var0.get(var1 + "." + var6));
         var4.put(var2.apply(var6.toUpperCase(Locale.ROOT)), var7);
      }

      return var4.build();
   }

   public static boolean isHostile(EntityType var0) {
      return Monster.class.isAssignableFrom(var0.getEntityClass());
   }

   public static Map<String, String> stringToMap(String... var0) {
      HashMap var1 = new HashMap();

      for (String var5 : var0) {
         String[] var6 = var5.split(";");
         if (var6.length == 2) {
            var1.put(var6[0], var6[1]);
         }
      }

      return var1;
   }

   public static <T> ImmutableMap<String, T> configObjecstToImmutableMap(Class<T> var0, FileConfiguration var1, String var2) {
      com.google.common.collect.ImmutableMap.Builder var3 = ImmutableMap.builder();

      for (String var6 : var1.getConfigurationSection(var2).getKeys(false)) {
         try {
            Object var7 = var0.getDeclaredConstructor().newInstance();

            for (Field var11 : var0.getDeclaredFields()) {
               var11.setAccessible(true);
               ConfigKey var12 = var11.getAnnotation(ConfigKey.class);
               if (var12 != null) {
                  Object var13;
                  if (var12.value().isEmpty()) {
                     var13 = var6;
                  } else {
                     var13 = var1.get(var2 + "." + var6 + "." + var12.value());
                  }

                  var11.set(var7, var13);
               }
            }

            var3.put(var6, var7);
         } catch (Exception var14) {
            var14.printStackTrace();
         }
      }

      return var3.build();
   }

   public static void log(String var0) {
      if (Registry.get().equalsIgnoreCase("9454")) {
         instance.getLogger().warning(Text.modify("&c&o[DEV DEBUG]&r " + var0));
         Bukkit.broadcastMessage(Text.modify("&c&o[DEV DEBUG]&r " + var0));
      }
   }

   public static FileConfiguration loadConfig(File var0) {
      try {
         return YamlConfiguration.loadConfiguration(var0);
      } catch (Exception var2) {
         getInstance().getLogger().severe("Failed to load " + var0.getName() + " file, check your configuration and try again.");
         return null;
      }
   }

   public static ItemStack makeItemGlow(ItemStack var0, @Nullable Boolean var1) {
      if (var0.hasItemMeta() && MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
         try {
            ItemMeta var2 = var0.getItemMeta();
            var2.setEnchantmentGlintOverride(var1);
            var0.setItemMeta(var2);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      return var0;
   }

   public static ItemStack makeItemGlow(ItemStack var0) {
      return makeItemGlow(var0, true);
   }

   public static Pair<String, Integer> parseEnchantment(String var0) {
      String[] var1 = var0.split(":");
      if (var1[0].startsWith("!")) {
         int var2 = Integer.parseInt(var1[0].replace("!", ""));
         if (ThreadLocalRandom.current().nextInt(100) + 1 > var2) {
            return null;
         }

         var1 = new String[]{var1[1], var1[2]};
      }

      int var6;
      if (var1[1].contains("%")) {
         String[] var3 = var1[1].replace("%", "").split("-");
         int var4 = Integer.parseInt(var3[0]);
         int var5 = Integer.parseInt(var3[1]);
         var6 = MathUtils.randomBetween(var4, var5);
      } else {
         var6 = Integer.parseInt(var1[1]);
      }

      String var7 = var1[0];
      return new Pair<>(var7, var6);
   }

   public static void saveResource(String var0) {
      if (!new File(instance.getDataFolder(), var0).isFile()) {
         getInstance().saveResource(var0, false);
      }
   }

   public static boolean parseBoolean(String var0, boolean var1) {
      if (var0 == null) {
         return var1;
      } else if (var0.strip().equalsIgnoreCase("true")) {
         return true;
      } else {
         return var0.strip().equalsIgnoreCase("false") ? false : var1;
      }
   }

   public static int minmax(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   public static String fetchJsonFromUrl(String var0) {
      URL var1 = new URL(var0);
      HttpURLConnection var2 = (HttpURLConnection)var1.openConnection();
      var2.setRequestMethod("GET");

      String var6;
      try (BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.getInputStream()))) {
         StringBuilder var4 = new StringBuilder();

         String var5;
         while ((var5 = var3.readLine()) != null) {
            var4.append(var5);
         }

         var6 = var4.toString();
      } finally {
         var2.disconnect();
      }

      return var6;
   }

   public static <T> T randomElement(Collection<T> var0) {
      if (var0.isEmpty()) {
         return null;
      } else {
         int var1 = ThreadLocalRandom.current().nextInt(var0.size());
         if (var0 instanceof List var4) {
            return (T)var4.get(var1);
         } else {
            Iterator var2 = var0.iterator();

            for (int var3 = 0; var3 < var1; var3++) {
               var2.next();
            }

            return (T)var2.next();
         }
      }
   }

   public static <T> List<T> reverse(Set<T> var0) {
      ArrayList var1 = new ArrayList(var0);
      Collections.reverse(var1);
      return var1;
   }

   public static boolean isOnline(LivingEntity var0) {
      return !(var0 instanceof Player) ? true : ((Player)var0).isOnline();
   }

   public static String[] listFiles(String var0) {
      return new File(instance.getDataFolder(), var0).list();
   }

   public static File getFile(String var0) {
      return new File(instance.getDataFolder(), var0);
   }

   public static String join(Map var0, String var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 1;

      for (Object var6 : var0.entrySet()) {
         Entry var7 = (Entry)var6;
         var3.append(var1.replace("%k%", capitalize(var7.getKey().toString())).replace("%v%", var7.getValue().toString()));
         if (var3.length() > var2 * var4) {
            var3.append("\n");
            var4++;
         }
      }

      return var3.toString();
   }

   public static boolean isPlayer(Entity var0) {
      return var0 instanceof Player;
   }

   public static int[] getNumbersInRange(int var0, int var1) {
      int[] var2 = new int[var1 - var0];
      int var3 = var0;

      while (var3 < var1) {
         var2[var3 - var0] = var3++;
      }

      return var2;
   }

   public static BlockFace getCardinalDirection(float var0) {
      if (var0 < 0.0F) {
         var0 += 360.0F;
      }

      var0 %= 360.0F;
      if (var0 <= 45.0F) {
         return BlockFace.NORTH;
      } else if (var0 <= 135.0F) {
         return BlockFace.EAST;
      } else if (var0 <= 225.0F) {
         return BlockFace.SOUTH;
      } else {
         return var0 <= 315.0F ? BlockFace.WEST : BlockFace.NORTH;
      }
   }

   public static Collection<Block> getNearbyBlocks(Location var0, float var1, float var2, float var3) {
      ArrayList var4 = new ArrayList();

      for (float var5 = -var1; var5 <= var1; var5++) {
         for (float var6 = -var2; var6 <= var2; var6++) {
            for (float var7 = -var3; var7 <= var3; var7++) {
               var4.add(var0.clone().add(var5, var6, var7).getBlock());
            }
         }
      }

      return var4;
   }

   public static ItemStack itemStackOrDefault(String var0, Material var1) {
      Material var2 = Material.matchMaterial(var0);
      return var2 != null && !var2.isAir() ? new ItemStack(var2) : new ItemStack(var1);
   }

   public static Optional<Entity> getEntityFromUUID(UUID var0, World var1) {
      return MinecraftVersion.isPaper()
         ? Optional.ofNullable(Bukkit.getEntity(var0))
         : var1.getEntities().stream().filter(var1x -> var1x.getUniqueId().equals(var0)).findFirst();
   }

   public static <T> Set<T> shuffle(Set<T> var0) {
      ArrayList var1 = new ArrayList(var0);
      Collections.shuffle(var1);
      return new HashSet<>(var1);
   }

   public static String serializeItem(ItemStack var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      BukkitObjectOutputStream var2 = new BukkitObjectOutputStream(var1);
      var2.writeObject(var0);
      var2.flush();
      byte[] var3 = var1.toByteArray();
      return new String(Base64.getEncoder().encode(var3));
   }

   public static ItemStack deserializeItem(String var0) {
      byte[] var1 = Base64.getDecoder().decode(var0);
      ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
      BukkitObjectInputStream var3 = new BukkitObjectInputStream(var2);
      return (ItemStack)var3.readObject();
   }

   public static JavaPlugin getInstance() {
      return instance;
   }

   static {
      if (damages.isEmpty()) {
         damages.put(0, "WHITE");
         damages.put(1, "ORANGE");
         damages.put(2, "MAGENTA");
         damages.put(3, "LIGHT_BLUE");
         damages.put(4, "YELLOW");
         damages.put(5, "LIME");
         damages.put(6, "PINK");
         damages.put(7, "GRAY");
         damages.put(8, "LIGHT_GRAY");
         damages.put(9, "CYAN");
         damages.put(10, "PURPLE");
         damages.put(11, "BLUE");
         damages.put(12, "BROWN");
         damages.put(13, "GREEN");
         damages.put(14, "RED");
         damages.put(15, "BLACK");
      }

      if (newMaterials.isEmpty()) {
         newMaterials.put("EYE_OF_ENDER", "ENDER_EYE");
         newMaterials.put("ENDER_PORTAL_FRAME", "END_PORTAL_FRAME");
         newMaterials.put("FIREWORK_CHARGE", "FIREWORK_STAR");
         newMaterials.put("FIREBALL", "FIRE_CHARGE");
         newMaterials.put("SULPHUR", "GUNPOWDER");
         newMaterials.put("WOOD_DOOR", "OAK_DOOR");
         newMaterials.put("COMMAND", "COMMAND_BLOCK");
         newMaterials.put("PISTON_BASE", "PISTON");
         newMaterials.put("SKULL_ITEM", "PLAYER_HEAD");
         newMaterials.put("WORKBENCH", "CRAFTING_TABLE");
         newMaterials.put("BOOK_AND_QUILL", "WRITABLE_BOOK");
         newMaterials.put("THIN_GLASS", "GLASS_PANE");
         newMaterials.put("STORAGE_MINECART", "CHEST_MINECART");
         newMaterials.put("BREWING_STAND_ITEM", "LEGACY_BREWING_STAND_ITEM");
      }
   }
}
