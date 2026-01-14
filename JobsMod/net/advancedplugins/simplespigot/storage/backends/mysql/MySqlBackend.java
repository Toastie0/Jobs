package net.advancedplugins.simplespigot.storage.backends.mysql;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.advancedplugins.simplespigot.plugin.SimplePlugin;
import net.advancedplugins.simplespigot.storage.Backend;
import net.advancedplugins.simplespigot.storage.StorageSettings;

public class MySqlBackend implements Backend {
   private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%where%` ( id VARCHAR(36) NOT NULL, json MEDIUMBLOB NOT NULL, PRIMARY KEY (id) )";
   private static final String DELETE = "DELETE FROM `%where%` WHERE id=?";
   private static final String INSERT = "INSERT INTO `%where%` (id, json) VALUES(?, ?)";
   private static final String SELECT = "SELECT id, json FROM `%where%` WHERE id=?";
   private static final String SELECT_ALL = "SELECT * FROM `%where%`";
   private final StorageSettings storageSettings;
   private final MySqlConnectionFactory connectionFactory;
   private final UnaryOperator<String> processor;

   public MySqlBackend(SimplePlugin var1, String var2) {
      this.storageSettings = var1.getStorageSettings();
      this.connectionFactory = new MySqlConnectionFactory(this.storageSettings);
      this.processor = var2x -> var2x.replace("%where%", this.storageSettings.getPrefix().concat(var2));
      this.createTable();
   }

   @Override
   public JsonObject load(String var1) {
      try {
         try (
            Connection var2 = this.connectionFactory.getConnection();
            PreparedStatement var3 = var2.prepareStatement(this.processor.apply("SELECT id, json FROM `%where%` WHERE id=?"));
         ) {
            var3.setString(1, var1);

            try (ResultSet var4 = var3.executeQuery()) {
               if (var4.next()) {
                  return new JsonParser().parse(var4.getString("json")).getAsJsonObject();
               }
            }
         }

         return null;
      } catch (Throwable var13) {
         throw var13;
      }
   }

   @Override
   public void save(String var1, JsonObject var2) {
      try {
         try (
            Connection var3 = this.connectionFactory.getConnection();
            PreparedStatement var4 = var3.prepareStatement(this.processor.apply("DELETE FROM `%where%` WHERE id=?"));
         ) {
            var4.setString(1, var1);
            var4.execute();
         }

         try (
            Connection var16 = this.connectionFactory.getConnection();
            PreparedStatement var17 = var16.prepareStatement(this.processor.apply("INSERT INTO `%where%` (id, json) VALUES(?, ?)"));
         ) {
            var17.setString(1, var1);
            var17.setString(2, new Gson().toJson(var2));
            var17.execute();
         }
      } catch (Throwable var15) {
         throw var15;
      }
   }

   @Override
   public Set<JsonObject> loadAll() {
      try {
         HashSet var1 = Sets.newHashSet();

         try (
            Connection var2 = this.connectionFactory.getConnection();
            PreparedStatement var3 = var2.prepareStatement(this.processor.apply("SELECT * FROM `%where%`"));
            ResultSet var4 = var3.executeQuery();
         ) {
            while (var4.next()) {
               var1.add(new JsonParser().parse(var4.getString("json")).getAsJsonObject());
            }
         }

         return var1;
      } catch (Throwable var13) {
         throw var13;
      }
   }

   @Override
   public void close() {
      this.connectionFactory.close();
   }

   private void createTable() {
      try {
         try (Connection var1 = this.connectionFactory.getConnection()) {
            var1.createStatement()
               .execute(this.processor.apply("CREATE TABLE IF NOT EXISTS `%where%` ( id VARCHAR(36) NOT NULL, json MEDIUMBLOB NOT NULL, PRIMARY KEY (id) )"));
         }
      } catch (Throwable var6) {
         throw var6;
      }
   }
}
