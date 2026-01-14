package net.advancedplugins.jobs.impl.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.nbt.backend.ClassWrapper;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;

public class ReallyFastBlockHandler {
   private static final Map<World, ReallyFastBlockHandler> handlers = new HashMap<>();
   private static Class<?> craftWorldClass;
   private static Class<?> craftBlockData;
   private static Class<?> craftMagicNumbersClass;
   private static Constructor<?> blockPos;
   private static Method getHandle;
   private static Method setType;
   private static Method getState;
   private static Method getMagicBlock;
   private static Method getMagicBlockData;
   private Object nmsWorld;

   public static ReallyFastBlockHandler getForWorld(World var0) {
      if (handlers.containsKey(var0)) {
         return handlers.get(var0);
      } else {
         ReallyFastBlockHandler var1 = new ReallyFastBlockHandler(var0);
         handlers.put(var0, var1);
         return var1;
      }
   }

   public static void init() {
      try {
         Class var0 = ClassWrapper.NMS_WORLD.getClazz();
         craftWorldClass = ClassWrapper.CRAFT_World.getClazz();
         Class var1 = ClassWrapper.NMS_BLOCKPOSITION.getClazz();
         Class var2 = ClassWrapper.NMS_IBLOCKDATA.getClazz();
         if (MinecraftVersion.isNew()) {
            craftBlockData = ClassWrapper.CRAFT_BlockData.getClazz();
            getState = craftBlockData.getMethod("getState");
         }

         blockPos = var1.getConstructor(int.class, int.class, int.class);
         getHandle = craftWorldClass.getMethod("getHandle");
         setType = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1)
            ? var0.getMethod("a", var1, var2, int.class)
            : var0.getMethod("setTypeAndData", var1, var2, int.class);
         if (!MinecraftVersion.isNew()) {
            craftMagicNumbersClass = ClassWrapper.CRAFT_MagicNumbers.getClazz();
            getMagicBlock = craftMagicNumbersClass.getMethod("getBlock", Material.class);
            Class var3 = ClassWrapper.NMS_Block.getClazz();
            getMagicBlockData = var3.getMethod("getBlockData");
         }
      } catch (NoSuchMethodException var4) {
         var4.printStackTrace();
      }
   }

   public ReallyFastBlockHandler(World var1) {
      Object var2 = craftWorldClass.cast(var1);

      try {
         this.nmsWorld = getHandle.invoke(var2);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         var4.printStackTrace();
      }
   }

   public void setType(Material var1, Block... var2) {
      if (!Bukkit.isPrimaryThread()) {
         FoliaScheduler.runTask(ASManager.getInstance(), () -> this.setType(var1, var2));
      } else {
         try {
            Object var3 = MinecraftVersion.isNew()
               ? getState.invoke(craftBlockData.cast(var1.createBlockData()))
               : getMagicBlockData.invoke(getMagicBlock.invoke(craftMagicNumbersClass, var1));

            for (Block var7 : var2) {
               if (var7.getType() != var1) {
                  boolean var8 = false;
                  int var9 = 0;
                  if (var7.getBlockData() instanceof Waterlogged var10) {
                     var8 = var10.isWaterlogged();
                  }

                  if (MinecraftVersion.isPaper()) {
                     String var14 = var7.getBlockData().getMaterial().name();
                     if (var14.equals("SEA_PICKLE") || var14.contains("CORAL") || var14.contains("KELP") || var14.contains("SEAGRASS")) {
                        var9 = var7.getWorld().getFluidData(var7.getLocation()).getLevel();
                     }
                  }

                  Object var15 = blockPos.newInstance(var7.getX(), var7.getY(), var7.getZ());
                  setType.invoke(this.nmsWorld, var15, var3, 3);
                  if (var8 || var9 > 0) {
                     var7.setType(Material.WATER);
                  }

                  if (var9 > 0 && var7.getBlockData() instanceof Levelled var16) {
                     var16.setLevel(var9);
                     var7.setBlockData(var16);
                  }
               }
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         }
      }
   }

   static {
      init();
   }
}
