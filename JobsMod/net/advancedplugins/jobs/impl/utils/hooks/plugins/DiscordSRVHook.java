package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import github.scarsz.discordsrv.DiscordSRV;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class DiscordSRVHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.DISCORDSRV.getPluginName();
   }

   public void processChatMessage(@NotNull Player var1, @NotNull String var2, @NotNull String var3, @NotNull Event var4) {
      DiscordSRV.getPlugin().processChatMessage(var1, var2, var3, false, var4);
   }

   public void processGlobalChatMessage(@NotNull Player var1, @NotNull String var2, @NotNull Event var3) {
      this.processChatMessage(var1, var2, DiscordSRV.getPlugin().getOptionalChannel("global"), var3);
   }
}
