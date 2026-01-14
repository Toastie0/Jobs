package net.advancedplugins.jobs.creator.listeners;

import net.advancedplugins.jobs.creator.GUICreator;
import net.advancedplugins.jobs.creator.GUICreatorHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
   @EventHandler
   public void onChat(AsyncPlayerChatEvent var1) {
      String var2 = var1.getMessage();
      Player var3 = var1.getPlayer();
      GUICreator var4 = GUICreatorHandler.getHandler().getEditor(var3.getUniqueId());
      if (var4 != null) {
         if (var4.hasChatRequest()) {
            var4.fulfillChatRequest(var2);
            var1.setCancelled(true);
         }
      }
   }
}
