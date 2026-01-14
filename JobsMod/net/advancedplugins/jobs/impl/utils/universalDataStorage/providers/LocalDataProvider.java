package net.advancedplugins.jobs.impl.utils.universalDataStorage.providers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.PlayerDataProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LocalDataProvider implements PlayerDataProvider {
   private final Plugin plugin;
   private final File dataFolder;
   private final Map<UUID, Map<String, Object>> playerDataCache = new HashMap<>();

   public LocalDataProvider(Plugin var1) {
      this.plugin = var1;
      this.dataFolder = new File(var1.getDataFolder(), "playerdata");
      if (!this.dataFolder.exists()) {
         this.dataFolder.mkdirs();
      }
   }

   @Override
   public <T> T getData(UUID var1, String var2, Class<T> var3) {
      Map var4 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromFile);
      if (var4.containsKey(var2)) {
         Object var5 = var4.get(var2);
         if (var3.isInstance(var5)) {
            return (T)var3.cast(var5);
         } else {
            this.plugin
               .getLogger()
               .warning("Data type mismatch for player " + var1 + ", key " + var2 + ". Expected " + var3.getName() + ", got " + var5.getClass().getName());
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void saveData(UUID var1, String var2, Object var3) {
      Map var4 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromFile);
      var4.put(var2, var3);
      this.savePlayerDataToFile(var1, var4);
   }

   @Override
   public void removeData(UUID var1, String var2) {
      Map var3 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromFile);
      var3.remove(var2);
      this.savePlayerDataToFile(var1, var3);
   }

   @Override
   public void clearPlayerData(UUID var1) {
      this.playerDataCache.remove(var1);
      File var2 = new File(this.dataFolder, var1 + ".yml");
      if (var2.exists()) {
         var2.delete();
      }
   }

   @Override
   public CompletableFuture<Map<String, Object>> getPlayerDataAsync(final UUID var1) {
      final CompletableFuture var2 = new CompletableFuture();
      (new BukkitRunnable() {
         public void run() {
            final Map var1x = LocalDataProvider.this.loadPlayerDataFromFile(var1);
            (new BukkitRunnable() {
               public void run() {
                  var2.complete(var1x);
               }
            }).runTask(LocalDataProvider.this.plugin);
         }
      }).runTaskAsynchronously(this.plugin);
      return var2;
   }

   @Override
   public void shutdown() {
      this.playerDataCache.forEach(this::savePlayerDataToFile);
      this.playerDataCache.clear();
   }

   @Override
   public boolean isPlayerDataLoaded(UUID var1) {
      return true;
   }

   private Map<String, Object> loadPlayerDataFromFile(UUID var1) {
      File var2 = new File(this.dataFolder, var1 + ".yml");
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
      HashMap var4 = new HashMap();
      if (var2.exists()) {
         ConfigurationSection var5 = var3.getConfigurationSection("data");
         if (var5 != null) {
            for (String var7 : var5.getKeys(false)) {
               var4.put(var7, var5.get(var7));
            }
         }
      }

      return var4;
   }

   private void savePlayerDataToFile(UUID var1, Map<String, Object> var2) {
      File var3 = new File(this.dataFolder, var1 + ".yml");
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      ConfigurationSection var5 = var4.createSection("data");

      for (Entry var7 : var2.entrySet()) {
         var5.set((String)var7.getKey(), var7.getValue());
      }

      try {
         var4.save(var3);
      } catch (IOException var8) {
         this.plugin.getLogger().severe("Failed to save player data for " + var1 + ": " + var8.getMessage());
         var8.printStackTrace();
      }
   }

   @Override
   public void saveAllPlayerDataAsync() {
      (new BukkitRunnable() {
         public void run() {
            LocalDataProvider.this.saveAllPlayerDataSync();
         }
      }).runTaskAsynchronously(this.plugin);
   }

   @Override
   public void saveAllPlayerDataSync() {
      this.playerDataCache.forEach(this::savePlayerDataToFile);
   }
}
