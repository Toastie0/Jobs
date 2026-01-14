package net.advancedplugins.simplespigot.storage;

import com.google.common.collect.Maps;
import java.util.Map;

public class StorageSettings {
   private String address;
   private String prefix;
   private String database;
   private String username;
   private String password;
   private int maximumPoolSize;
   private int minimumIdle;
   private int maximumLifetime;
   private int connectionTimeout;
   private Map<String, String> properties = Maps.newHashMap();

   public StorageSettings() {
      this.address = "";
      this.prefix = "";
      this.database = "";
      this.username = "";
      this.password = "";
      this.maximumPoolSize = 0;
      this.minimumIdle = 0;
      this.maximumLifetime = 0;
      this.connectionTimeout = 0;
   }

   public String getHost() {
      return this.address.split(":")[0];
   }

   public String getPort() {
      String[] var1 = this.address.split(":");
      return var1.length > 1 ? var1[1] : "3306";
   }

   public void setAddress(String var1) {
      this.address = var1;
   }

   public void setPrefix(String var1) {
      this.prefix = var1;
   }

   public void setDatabase(String var1) {
      this.database = var1;
   }

   public void setUsername(String var1) {
      this.username = var1;
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   public void setMaximumPoolSize(int var1) {
      this.maximumPoolSize = var1;
   }

   public void setMinimumIdle(int var1) {
      this.minimumIdle = var1;
   }

   public void setMaximumLifetime(int var1) {
      this.maximumLifetime = var1;
   }

   public void setConnectionTimeout(int var1) {
      this.connectionTimeout = var1;
   }

   public void setProperties(Map<String, String> var1) {
      this.properties = var1;
   }

   public String getAddress() {
      return this.address;
   }

   public String getPrefix() {
      return this.prefix;
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

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   public int getMinimumIdle() {
      return this.minimumIdle;
   }

   public int getMaximumLifetime() {
      return this.maximumLifetime;
   }

   public int getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }
}
