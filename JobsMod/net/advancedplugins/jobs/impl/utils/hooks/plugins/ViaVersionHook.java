package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.viaversion.viaversion.api.Via;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class ViaVersionHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.VIAVERSION.getPluginName();
   }

   public String getPlayerVersion(Player var1) {
      return Via.getAPI().getPlayerVersion(var1) + "";
   }
}
