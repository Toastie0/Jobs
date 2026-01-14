package net.advancedplugins.jobs.impl.utils.data.connection;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.DatabaseConnection;

public class SqliteConnectionSource extends JdbcConnectionSource {
   public SqliteConnectionSource() {
   }

   public SqliteConnectionSource(String var1) {
      super(var1);
   }

   public SqliteConnectionSource(String var1, DatabaseType var2) {
      super(var1, var2);
   }

   public SqliteConnectionSource(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public SqliteConnectionSource(String var1, String var2, String var3, DatabaseType var4) {
      super(var1, var2, var3, var4);
   }

   protected DatabaseConnection makeConnection(Logger var1) {
      DatabaseConnection var2 = super.makeConnection(var1);
      var2.executeStatement("PRAGMA foreign_keys = ON", -1);
      return var2;
   }
}
