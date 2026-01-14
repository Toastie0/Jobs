package net.advancedplugins.jobs.impl.utils.nbt.backend;

import java.util.logging.Level;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.nbt.utils.PackageWrapper;
import org.bukkit.Bukkit;

public enum ClassWrapper {
   CRAFT_BlockData(PackageWrapper.CRAFTBUKKIT, "block.data.CraftBlockData", MinecraftVersion.MC1_13_R1, null),
   CRAFT_World(PackageWrapper.CRAFTBUKKIT, "CraftWorld", MinecraftVersion.MC1_8_R3, null),
   CRAFT_MagicNumbers(PackageWrapper.CRAFTBUKKIT, "util.CraftMagicNumbers", MinecraftVersion.MC1_8_R3, null),
   NMS_Block(PackageWrapper.NMS, "world.level.block.Block", MinecraftVersion.MC1_8_R3, null),
   CRAFT_ITEMSTACK(PackageWrapper.CRAFTBUKKIT, "inventory.CraftItemStack", MinecraftVersion.MC1_14_R1, null),
   CRAFT_METAITEM(PackageWrapper.CRAFTBUKKIT, "inventory.CraftMetaItem", MinecraftVersion.MC1_14_R1, null),
   CRAFT_ENTITY(PackageWrapper.CRAFTBUKKIT, "entity.CraftEntity", MinecraftVersion.MC1_14_R1, null),
   NMS_ITEMSTACK(PackageWrapper.NMS, "ItemStack", null, null, "net.minecraft.world.item", "net.minecraft.world.item.ItemStack"),
   NMS_BLOCKPOSITION(PackageWrapper.NMS, "BlockPosition", MinecraftVersion.MC1_8_R3, null, "net.minecraft.core", "net.minecraft.core.BlockPos"),
   NMS_WORLD(PackageWrapper.NMS, "World", null, null, "net.minecraft.world.level", "net.minecraft.world.level.Level"),
   NMS_IBLOCKDATA(
      PackageWrapper.NMS,
      "IBlockData",
      MinecraftVersion.MC1_8_R3,
      null,
      "net.minecraft.world.level.block.state",
      "net.minecraft.world.level.block.state.BlockState"
   );

   private Class<?> clazz;
   private String mojangName = "";

   private ClassWrapper(PackageWrapper nullxx, String nullxxx, MinecraftVersion nullxxxx, MinecraftVersion nullxxxxx) {
      this(nullxx, nullxxx, nullxxxx, nullxxxxx, null, null);
   }

   private ClassWrapper(PackageWrapper nullxx, String nullxxx, MinecraftVersion nullxxxx, MinecraftVersion nullxxxxx, String nullxxxxxx, String nullxxxxxxx) {
      this.mojangName = nullxxxxxxx;
      if (nullxxxx == null || MinecraftVersion.getVersion().getVersionId() >= nullxxxx.getVersionId()) {
         if (nullxxxxx == null || MinecraftVersion.getVersion().getVersionId() <= nullxxxxx.getVersionId()) {
            try {
               if (nullxx == PackageWrapper.NONE) {
                  this.clazz = Class.forName(nullxxx);
               } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_21_R7) && nullxxxxxxx != null) {
                  try {
                     this.clazz = Class.forName(nullxxxxxxx);
                  } catch (ClassNotFoundException var23) {
                     this.clazz = Class.forName(nullxxxxxx + "." + nullxxx);
                  } finally {
                     if (this.clazz == null) {
                        Bukkit.getLogger().warning("Failed to load class " + nullxxx);
                     }
                  }
               } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
                  if (MinecraftVersion.isPaper()) {
                     if (nullxxxxxx != null) {
                        this.clazz = Class.forName(nullxxxxxx + "." + nullxxx);
                     } else {
                        this.clazz = Class.forName(
                           (nullxx.equals(PackageWrapper.NMS) ? nullxx.getUri().split(".server")[0] + "." : nullxx.getUri() + ".") + nullxxx
                        );
                     }
                  } else {
                     String var27 = MinecraftVersion.getVersion().getPackageName();
                     if (nullxxxxxx != null) {
                        this.clazz = Class.forName(nullxxxxxx + "." + nullxxx);
                     } else {
                        this.clazz = Class.forName(
                           (nullxx.equals(PackageWrapper.NMS) ? nullxx.getUri().split(".server")[0] + "." : nullxx.getUri() + "." + var27 + ".") + nullxxx
                        );
                     }
                  }
               } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1) && nullxxxxxxx != null) {
                  try {
                     this.clazz = Class.forName(nullxxxxxxx);
                  } catch (ClassNotFoundException var22) {
                     this.clazz = Class.forName(nullxxxxxx + "." + nullxxx);
                  } finally {
                     if (this.clazz == null) {
                        Bukkit.getLogger().warning("Failed to load class " + nullxxx);
                     }
                  }
               } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1) && nullxxxxxx != null) {
                  this.clazz = Class.forName(nullxxxxxx + "." + nullxxx);
               } else if (nullxx == PackageWrapper.CRAFTBUKKIT) {
                  this.clazz = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + "." + nullxxx);
               } else {
                  String var9 = MinecraftVersion.getVersion().getPackageName();
                  String var10 = nullxx.equals(PackageWrapper.NMS) ? nullxx.getUri().split(".server")[0] : nullxx.getUri() + "." + var9;
                  this.clazz = Class.forName(var10 + "." + nullxxx);
               }

               if (this.clazz == null) {
                  Bukkit.getLogger().warning("Failed to load class " + nullxxx);
               }
            } catch (Throwable var26) {
               if (FoliaScheduler.isFolia()) {
                  Bukkit.getLogger().log(Level.WARNING, "[AdvancedPlugins] Skipping class '" + nullxxx + "' due to Folia");
               } else {
                  Bukkit.getLogger().log(Level.WARNING, "[AdvancedPlugins] Error while trying to resolve the class '" + nullxxx + "'!", var26);
               }
            }
         }
      }
   }

   private ClassWrapper(PackageWrapper nullxx, String nullxxx, String nullxxxx, MinecraftVersion nullxxxxx, boolean nullxxxxxx) {
      this(nullxx, nullxxx + "." + nullxxxx, nullxxxxx, null, null, null);
   }

   private ClassWrapper(PackageWrapper nullxx, String nullxxx, String nullxxxx, MinecraftVersion nullxxxxx) {
      this(nullxx, nullxxx, nullxxxx, nullxxxxx, false);
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public String getMojangName() {
      return this.mojangName;
   }
}
