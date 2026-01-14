package net.advancedplugins.jobs.impl.utils.hooks.holograms;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramHandler {
   private final JavaPlugin plugin;

   public HologramHandler(JavaPlugin var1) {
      this.plugin = var1;
   }

   public double getOffset() {
      return 1.0;
   }

   public String getName() {
      return "?";
   }

   public void createHologram(Location var1, String var2, String var3) {
   }

   public void updateHologram(String var1, String var2) {
   }

   protected void removeFromList(String var1) {
   }

   public void removeHologram(String var1) {
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }
}
