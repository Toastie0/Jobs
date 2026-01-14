package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.pets.api.APAPI;
import org.bukkit.entity.Player;

public class APetsHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.ADVANCEDPETS.getPluginName();
   }

   public int getPetLevel(Player var1) {
      return APAPI.getPetLevel(var1);
   }
}
