package net.advancedplugins.jobs.impl.utils.hooks.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class HolographicDisplaysHandler extends HologramHandler {
   private HashMap<String, Hologram> holograms = new HashMap<>();

   public HolographicDisplaysHandler(JavaPlugin var1) {
      super(var1);
   }

   @Override
   public String getName() {
      return "HolographicDisplays";
   }

   @Override
   public void createHologram(Location var1, String var2, String var3) {
      Hologram var4 = HologramsAPI.createHologram(this.getPlugin(), var1);
      var4.appendTextLine(var3);
      this.holograms.put(var2, var4);
   }

   @Override
   public void removeHologram(String var1) {
      this.holograms.get(var1).delete();
   }

   @Override
   public void updateHologram(String var1, String var2) {
      Hologram var3 = this.holograms.get(var1);
      var3.removeLine(0);
      var3.appendTextLine(var2);
   }
}
