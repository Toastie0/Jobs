package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.earth2me.essentials.Essentials;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.jobs.impl.utils.hooks.VanishHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EssentialsHook extends PluginHookInstance implements VanishHook, Listener {
   public EssentialsHook() {
      Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.ESSENTIALS.getPluginName();
   }

   @Override
   public boolean isPlayerVanished(Player var1) {
      return ((Essentials)this.getPluginInstance()).getUser(var1).isVanished();
   }
}
