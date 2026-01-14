package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Bukkit;
import org.dynmap.DynmapAPI;

public class DynmapHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.DYNMAP.getPluginName();
   }

   public void setDynmapGeneration(boolean var1) {
      DynmapAPI var2 = (DynmapAPI)Bukkit.getPluginManager().getPlugin(HookPlugin.DYNMAP.getPluginName());

      assert var2 != null;

      var2.setPauseFullRadiusRenders(!var1);
   }
}
