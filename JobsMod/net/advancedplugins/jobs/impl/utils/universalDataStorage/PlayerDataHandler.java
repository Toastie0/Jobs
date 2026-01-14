package net.advancedplugins.jobs.impl.utils.universalDataStorage;

import java.util.Map;
import java.util.UUID;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.listeners.PlayerJoinLeaveListener;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.providers.LocalDataProvider;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.providers.MysqlDataProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDataHandler {
   private final PlayerDataProvider dataProvider;
   private final StorageType storageType;
   private final Plugin plugin;
   private final long saveIntervalTicks;

   public PlayerDataHandler(Plugin var1, StorageType var2, Map<String, Object> var3) {
      this.plugin = var1;
      this.storageType = var2;
      this.saveIntervalTicks = 12000L;
      switch (var2) {
         case LOCAL:
            this.dataProvider = new LocalDataProvider(var1);
            break;
         case MYSQL:
            this.dataProvider = new MysqlDataProvider(var1, var3);
            break;
         default:
            throw new IllegalArgumentException("Invalid storage type: " + var2);
      }

      this.startAutoSaveTask();
      if (var2 == StorageType.MYSQL) {
         var1.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(var1, this), var1);
      }
   }

   private void startAutoSaveTask() {
      (new BukkitRunnable() {
         public void run() {
            PlayerDataHandler.this.saveAllPlayerDataAsync();
            ASManager.debug("Auto-saving all player data...");
         }
      }).runTaskTimerAsynchronously(this.plugin, this.saveIntervalTicks, this.saveIntervalTicks);
   }

   private void saveAllPlayerDataAsync() {
      this.dataProvider.saveAllPlayerDataAsync();
   }

   public boolean isPlayerDataLoaded(UUID var1) {
      return this.dataProvider.isPlayerDataLoaded(var1);
   }

   public <T> T getData(UUID var1, String var2, Class<T> var3) {
      return this.dataProvider.getData(var1, var2, var3);
   }

   public void saveData(UUID var1, String var2, Object var3) {
      this.dataProvider.saveData(var1, var2, var3);
   }

   public void removeData(UUID var1, String var2) {
      this.dataProvider.removeData(var1, var2);
   }

   public void clearPlayerData(UUID var1) {
      this.dataProvider.clearPlayerData(var1);
   }

   public void shutdown() {
      this.plugin.getLogger().info("Shutting down PlayerDataHandler, saving all data...");
      this.saveAllPlayerDataSync();
      this.dataProvider.shutdown();
   }

   private void saveAllPlayerDataSync() {
      this.dataProvider.saveAllPlayerDataSync();
   }

   public PlayerDataProvider getDataProvider() {
      return this.dataProvider;
   }

   public StorageType getStorageType() {
      return this.storageType;
   }

   public Plugin getPlugin() {
      return this.plugin;
   }

   public long getSaveIntervalTicks() {
      return this.saveIntervalTicks;
   }
}
