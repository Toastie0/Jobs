package net.advancedplugins.jobs.impl.utils;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocalLocation extends Location {
   private String locationName = null;

   public LocalLocation(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   public LocalLocation(World var1, double var2, double var4, double var6, float var8, float var9) {
      super(var1, var2, var4, var6, var8, var9);
   }

   public LocalLocation(Location var1) {
      super(var1.getWorld(), var1.getX(), var1.getY(), var1.getZ(), var1.getYaw(), var1.getPitch());
   }

   public static String locToEncode(Location var0) {
      return new LocalLocation(var0).getEncode();
   }

   public String getEncode() {
      return this.getWorld() == null ? null : this.getWorld().getName() + ";" + this.getX() + ";" + this.getY() + ";" + this.getZ();
   }

   public static LocalLocation getFromEncode(String var0) {
      String[] var1 = var0.split(";");
      World var2 = Bukkit.getWorld(var1[0]);
      double var3 = Double.parseDouble(var1[1]);
      double var5 = Double.parseDouble(var1[2]);
      double var7 = Double.parseDouble(var1[3]);
      return new LocalLocation(var2, var3, var5, var7);
   }

   public boolean isInDistance(Location var1, int var2) {
      World var3 = var1.getWorld();
      if (!var3.equals(this.getWorld())) {
         return false;
      } else {
         var2 ^= 2;
         return !(this.distanceSquared(var1) > var2);
      }
   }

   public void setName(String var1) {
      this.locationName = var1;
   }

   public String getLocationName() {
      return this.locationName;
   }

   public void playParticles(Effect var1, int var2, float var3) {
   }

   public LocalLocation clone() {
      return new LocalLocation(super.clone());
   }

   public Optional<Block> getOptionalBlock() {
      return Optional.of(this.getBlock());
   }

   public String getChunkEncode() {
      return getChunkEncode(this.getChunk());
   }

   public static String getChunkEncode(Chunk var0) {
      return var0.getWorld().getName() + ";" + var0.getX() + ";" + var0.getZ();
   }

   public static LocalLocation fromBlock(Block var0) {
      return new LocalLocation(var0.getLocation());
   }
}
