package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import de.myzelyam.api.vanish.VanishAPI;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.jobs.impl.utils.hooks.VanishHook;
import org.bukkit.entity.Player;

public class PremiumVanishHook extends PluginHookInstance implements VanishHook {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.PREMIUMVANISH.getPluginName();
   }

   @Override
   public boolean isPlayerVanished(Player var1) {
      return VanishAPI.isInvisible(var1);
   }
}
