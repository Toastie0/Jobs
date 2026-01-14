package net.advancedplugins.jobs.impl.utils.hooks.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class DecentHologramsHandler extends HologramHandler {
   public DecentHologramsHandler(JavaPlugin var1) {
      super(var1);
   }

   @Override
   public String getName() {
      return "DecentHolograms";
   }

   @Override
   public void createHologram(Location var1, String var2, String var3) {
      if (DHAPI.getHologram(var2) == null) {
         DHAPI.createHologram(var2, var1, false, Arrays.asList(var3));
      }
   }

   @Override
   public void removeHologram(String var1) {
      Hologram var2 = DHAPI.getHologram(var1);
      if (var2 != null) {
         var2.delete();
         DecentHologramsAPI.get().getHologramManager().removeHologram(var1);
      }
   }

   @Override
   public void updateHologram(String var1, String var2) {
      Hologram var3 = DHAPI.getHologram(var1);
      if (var3 != null) {
         DHAPI.setHologramLines(var3, Arrays.asList(var2));
      }
   }
}
