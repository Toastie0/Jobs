package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import us.lynuxcraft.deadsilenceiv.advancedchests.AdvancedChestsPlugin;

public class AdvancedChestsHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.ADVANCEDCHESTS.getPluginName();
   }

   public boolean isAdvancedChest(Location var1) {
      return AdvancedChestsPlugin.getInstance().getChestsManager().getAdvancedChest(var1) != null;
   }
}
