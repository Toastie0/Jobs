package net.advancedplugins.jobs.impl.utils.universalDataStorage.providers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.universalDataStorage.PlayerDataProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MysqlDataProvider implements PlayerDataProvider {
   private final Plugin plugin;
   private final String host;
   private final String database;
   private final String username;
   private final String password;
   private final String prefix;
   private volatile Connection connection;
   private final Map<UUID, Map<String, Object>> playerDataCache = new HashMap<>();
   private final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);
   private final long SHUTDOWN_TIMEOUT_SECONDS = 10L;

   public MysqlDataProvider(Plugin var1, Map<String, Object> var2) {
      this.plugin = var1;
      this.host = (String)var2.get("host");
      this.database = (String)var2.get("database");
      this.username = (String)var2.get("username");
      this.password = (String)var2.get("password");
      this.prefix = var2.getOrDefault("prefix", "");
      this.connectAsync();
   }

   private void connectAsync() {
      ASManager.debug("Connecting to MySQL database asynchronously...");
      this.databaseExecutor.execute(() -> {
         try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String var1 = "jdbc:mysql://" + this.host + "/" + this.database + "?autoReconnect=true&useSSL=false";
            this.connection = DriverManager.getConnection(var1, this.username, this.password);
            ASManager.debug("Connected to MySQL database.");
            this.createTableAsync();
         } catch (SQLException | ClassNotFoundException var2) {
            ASManager.debug("Failed to connect to MySQL: " + var2.getMessage());
            var2.printStackTrace();
            this.connection = null;
         }
      });
   }

   private void createTableAsync() {
      if (this.connection == null) {
         ASManager.debug("Cannot create table: No database connection.");
      } else {
         this.databaseExecutor.execute(() -> {
            try (Statement var1 = this.connection.createStatement()) {
               String var2 = this.prefix + "player_data";
               String var3 = "CREATE TABLE IF NOT EXISTS " + var2 + " (player_id VARCHAR(36) NOT NULL PRIMARY KEY,data TEXT)";
               var1.executeUpdate(var3);
               ASManager.debug("Created or checked " + var2 + " table.");
            } catch (SQLException var6) {
               ASManager.debug("Failed to create " + this.prefix + "player_data table: " + var6.getMessage());
               var6.printStackTrace();
            }
         });
      }
   }

   @Override
   public <T> T getData(UUID var1, String var2, Class<T> var3) {
      Map var4 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromDatabase);
      if (var4 == null) {
         return null;
      } else if (var4.containsKey(var2)) {
         Object var5 = var4.get(var2);
         if (var3.isInstance(var5)) {
            return (T)var3.cast(var5);
         } else {
            ASManager.debug("Data type mismatch for player " + var1 + ", key " + var2 + ". Expected " + var3.getName() + ", got " + var5.getClass().getName());
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void saveData(UUID var1, String var2, Object var3) {
      Map var4 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromDatabase);
      var4.put(var2, var3);
   }

   public Map<String, Object> loadPlayerDataFromDatabase(UUID var1) {
      if (this.connection == null) {
         return null;
      } else {
         String var2 = this.prefix + "player_data";

         try {
            label80: {
               HashMap var6;
               try (PreparedStatement var3 = this.connection.prepareStatement("SELECT data FROM " + var2 + " WHERE player_id = ?")) {
                  var3.setString(1, var1.toString());
                  ResultSet var4 = var3.executeQuery();
                  if (!var4.next()) {
                     break label80;
                  }

                  String var5 = var4.getString("data");
                  if (var5 != null && !var5.isEmpty()) {
                     Map var11 = this.deserializeData(var5);
                     this.playerDataCache.put(var1, var11);
                     return var11;
                  }

                  var6 = new HashMap();
               }

               return var6;
            }
         } catch (SQLException var10) {
            ASManager.debug("Failed to get data for player " + var1 + ": " + var10.getMessage());
            var10.printStackTrace();
            return null;
         }

         this.playerDataCache.put(var1, new HashMap<>());
         return new HashMap<>();
      }
   }

   @Override
   public void removeData(UUID var1, String var2) {
      Map var3 = this.playerDataCache.computeIfAbsent(var1, this::loadPlayerDataFromDatabase);
      if (var3 != null) {
         var3.remove(var2);
      }
   }

   @Override
   public void clearPlayerData(UUID var1) {
      this.playerDataCache.remove(var1);
      if (this.connection != null) {
         String var2 = this.prefix + "player_data";

         try (PreparedStatement var3 = this.connection.prepareStatement("DELETE FROM " + var2 + " WHERE player_id = ?")) {
            var3.setString(1, var1.toString());
            var3.executeUpdate();
         } catch (SQLException var8) {
            ASManager.debug("Failed to clear player data for player " + var1 + ": " + var8.getMessage());
            var8.printStackTrace();
         }
      }
   }

   @Override
   public void saveAllPlayerDataAsync() {
      (new BukkitRunnable() {
         public void run() {
            MysqlDataProvider.this.saveAllPlayerDataSync();
         }
      }).runTaskAsynchronously(this.plugin);
   }

   @Override
   public void saveAllPlayerDataSync() {
      if (this.connection != null) {
         CountDownLatch var1 = new CountDownLatch(this.playerDataCache.size());
         this.playerDataCache.forEach((var2, var3x) -> this.savePlayerDataToDatabase(var2, (Map<String, Object>)var3x, var1));

         try {
            var1.await(10L, TimeUnit.SECONDS);
            ASManager.debug("All player data saved (or timeout reached).");
         } catch (InterruptedException var3) {
            ASManager.debug("Interrupted while waiting for data to save.");
            Thread.currentThread().interrupt();
         }
      }
   }

   @Override
   public boolean isPlayerDataLoaded(UUID var1) {
      return this.playerDataCache.containsKey(var1);
   }

   public void savePlayerDataToDatabase(UUID var1, Map<String, Object> var2, CountDownLatch var3) {
      if (this.connection == null) {
         if (var3 != null) {
            var3.countDown();
         }
      } else if (var2 != null && !var2.isEmpty()) {
         this.databaseExecutor.execute(() -> {
            try {
               String var4 = this.serializeData(var2);
               String var5 = "INSERT INTO " + this.prefix + "player_data (player_id, data) VALUES (?, ?)";

               try (PreparedStatement var6 = this.connection.prepareStatement(var5)) {
                  var6.setString(1, var1.toString());
                  var6.setString(2, var4);
                  var6.executeUpdate();
                  ASManager.debug("Inserted new player data for player " + var1);
               } catch (SQLException var25) {
                  if (var25.getSQLState().equals("23000") && var25.getErrorCode() == 1062) {
                     String var7 = "UPDATE " + this.prefix + "player_data SET data = ? WHERE player_id = ?";

                     try (PreparedStatement var8 = this.connection.prepareStatement(var7)) {
                        var8.setString(1, var4);
                        var8.setString(2, var1.toString());
                        int var9 = var8.executeUpdate();
                        if (var9 > 0) {
                           ASManager.debug("Updated player data for player " + var1);
                        } else {
                           ASManager.debug("No player data found to update for player " + var1 + ". This should not happen.");
                        }
                     } catch (SQLException var23) {
                        ASManager.debug("Failed to update data for player " + var1 + ": " + var23.getMessage());
                        var23.printStackTrace();
                     }
                  } else {
                     ASManager.debug("Failed to insert data for player " + var1 + ": " + var25.getMessage());
                     var25.printStackTrace();
                  }
               }
            } catch (Exception var26) {
               ASManager.debug("General error during savePlayerDataToDatabase for player " + var1 + ": " + var26.getMessage());
               var26.printStackTrace();
            } finally {
               if (var3 != null) {
                  var3.countDown();
               }
            }
         });
      } else {
         if (var3 != null) {
            var3.countDown();
         }
      }
   }

   @Override
   public CompletableFuture<Map<String, Object>> getPlayerDataAsync(final UUID var1) {
      final CompletableFuture var2 = new CompletableFuture();
      (new BukkitRunnable() {
         public void run() {
            final Map var1x = MysqlDataProvider.this.loadPlayerDataFromDatabase(var1);
            (new BukkitRunnable() {
               public void run() {
                  var2.complete(var1x);
               }
            }).runTask(MysqlDataProvider.this.plugin);
         }
      }).runTaskAsynchronously(this.plugin);
      return var2;
   }

   private boolean playerExists(UUID var1) {
      if (this.connection == null) {
         return false;
      } else {
         String var2 = this.prefix + "player_data";

         try {
            boolean var5;
            try (PreparedStatement var3 = this.connection.prepareStatement("SELECT 1 FROM " + var2 + " WHERE player_id = ?")) {
               var3.setString(1, var1.toString());
               ResultSet var4 = var3.executeQuery();
               var5 = var4.next();
            }

            return var5;
         } catch (SQLException var8) {
            ASManager.debug("Failed to check if player exists: " + var8.getMessage());
            var8.printStackTrace();
            return false;
         }
      }
   }

   @Override
   public void shutdown() {
      this.saveAllPlayerDataSync();
      if (this.connection != null) {
         try {
            this.connection.close();
         } catch (SQLException var3) {
            var3.printStackTrace();
         }
      }

      this.databaseExecutor.shutdown();

      try {
         if (!this.databaseExecutor.awaitTermination(10L, TimeUnit.SECONDS)) {
            this.databaseExecutor.shutdownNow();
         }
      } catch (InterruptedException var2) {
         this.databaseExecutor.shutdownNow();
         Thread.currentThread().interrupt();
      }
   }

   private String serializeData(Map<String, Object> var1) {
      Gson var2 = new Gson();
      return var2.toJson(var1);
   }

   private Map<String, Object> deserializeData(String var1) {
      Gson var2 = new Gson();
      Type var3 = (new TypeToken<Map<String, Object>>() {}).getType();
      return (Map<String, Object>)var2.fromJson(var1, var3);
   }

   public Plugin getPlugin() {
      return this.plugin;
   }

   public String getHost() {
      return this.host;
   }

   public String getDatabase() {
      return this.database;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public Connection getConnection() {
      return this.connection;
   }

   public Map<UUID, Map<String, Object>> getPlayerDataCache() {
      return this.playerDataCache;
   }

   public ExecutorService getDatabaseExecutor() {
      return this.databaseExecutor;
   }

   public long getSHUTDOWN_TIMEOUT_SECONDS() {
      return 10L;
   }
}
