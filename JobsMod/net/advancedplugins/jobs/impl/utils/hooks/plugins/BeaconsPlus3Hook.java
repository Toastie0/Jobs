package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import thito.beaconplus.BeaconAPI;

public class BeaconsPlus3Hook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.BEACONPLUS3.getPluginName();
   }

   public boolean isBeaconPlus(Location var1) {
      return BeaconAPI.getAPI().getBeaconData(var1) != null;
   }
}
