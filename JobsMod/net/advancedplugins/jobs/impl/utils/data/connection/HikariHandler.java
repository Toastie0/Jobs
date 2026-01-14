package net.advancedplugins.jobs.impl.utils.data.connection;

import com.zaxxer.hikari.HikariConfig;

public class HikariHandler {
   public static String POOL_NAME = "hikari";
   public static int MAX_POOL_SIZE = 10;
   public static int MIN_IDLE = 10;
   public static long MAX_LIFETIME = 1800000L;
   public static int TIMEOUT = 30000;
   public static boolean SSL = false;

   public static HikariConfig configure(HikariConfig var0) {
      var0.setPoolName(POOL_NAME);
      var0.setMaximumPoolSize(MAX_POOL_SIZE);
      var0.setMinimumIdle(MIN_IDLE);
      var0.setMaxLifetime(MAX_LIFETIME);
      var0.setConnectionTimeout(TIMEOUT);
      var0.addDataSourceProperty("characterEncoding", "utf8");
      var0.addDataSourceProperty("autoReconnect", "true");
      var0.addDataSourceProperty("useSSL", SSL);
      return var0;
   }
}
