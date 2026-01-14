package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LWCHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.LWC.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      Protection var3 = LWC.getInstance().findProtection(var2);
      return var3 != null ? LWC.getInstance().canAccessProtection(var1, var2.getBlock()) : true;
   }
}
