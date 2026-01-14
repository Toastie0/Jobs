package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.Zrips.CMI.CMI;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.jobs.impl.utils.hooks.VanishHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CMIHook extends PluginHookInstance implements VanishHook, Listener {
   public CMIHook() {
      Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.CMI.getPluginName();
   }

   @Override
   public boolean isPlayerVanished(Player var1) {
      return CMI.getInstance().getPlayerManager().getUser(var1).isVanished();
   }
}
