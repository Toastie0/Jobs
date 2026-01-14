package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.PLACEHOLDERAPI.getPluginName();
   }

   public String parsePlaceholder(OfflinePlayer var1, String var2) {
      return PlaceholderAPI.setPlaceholders(var1, var2);
   }

   public String parsePlaceholders(Player var1, Player var2, String var3) {
      var3 = this.parsePlaceholder(var1, var3);
      return PlaceholderAPI.setRelationalPlaceholders(var1, var2, var3);
   }

   public boolean registerPlaceholder(String var1, PlaceholderHook var2) {
      return PlaceholderAPI.registerPlaceholderHook(var1, var2);
   }
}
