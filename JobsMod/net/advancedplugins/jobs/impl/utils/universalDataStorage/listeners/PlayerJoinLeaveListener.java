package net.advancedplugins.jobs.impl.utils.universalDataStorage.listeners;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.PlayerDataHandler;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.StorageType;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.providers.MysqlDataProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinLeaveListener implements Listener {
   private final PlayerDataHandler playerDataHandler;
   private final Plugin plugin;

   public PlayerJoinLeaveListener(Plugin var1, PlayerDataHandler var2) {
      this.plugin = var1;
      this.playerDataHandler = var2;
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      if (this.playerDataHandler.getStorageType() == StorageType.MYSQL) {
         MysqlDataProvider var3 = (MysqlDataProvider)this.playerDataHandler.getDataProvider();
         var3.loadPlayerDataFromDatabase(var2);
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      if (this.playerDataHandler.getStorageType() == StorageType.MYSQL) {
         MysqlDataProvider var3 = (MysqlDataProvider)this.playerDataHandler.getDataProvider();
         CountDownLatch var4 = new CountDownLatch(var3.getPlayerDataCache().size());
         var3.savePlayerDataToDatabase(var2, var3.getPlayerDataCache().getOrDefault(var2, null), var4);
      }
   }
}
