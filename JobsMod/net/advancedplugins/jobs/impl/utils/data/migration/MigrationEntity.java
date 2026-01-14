package net.advancedplugins.jobs.impl.utils.data.migration;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(
   tableName = "advancedplugins_database_info"
)
public class MigrationEntity {
   @DatabaseField(
      id = true
   )
   String pluginName;
   @DatabaseField
   int version;

   public MigrationEntity(String var1, int var2) {
      this.pluginName = var1;
      this.version = var2;
   }

   public MigrationEntity() {
   }

   public String getPluginName() {
      return this.pluginName;
   }

   public int getVersion() {
      return this.version;
   }

   public void setPluginName(String var1) {
      this.pluginName = var1;
   }

   public void setVersion(int var1) {
      this.version = var1;
   }
}
