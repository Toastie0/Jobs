package net.advancedplugins.jobs.impl.utils.hooks.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class CMIHologramHandler extends HologramHandler {
   private HologramManager manager = null;

   public CMIHologramHandler(JavaPlugin var1) {
      super(var1);
      this.manager = ((CMI)Bukkit.getPluginManager().getPlugin("CMI")).getHologramManager();
   }

   @Override
   public String getName() {
      return "CMI";
   }

   @Override
   public void createHologram(Location var1, String var2, String var3) {
      CMIHologram var4 = new CMIHologram(var2, var1);
      var4.setLines(Arrays.asList(var3));
      this.manager.addHologram(var4);
      var4.update();
   }

   @Override
   public void removeHologram(String var1) {
      CMIHologram var2 = this.manager.getByName(var1);
      if (var2 != null) {
         this.manager.removeHolo(var2);
      }
   }

   @Override
   public void updateHologram(String var1, String var2) {
      CMIHologram var3 = this.manager.getByName(var1);
      var3.setLines(Arrays.asList(var2));
   }
}
