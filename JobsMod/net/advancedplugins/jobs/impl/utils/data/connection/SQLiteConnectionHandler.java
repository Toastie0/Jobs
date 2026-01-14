package net.advancedplugins.jobs.impl.utils.data.connection;

import com.j256.ormlite.jdbc.db.SqliteDatabaseType;
import com.j256.ormlite.support.BaseConnectionSource;
import java.io.File;
import org.bukkit.configuration.ConfigurationSection;

public class SQLiteConnectionHandler implements IConnectionHandler {
   private final String fileName = "database.db";
   private final File file;

   public SQLiteConnectionHandler(File var1) {
      this.file = new File(var1, "database.db");
   }

   @Override
   public ConnectionType getConnectionType() {
      return ConnectionType.SQLITE;
   }

   @Override
   public void retrieveCredentials(ConfigurationSection var1) {
   }

   @Override
   public BaseConnectionSource connect() {
      this.file.getParentFile().mkdirs();
      if (!this.file.exists()) {
         this.file.createNewFile();
      }

      String var1 = "jdbc:sqlite:" + this.file.getAbsolutePath();
      return new SqliteConnectionSource(var1, new SqliteDatabaseType());
   }

   @Override
   public BaseConnectionSource connectHikari() {
      return this.connect();
   }

   @Override
   public void close() {
   }
}
