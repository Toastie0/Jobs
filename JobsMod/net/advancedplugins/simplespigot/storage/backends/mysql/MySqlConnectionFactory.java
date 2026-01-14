package net.advancedplugins.simplespigot.storage.backends.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.function.Function;
import net.advancedplugins.simplespigot.storage.StorageSettings;
import org.bukkit.Bukkit;

public class MySqlConnectionFactory {
   private final StorageSettings storageSettings;
   private final HikariConfig config;
   private HikariDataSource dataSource;

   public MySqlConnectionFactory(StorageSettings var1) {
      this.storageSettings = var1;
      this.config = new HikariConfig();

      try {
         this.dataSource = new HikariDataSource(this.configure(this.config));
      } catch (Exception var3) {
         Bukkit.getLogger().severe("Could not setup mysql, please check your credentials.");
         var3.printStackTrace();
      }

      if (this.getConnection() != null) {
         Bukkit.getLogger().info("Successfully connected to MySQL.");
      }
   }

   public Connection getConnection() {
      try {
         if (this.dataSource == null) {
            throw new SQLException("Unable to get a connection from the pool.");
         } else {
            Connection var1 = this.dataSource.getConnection();
            if (var1 == null) {
               throw new SQLException("Unable to get a connection from the pool.");
            } else {
               return var1;
            }
         }
      } catch (Throwable var2) {
         throw var2;
      }
   }

   public void close() {
      if (this.dataSource != null) {
         this.dataSource.close();
      }
   }

   private HikariConfig configure(HikariConfig var1) {
      try {
         Class.forName("com.mysql.cj.jdbc.MysqlDataSource");
         var1.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
      } catch (Exception var4) {
         var1.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
      }

      this.config.setUsername(this.storageSettings.getUsername());
      this.config.setPassword(this.storageSettings.getPassword());
      this.config.setPoolName(this.storageSettings.getPrefix().concat("hikari"));
      this.config.setMaximumPoolSize(this.storageSettings.getMaximumPoolSize());
      this.config.setMinimumIdle(this.storageSettings.getMinimumIdle());
      this.config.setMaxLifetime(this.storageSettings.getMaximumLifetime());
      this.config.setConnectionTimeout(this.storageSettings.getConnectionTimeout());
      this.addProperty("characterEncoding", "utf8");
      this.addProperty("serverName", StorageSettings::getHost);
      this.addProperty("port", StorageSettings::getPort);
      this.addProperty("databaseName", StorageSettings::getDatabase);

      for (Entry var3 : this.storageSettings.getProperties().entrySet()) {
         this.addProperty((String)var3.getKey(), var3.getValue());
      }

      return var1;
   }

   private void addProperty(String var1, Function<StorageSettings, Object> var2) {
      this.config.addDataSourceProperty(var1, var2.apply(this.storageSettings));
   }

   private void addProperty(String var1, Object var2) {
      this.config.addDataSourceProperty(var1, var2);
   }
}
