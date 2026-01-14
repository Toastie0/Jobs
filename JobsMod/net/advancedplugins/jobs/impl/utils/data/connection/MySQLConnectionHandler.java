package net.advancedplugins.jobs.impl.utils.data.connection;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import com.j256.ormlite.support.BaseConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.configuration.ConfigurationSection;

public class MySQLConnectionHandler implements IConnectionHandler {
   private String host;
   private int port;
   private String username;
   private String password;
   private String database;
   private HikariDataSource dataSource;

   @Override
   public ConnectionType getConnectionType() {
      return ConnectionType.MYSQL;
   }

   @Override
   public void retrieveCredentials(ConfigurationSection var1) {
      if (var1.contains("host")) {
         this.host = var1.getString("host");
         this.port = var1.getInt("port", 3306);
      } else {
         String[] var2 = var1.getString("address", "").split(":", 2);
         this.host = var2[0];
         if (var2.length == 2) {
            this.port = Integer.parseInt(var2[1]);
         } else {
            this.port = 3306;
         }
      }

      this.username = var1.getString("username");
      this.password = var1.getString("password");
      this.database = var1.getString("database");
   }

   @Override
   public BaseConnectionSource connect() {
      String var1 = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true";
      return new JdbcPooledConnectionSource(var1, this.username, this.password, new MysqlDatabaseType());
   }

   @Override
   public BaseConnectionSource connectHikari() {
      HikariConfig var1 = new HikariConfig();
      var1.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
      var1.setUsername(this.username);
      var1.setPassword(this.password);
      TryCatchUtil.tryAndReturn(() -> Class.forName("com.mysql.cj.jdbc.Driver"));
      this.dataSource = new HikariDataSource(HikariHandler.configure(var1));
      return new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());
   }

   @Override
   public void close() {
      if (this.dataSource != null) {
         this.dataSource.close();
      }
   }
}
