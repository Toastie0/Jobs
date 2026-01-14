package net.advancedplugins.jobs.impl.utils.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginHookInstance {
   public boolean isEnabled() {
      return false;
   }

   public String getName() {
      return "";
   }

   public Plugin getPluginInstance() {
      return Bukkit.getPluginManager().getPlugin(this.getName());
   }
}
