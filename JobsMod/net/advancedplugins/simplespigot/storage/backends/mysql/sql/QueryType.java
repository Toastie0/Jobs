package net.advancedplugins.simplespigot.storage.backends.mysql.sql;

public enum QueryType {
   CREATE_TABLE("CREATE TABLE IF NOT EXISTS "),
   INSERT("INSERT INTO "),
   DELETE("DELETE FROM "),
   SELECT("SELECT ");

   private final String start;

   private QueryType(String nullxx) {
      this.start = nullxx;
   }

   public String query(String var1) {
      return this.start.concat(var1);
   }
}
