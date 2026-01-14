package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class TownyChatHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.TOWNYCHAT.getPluginName();
   }

   public boolean isInTownyChannel(Player var1) {
      Channel var2 = Chat.getTownyChat().getPlayerChannel(var1);
      return var2 != null && var2.getType() != channelTypes.GLOBAL;
   }
}
