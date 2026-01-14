package net.advancedplugins.jobs.impl.utils.pdc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.ASManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class PDCHandler {
   private static final List<PersistentDataType> dataTypes = Arrays.asList(
      PersistentDataType.BYTE,
      PersistentDataType.BYTE_ARRAY,
      PersistentDataType.DOUBLE,
      PersistentDataType.FLOAT,
      PersistentDataType.INTEGER,
      PersistentDataType.INTEGER_ARRAY,
      PersistentDataType.LONG,
      PersistentDataType.SHORT,
      PersistentDataType.STRING
   );

   public static boolean contains(PersistentDataHolder var0, String var1) {
      return var0 == null
         ? false
         : dataTypes.stream().filter(var2 -> var0.getPersistentDataContainer().has(getNamespace(var1), var2)).findFirst().orElse(null) != null;
   }

   public static String getString(PersistentDataHolder var0, String var1, String var2) {
      return !has(var0, var1, PersistentDataType.STRING) ? var2 : get(var0, var1, PersistentDataType.STRING).toString();
   }

   public static String getString(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.STRING) ? null : get(var0, var1, PersistentDataType.STRING).toString();
   }

   public static int getInt(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.INTEGER) ? 0 : (Integer)get(var0, var1, PersistentDataType.INTEGER);
   }

   public static long getLong(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.LONG) ? 0L : (Long)get(var0, var1, PersistentDataType.LONG);
   }

   public static float getFloat(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.FLOAT) ? 0.0F : (Float)get(var0, var1, PersistentDataType.FLOAT);
   }

   public static double getDouble(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.DOUBLE) ? 0.0 : (Double)get(var0, var1, PersistentDataType.DOUBLE);
   }

   public static boolean getBoolean(PersistentDataHolder var0, String var1) {
      return !has(var0, var1, PersistentDataType.BYTE) ? false : (Byte)get(var0, var1, PersistentDataType.BYTE) == 1;
   }

   public static void set(PersistentDataHolder var0, String var1, PersistentDataType var2, Object var3) {
      if (var0 != null) {
         var0.getPersistentDataContainer().set(getNamespace(var1), var2, var3);
      }
   }

   public static void remove(PersistentDataHolder var0, String var1) {
      var0.getPersistentDataContainer().remove(getNamespace(var1));
   }

   public static Object get(PersistentDataHolder var0, String var1, PersistentDataType var2) {
      return var0 instanceof ItemStack && !((ItemStack)var0).hasItemMeta() ? null : var0.getPersistentDataContainer().get(getNamespace(var1), var2);
   }

   public static boolean has(PersistentDataHolder var0, String var1, PersistentDataType var2) {
      if (var0 instanceof ItemStack && !((ItemStack)var0).hasItemMeta()) {
         return false;
      } else {
         return var0 == null ? false : var0.getPersistentDataContainer().has(getNamespace(var1), var2);
      }
   }

   public static boolean has(PersistentDataHolder var0, String var1) {
      if (var0 instanceof ItemStack && !((ItemStack)var0).hasItemMeta()) {
         return false;
      } else {
         return var0 == null ? false : var0.getPersistentDataContainer().has(getNamespace(var1));
      }
   }

   public static boolean has(PersistentDataHolder var0, @Nullable NamespacedKey var1) {
      if (var1 == null) {
         return false;
      } else if (var0 instanceof ItemStack && !((ItemStack)var0).hasItemMeta()) {
         return false;
      } else {
         return var0 == null ? false : var0.getPersistentDataContainer().has(var1);
      }
   }

   public static boolean hasString(PersistentDataHolder var0, String var1) {
      return has(var0, var1, PersistentDataType.STRING);
   }

   public static boolean hasInt(PersistentDataHolder var0, String var1) {
      return has(var0, var1, PersistentDataType.INTEGER);
   }

   public static boolean hasBoolean(PersistentDataHolder var0, String var1) {
      return has(var0, var1, PersistentDataType.BYTE);
   }

   @Nullable
   public static NamespacedKey getNamespace(String var0, String var1) {
      Plugin var2 = Bukkit.getPluginManager().getPlugin(var0);
      return var2 == null ? null : new NamespacedKey(var2, var1);
   }

   public static NamespacedKey getNamespace(String var0) {
      return new NamespacedKey(ASManager.getInstance(), var0.replace(";", "-"));
   }

   public static void setString(PersistentDataHolder var0, String var1, String var2) {
      set(var0, var1, PersistentDataType.STRING, var2);
   }

   public static void setBoolean(PersistentDataHolder var0, String var1, boolean var2) {
      set(var0, var1, PersistentDataType.BYTE, (byte)(var2 ? 1 : 0));
   }

   public static void setLong(PersistentDataHolder var0, String var1, long var2) {
      set(var0, var1, PersistentDataType.LONG, var2);
   }

   public static void setDouble(PersistentDataHolder var0, String var1, double var2) {
      set(var0, var1, PersistentDataType.DOUBLE, var2);
   }

   public static void setInt(PersistentDataHolder var0, String var1, int var2) {
      set(var0, var1, PersistentDataType.INTEGER, var2);
   }

   public static long getLong(World var0, String var1) {
      return (Long)get(var0, var1, PersistentDataType.LONG);
   }

   public static void unset(PersistentDataHolder var0, String var1) {
      if (var0 != null) {
         var0.getPersistentDataContainer().remove(getNamespace(var1));
      }
   }

   public static boolean hasPDC(PersistentDataHolder var0) {
      return var0 == null ? false : !var0.getPersistentDataContainer().getKeys().isEmpty();
   }

   public static List<String> getKeys(ItemStack var0) {
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         ItemMeta var1 = var0.getItemMeta();
         if (var1 == null) {
            return Collections.emptyList();
         } else {
            PersistentDataContainer var2 = var1.getPersistentDataContainer();
            Set var3 = var2.getKeys();
            if (var3.isEmpty()) {
               return Collections.emptyList();
            } else if (var3.size() == 1) {
               return Collections.singletonList(((NamespacedKey)var3.iterator().next()).getKey());
            } else {
               ArrayList var4 = new ArrayList(var3.size());

               for (NamespacedKey var6 : var3) {
                  var4.add(var6.getKey());
               }

               return Collections.unmodifiableList(var4);
            }
         }
      }
   }

   public static Object get(ItemStack var0, NamespacedKey var1) {
      for (PersistentDataType var3 : dataTypes) {
         if (var0.getItemMeta().getPersistentDataContainer().has(var1, var3)) {
            return var0.getItemMeta().getPersistentDataContainer().get(var1, var3);
         }
      }

      return null;
   }

   public static void set(Block var0, String var1, PersistentDataType var2, String var3) {
      var0.getChunk().getPersistentDataContainer().set(getNamespace(blockToString(var0) + var1), var2, var3);
   }

   public static Object get(Block var0, String var1) {
      for (PersistentDataType var3 : dataTypes) {
         if (var0.getChunk().getPersistentDataContainer().has(getNamespace(blockToString(var0) + var1), var3)) {
            return var0.getChunk().getPersistentDataContainer().get(getNamespace(blockToString(var0) + var1), var3);
         }
      }

      return null;
   }

   public static void remove(Block var0, String var1) {
      var0.getChunk().getPersistentDataContainer().remove(getNamespace(blockToString(var0) + var1));
   }

   public static boolean has(Block var0, String var1) {
      for (PersistentDataType var3 : dataTypes) {
         if (var0.getChunk().getPersistentDataContainer().has(getNamespace(blockToString(var0) + var1), var3)) {
            return true;
         }
      }

      return false;
   }

   private static String blockToString(Block var0) {
      return var0.getX() + ";" + var0.getY() + ";" + var0.getZ() + ";";
   }

   public static String getKeys(Player var0) {
      return var0.getPersistentDataContainer().getKeys().stream().<CharSequence>map(NamespacedKey::getKey).collect(Collectors.joining(","));
   }
}
