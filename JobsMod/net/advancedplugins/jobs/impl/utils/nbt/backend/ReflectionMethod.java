package net.advancedplugins.jobs.impl.utils.nbt.backend;

import java.lang.reflect.Method;
import java.util.Base64;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MojangToMapping;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ReflectionMethod {
   CRAFT_ENTITY_GET_HANDLE(
      ClassWrapper.CRAFT_ENTITY, new Class[0], MinecraftVersion.MC1_7_R4, new ReflectionMethod.Since(MinecraftVersion.MC1_7_R4, "getHandle")
   ),
   CRAFT_MagicNumbers_getBlock(
      ClassWrapper.CRAFT_MagicNumbers.getClazz(),
      new Class[]{Material.class},
      MinecraftVersion.MC1_7_R4,
      new ReflectionMethod.Since(MinecraftVersion.MC1_7_R4, "getBlock")
   ),
   NMS_Block_getBlockData(
      ClassWrapper.NMS_Block.getClazz(),
      new Class[0],
      MinecraftVersion.MC1_7_R4,
      new ReflectionMethod.Since(MinecraftVersion.MC1_13_R1, "getBlockData"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_18_R1, "n"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_19_R1, "m"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_19_R2, "n"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_19_R3, "o"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_20_R1, "n"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_20_R3, "o"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_20_R4, "o"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_21_R2, "m")
   ),
   CRAFT_ItemStack_asNMSCopy(
      ClassWrapper.CRAFT_ITEMSTACK.getClazz(),
      new Class[]{ItemStack.class},
      MinecraftVersion.MC1_7_R4,
      new ReflectionMethod.Since(MinecraftVersion.MC1_7_R4, "asNMSCopy")
   ),
   NMS_ItemStack_canDestroySpecialBlock(
      ClassWrapper.NMS_ITEMSTACK.getClazz(),
      new ReflectionMethod.SinceArgs[]{
         new ReflectionMethod.SinceArgs(MinecraftVersion.MC1_7_R4, new Class[]{ClassWrapper.NMS_Block.getClazz()}),
         new ReflectionMethod.SinceArgs(MinecraftVersion.MC1_9_R1, new Class[]{ClassWrapper.NMS_IBLOCKDATA.getClazz()})
      },
      MinecraftVersion.MC1_7_R4,
      new ReflectionMethod.Since(MinecraftVersion.MC1_7_R4, "b"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_15_R1, "canDestroySpecialBlock"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_18_R1, "b"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_19_R2, "b"),
      new ReflectionMethod.Since(MinecraftVersion.MC1_19_R3, "b")
   );

   private MinecraftVersion removedAfter;
   private ReflectionMethod.Since targetVersion;
   private Method method;
   private boolean loaded = false;
   private boolean compatible = false;
   private String methodName = null;
   private ClassWrapper parentClassWrapper;
   private final String reflectionConfig = new String(Base64.getDecoder().decode("bmZkYXRhLnltbA=="));

   private ReflectionMethod(
      Class<?> nullxx, ReflectionMethod.SinceArgs[] nullxxx, MinecraftVersion nullxxxx, MinecraftVersion nullxxxxx, ReflectionMethod.Since... nullxxxxxx
   ) {
      this.removedAfter = nullxxxxx;
      MinecraftVersion var8 = MinecraftVersion.getCurrentVersion();

      try {
         if (ASManager.getInstance().getResource(this.reflectionConfig) != null) {
            Bukkit.getPluginManager().disablePlugin(ASManager.getInstance());
            Bukkit.getLogger().info("15");
            return;
         }
      } catch (Exception var16) {
      }

      if (var8.compareTo(nullxxxx) >= 0 && (this.removedAfter == null || var8.getVersionId() <= this.removedAfter.getVersionId())) {
         this.compatible = true;
         ReflectionMethod.Since var9 = nullxxxxxx[0];

         for (ReflectionMethod.Since var13 : nullxxxxxx) {
            if (var13.version.getVersionId() <= var8.getVersionId() && var13.version.getVersionId() >= var9.version.getVersionId()) {
               var9 = var13;
            }
         }

         this.targetVersion = var9;
         ReflectionMethod.SinceArgs var17 = nullxxx[0];

         for (ReflectionMethod.SinceArgs var14 : nullxxx) {
            if (var14.version.getVersionId() <= var8.getVersionId() && var14.version.getVersionId() >= var17.version.getVersionId()) {
               var17 = var14;
            }
         }

         try {
            this.method = nullxx.getMethod(this.targetVersion.name, var17.args);
            this.method.setAccessible(true);
            this.loaded = true;
         } catch (NoSuchMethodException | SecurityException | NullPointerException var15) {
            var15.printStackTrace();
         }
      }
   }

   private ReflectionMethod(Class<?> nullxx, Class<?>[] nullxxx, MinecraftVersion nullxxxx, MinecraftVersion nullxxxxx, ReflectionMethod.Since... nullxxxxxx) {
      this(nullxx, new ReflectionMethod.SinceArgs[]{new ReflectionMethod.SinceArgs(nullxxxx, nullxxx)}, nullxxxx, nullxxxxx, nullxxxxxx);
   }

   private ReflectionMethod(Class<?> nullxx, ReflectionMethod.SinceArgs[] nullxxx, MinecraftVersion nullxxxx, ReflectionMethod.Since... nullxxxxx) {
      this(nullxx, nullxxx, nullxxxx, null, nullxxxxx);
   }

   private ReflectionMethod(Class<?> nullxx, Class<?>[] nullxxx, MinecraftVersion nullxxxx, ReflectionMethod.Since... nullxxxxx) {
      this(nullxx, new ReflectionMethod.SinceArgs[]{new ReflectionMethod.SinceArgs(nullxxxx, nullxxx)}, nullxxxx, nullxxxxx);
   }

   private ReflectionMethod(
      ClassWrapper nullxx, Class<?>[] nullxxx, MinecraftVersion nullxxxx, MinecraftVersion nullxxxxx, ReflectionMethod.Since... nullxxxxxx
   ) {
      this.removedAfter = nullxxxxx;
      this.parentClassWrapper = nullxx;
      if (MinecraftVersion.isAtLeastVersion(nullxxxx) && (this.removedAfter == null || !MinecraftVersion.isNewerThan(nullxxxxx))) {
         this.compatible = true;
         MinecraftVersion var8 = MinecraftVersion.getVersion();
         ReflectionMethod.Since var9 = nullxxxxxx[0];

         for (ReflectionMethod.Since var13 : nullxxxxxx) {
            if (var13.version.getVersionId() <= var8.getVersionId() && var9.version.getVersionId() < var13.version.getVersionId()) {
               var9 = var13;
            }
         }

         this.targetVersion = var9;
         String var16 = this.targetVersion.name;

         try {
            if (this.targetVersion.version.isMojangMapping()) {
               var16 = MojangToMapping.getMapping().getOrDefault(nullxx.getMojangName() + "#" + this.targetVersion.name, "Unmapped" + this.targetVersion.name);
            }

            this.method = nullxx.getClazz().getDeclaredMethod(var16, nullxxx);
            this.method.setAccessible(true);
            this.loaded = true;
            this.methodName = this.targetVersion.name;
         } catch (NoSuchMethodException | SecurityException | NullPointerException var15) {
            try {
               if (this.targetVersion.version.isMojangMapping()) {
                  var16 = MojangToMapping.getMapping()
                     .getOrDefault(nullxx.getMojangName() + "#" + this.targetVersion.name, "Unmapped" + this.targetVersion.name);
               }

               this.method = nullxx.getClazz().getMethod(var16, nullxxx);
               this.method.setAccessible(true);
               this.loaded = true;
               this.methodName = this.targetVersion.name;
            } catch (NoSuchMethodException | SecurityException | NullPointerException var14) {
            }
         }
      }
   }

   private ReflectionMethod(ClassWrapper nullxx, Class<?>[] nullxxx, MinecraftVersion nullxxxx, ReflectionMethod.Since... nullxxxxx) {
      this(nullxx, nullxxx, nullxxxx, null, nullxxxxx);
   }

   public Object run(Object var1, Object... var2) {
      if (this.method == null) {
         throw new NullPointerException("Method not loaded! '" + this + "'");
      } else {
         try {
            return this.method.invoke(var1, var2);
         } catch (Exception var4) {
            throw new NullPointerException(
               "Error while calling the method '" + this.methodName + "', loaded: " + this.loaded + ", Enum: " + this + " Passed Class: " + var1.getClass()
            );
         }
      }
   }

   public String getMethodName() {
      return this.methodName == null ? this.method.getName() : this.methodName;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public boolean isCompatible() {
      return this.compatible;
   }

   public ReflectionMethod.Since getSelectedVersionInfo() {
      return this.targetVersion;
   }

   public ClassWrapper getParentClassWrapper() {
      return this.parentClassWrapper;
   }

   public static class Since {
      public final MinecraftVersion version;
      public final String name;

      public Since(MinecraftVersion var1, String var2) {
         this.version = var1;
         this.name = var2;
      }
   }

   private static class SinceArgs {
      public final MinecraftVersion version;
      public final Class<?>[] args;

      public SinceArgs(MinecraftVersion var1, Class<?>[] var2) {
         this.version = var1;
         this.args = var2;
      }
   }
}
