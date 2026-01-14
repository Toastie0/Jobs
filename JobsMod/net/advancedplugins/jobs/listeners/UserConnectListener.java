package net.advancedplugins.jobs.listeners;

import java.util.UUID;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.validator.Validator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserConnectListener implements Listener {
   private static UserConnectListener connectionListener;
   private final Core core;
   private final UserCache userCache;
   private final boolean bungeeFix;

   public UserConnectListener(Core var1) {
      connectionListener = this;
      this.core = var1;
      this.userCache = var1.getUserCache();
      this.bungeeFix = var1.getConfig("config").bool("storage-options.bungee-fix");
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.bungeeFix) {
         Bukkit.getScheduler().runTaskLater(this.core, () -> this.loadPlayer(var2), 20L);
      } else {
         this.loadPlayer(var2);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      this.userCache.unload(var2, true);
      this.core.getBossBars().forEach(var1x -> {
         if (var1x.containsKey(var2)) {
            var1x.get(var2).endDisplay();
         }
      });
   }

   public void loadPlayer(Player var1) {
      this.userCache.load(var1.getUniqueId()).thenAccept(Validator::fixUser);
   }

   public static UserConnectListener getConnectionListener() {
      return connectionListener;
   }
}
