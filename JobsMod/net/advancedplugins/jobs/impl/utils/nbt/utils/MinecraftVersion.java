package net.advancedplugins.jobs.impl.utils.nbt.utils;

import java.util.HashMap;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.nbt.backend.ClassWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum MinecraftVersion {
   Unknown(Integer.MAX_VALUE),
   MC1_7_R4(174),
   MC1_8_R3(183),
   MC1_9_R1(191),
   MC1_9_R2(192),
   MC1_10_R1(1101),
   MC1_11_R1(1111),
   MC1_12_R1(1121),
   MC1_13_R1(1131),
   MC1_13_R2(1132),
   MC1_14_R1(1141),
   MC1_15_R1(1150),
   MC1_16_R1(1160),
   MC1_16_R2(1161),
   MC1_16_R3(1163),
   MC1_17_R1(1170),
   MC1_18_R1(1180, true),
   MC1_18_R2(1182, true),
   MC1_19_R1(1191, true),
   MC1_19_R2(1192, true),
   MC1_19_R3(1194, true),
   MC1_20_R1(1201, true),
   MC1_20_R2(1202, true),
   MC1_20_R3(1204, true),
   MC1_20_R4(1206, true),
   MC1_21_R1(1210, true),
   MC1_21_R2(1212, true),
   MC1_21_R3(1213, true),
   MC1_21_R4(1214, true),
   MC1_21_R5(1215, true),
   MC1_21_R6(1216, true),
   MC1_21_R7(1217, true);

   private static final Map<String, MinecraftVersion> VERSION_TO_REVISION = new HashMap<String, MinecraftVersion>() {
      {
         this.put("1.20", MinecraftVersion.MC1_20_R1);
         this.put("1.20.1", MinecraftVersion.MC1_20_R1);
         this.put("1.20.2", MinecraftVersion.MC1_20_R2);
         this.put("1.20.3", MinecraftVersion.MC1_20_R3);
         this.put("1.20.4", MinecraftVersion.MC1_20_R3);
         this.put("1.20.5", MinecraftVersion.MC1_20_R4);
         this.put("1.20.6", MinecraftVersion.MC1_20_R4);
         this.put("1.21", MinecraftVersion.MC1_21_R1);
         this.put("1.21.1", MinecraftVersion.MC1_21_R1);
         this.put("1.21.2", MinecraftVersion.MC1_21_R2);
         this.put("1.21.3", MinecraftVersion.MC1_21_R2);
         this.put("1.21.4", MinecraftVersion.MC1_21_R3);
         this.put("1.21.5", MinecraftVersion.MC1_21_R4);
         this.put("1.21.6", MinecraftVersion.MC1_21_R5);
         this.put("1.21.7", MinecraftVersion.MC1_21_R5);
         this.put("1.21.8", MinecraftVersion.MC1_21_R5);
         this.put("1.21.9", MinecraftVersion.MC1_21_R6);
         this.put("1.21.10", MinecraftVersion.MC1_21_R7);
         this.put("1.21.11", MinecraftVersion.MC1_21_R7);
      }
   };
   private final int versionId;
   public final boolean mojangMapping;
   private static MinecraftVersion version;
   private static Boolean hasGsonSupport;
   private static Boolean isPaper;

   private MinecraftVersion(int nullxx) {
      this(nullxx, false);
   }

   private MinecraftVersion(int nullxx, boolean nullxxx) {
      this.versionId = nullxx;
      this.mojangMapping = nullxxx;
   }

   public static MinecraftVersion init() {
      if (version != null) {
         return version;
      } else {
         try {
            String var0 = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            version = valueOf(var0.replace("v", "MC"));
         } catch (Exception var1) {
            version = VERSION_TO_REVISION.getOrDefault(Bukkit.getServer().getBukkitVersion().split("-")[0], Unknown);
         }

         isPaper = Package.getPackage("com.destroystokyo.paper") != null;
         if (version == Unknown) {
            Bukkit.getServer().getLogger().warning("You are using invalid version of Minecraft [" + Bukkit.getServer().getBukkitVersion() + "]! Disabling...");
         }

         ClassWrapper.NMS_WORLD.getMojangName();
         return version;
      }
   }

   public static boolean isNew() {
      return getVersionNumber() >= 1130;
   }

   public static boolean isPaper() {
      return isPaper;
   }

   public static int getVersionNumber() {
      return version.versionId;
   }

   public static MinecraftVersion getCurrentVersion() {
      return version;
   }

   public String getPackageName() {
      return getVersion() == Unknown ? Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] : this.name().replace("MC", "v");
   }

   public static boolean hasGsonSupport() {
      if (hasGsonSupport != null) {
         return hasGsonSupport;
      } else {
         try {
            hasGsonSupport = true;
         } catch (Exception var1) {
            hasGsonSupport = false;
         }

         return hasGsonSupport;
      }
   }

   public static boolean isAtLeastVersion(MinecraftVersion var0) {
      return getVersion().getVersionId() >= var0.getVersionId();
   }

   public static boolean isNewerThan(MinecraftVersion var0) {
      return getVersion().getVersionId() > var0.getVersionId();
   }

   public static void disableDuplicateUUIDReporting(JavaPlugin var0) {
   }

   public int getVersionId() {
      return this.versionId;
   }

   public boolean isMojangMapping() {
      return this.mojangMapping;
   }

   public static MinecraftVersion getVersion() {
      return version;
   }
}
