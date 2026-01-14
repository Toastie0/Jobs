package net.advancedplugins.jobs.impl.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCreator {
   private static boolean warningPosted = true;
   private static Field blockProfileField;
   private static Method metaSetProfileMethod;
   private static Field metaProfileField;
   private static final Map<String, GameProfile> profilesCache = new HashMap<>();

   private SkullCreator() {
   }

   public static ItemStack createSkull() {
      checkLegacy();

      try {
         return new ItemStack(Material.valueOf("PLAYER_HEAD"));
      } catch (IllegalArgumentException var1) {
         return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
      }
   }

   /** @deprecated */
   public static ItemStack itemFromName(String var0) {
      return itemWithName(createSkull(), var0);
   }

   public static ItemStack itemFromUuid(UUID var0) {
      return itemWithUuid(createSkull(), var0);
   }

   public static ItemStack itemFromUrl(String var0) {
      return itemWithUrl(createSkull(), var0);
   }

   public static ItemStack itemFromBase64(String var0) {
      return itemWithBase64(createSkull(), var0);
   }

   @Deprecated
   public static ItemStack itemWithName(ItemStack var0, String var1) {
      notNull(var0, "item");
      notNull(var1, "name");
      if (!var1.isEmpty() && !var1.equalsIgnoreCase("-")) {
         SkullMeta var2 = (SkullMeta)var0.getItemMeta();
         var2.setOwner(var1);
         var0.setItemMeta(var2);
         return var0;
      } else {
         return var0;
      }
   }

   public static ItemStack itemWithUuid(ItemStack var0, UUID var1) {
      notNull(var0, "item");
      notNull(var1, "id");
      SkullMeta var2 = (SkullMeta)var0.getItemMeta();
      var2.setOwningPlayer(Bukkit.getOfflinePlayer(var1));
      var0.setItemMeta(var2);
      return var0;
   }

   public static ItemStack itemWithUrl(ItemStack var0, String var1) {
      notNull(var0, "item");
      notNull(var1, "url");
      return itemWithBase64(var0, urlToBase64(var1));
   }

   public static ItemStack itemWithBase64(ItemStack var0, String var1) {
      if (!(var0.getItemMeta() instanceof SkullMeta)) {
         return null;
      } else {
         SkullMeta var2 = (SkullMeta)var0.getItemMeta();
         mutateItemMeta(var2, var1);
         var0.setItemMeta(var2);
         return var0;
      }
   }

   @Deprecated
   public static void blockWithName(Block var0, String var1) {
      notNull(var0, "block");
      notNull(var1, "name");
      Skull var2 = (Skull)var0.getState();
      var2.setOwningPlayer(Bukkit.getOfflinePlayer(var1));
      var2.update(false, false);
   }

   public static void blockWithUuid(Block var0, UUID var1) {
      notNull(var0, "block");
      notNull(var1, "id");
      setToSkull(var0);
      Skull var2 = (Skull)var0.getState();
      var2.setOwningPlayer(Bukkit.getOfflinePlayer(var1));
      var2.update(false, false);
   }

   public static void blockWithUrl(Block var0, String var1) {
      notNull(var0, "block");
      notNull(var1, "url");
      blockWithBase64(var0, urlToBase64(var1));
   }

   public static void blockWithBase64(Block var0, String var1) {
      notNull(var0, "block");
      notNull(var1, "base64");
      setToSkull(var0);
      Skull var2 = (Skull)var0.getState();
      mutateBlockState(var2, var1);
      var2.update(false, false);
   }

   private static void setToSkull(Block var0) {
      checkLegacy();

      try {
         var0.setType(Material.valueOf("PLAYER_HEAD"), false);
      } catch (IllegalArgumentException var3) {
         var0.setType(Material.valueOf("SKULL"), false);
         Skull var2 = (Skull)var0.getState();
         var2.setSkullType(SkullType.PLAYER);
         var2.update(false, false);
      }
   }

   private static void notNull(Object var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1 + " should not be null!");
      }
   }

   private static String urlToBase64(String var0) {
      URI var1;
      try {
         var1 = new URI(var0);
      } catch (URISyntaxException var3) {
         throw new RuntimeException(var3);
      }

      String var2 = "{\"textures\":{\"SKIN\":{\"url\":\"" + var1.toString() + "\"}}}";
      return Base64.getEncoder().encodeToString(var2.getBytes());
   }

   private static GameProfile makeProfile(String var0) {
      if (profilesCache.containsKey(var0)) {
         return profilesCache.get(var0);
      } else {
         UUID var1 = new UUID(var0.substring(var0.length() - 20).hashCode(), var0.substring(var0.length() - 10).hashCode());

         try {
            Class var2 = Class.forName("com.mojang.authlib.GameProfile");
            Class var3 = Class.forName("com.mojang.authlib.properties.Property");
            Object var4 = var2.getConstructor(UUID.class, String.class).newInstance(var1, "aaaaa");
            Object var5 = var3.getConstructor(String.class, String.class).newInstance("textures", var0);
            Method var6 = var4.getClass().getMethod("getProperties");
            Object var7 = var6.invoke(var4);
            Method var8 = var7.getClass().getMethod("put", Object.class, Object.class);
            var8.invoke(var7, "textures", var5);
            profilesCache.put(var0, (GameProfile)var4);
            return (GameProfile)var4;
         } catch (ReflectiveOperationException var9) {
            var9.printStackTrace();
            return null;
         }
      }
   }

   private static void mutateBlockState(Skull var0, String var1) {
      try {
         if (blockProfileField == null) {
            blockProfileField = var0.getClass().getDeclaredField("profile");
            blockProfileField.setAccessible(true);
         }

         blockProfileField.set(var0, makeProfile(var1));
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         var3.printStackTrace();
      }
   }

   private static void mutateItemMeta(SkullMeta var0, String var1) {
      if (MinecraftVersion.isPaper()) {
         UUID var2 = new UUID(var1.substring(var1.length() - 20).hashCode(), var1.substring(var1.length() - 10).hashCode());
         PlayerProfile var6 = Bukkit.createProfile(var2, var2.toString().substring(0, 16));
         var6.setProperty(new ProfileProperty("textures", var1));
         var0.setPlayerProfile(var6);
      } else {
         try {
            if (metaSetProfileMethod == null) {
               metaSetProfileMethod = var0.getClass().getDeclaredMethod("setProfile", GameProfile.class);
               metaSetProfileMethod.setAccessible(true);
            }

            metaSetProfileMethod.invoke(var0, makeProfile(var1));
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var5) {
            try {
               if (metaProfileField == null) {
                  metaProfileField = var0.getClass().getDeclaredField("profile");
                  metaProfileField.setAccessible(true);
               }

               Object var3 = makeProfile(var1);
               if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_21_R1)) {
                  var3 = Class.forName("net.minecraft.world.item.component.ResolvableProfile").getConstructor(GameProfile.class).newInstance(var3);
               }

               metaProfileField.set(var0, var3);
            } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException var4) {
               var4.printStackTrace();
            }
         }
      }
   }

   private static void checkLegacy() {
      try {
         Material.class.getDeclaredField("PLAYER_HEAD");
         Material.valueOf("SKULL");
         if (!warningPosted) {
            warningPosted = true;
         }
      } catch (IllegalArgumentException | NoSuchFieldException var1) {
      }
   }
}
