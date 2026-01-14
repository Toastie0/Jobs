package net.advancedplugins.jobs.impl.utils.data.migration;

import com.j256.ormlite.dao.Dao;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.advancedplugins.jobs.impl.utils.data.DatabaseController;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class MigrationController {
   private final JavaPlugin plugin;
   private final DatabaseController databaseController;
   private final String tag;
   private final int currentVersion;
   private final Map<Integer, BiConsumer<JavaPlugin, DatabaseController>> migrations;
   private final Dao<MigrationEntity, String> migrationDao;

   public MigrationController(JavaPlugin var1, DatabaseController var2, String var3, int var4) {
      this.plugin = var1;
      this.databaseController = var2;
      this.tag = var3;
      this.currentVersion = var4;
      this.migrations = new HashMap<>();
      this.databaseController.registerEntity(MigrationEntity.class);
      this.migrationDao = this.databaseController.getDao(MigrationEntity.class, String.class);
      this.checkAndCreateInfo();
   }

   public MigrationController registerMigration(int var1, BiConsumer<JavaPlugin, DatabaseController> var2) {
      if (var1 > this.currentVersion) {
         throw new IllegalArgumentException("Migration version out of range");
      } else {
         this.migrations.put(var1, var2);
         return this;
      }
   }

   public void migrate() {
      this.checkAndCreateInfo();
      MigrationEntity var1 = TryCatchUtil.tryAndReturn(() -> (MigrationEntity)this.migrationDao.queryForId(this.tag));
      int var2 = var1.getVersion();

      for (int var3 = var2 + 1; var3 <= this.currentVersion; var3++) {
         int var4 = var3;
         if (this.migrations.containsKey(var4)) {
            TryCatchUtil.tryRun(() -> this.migrations.get(var4).accept(this.plugin, this.databaseController));
         }
      }

      var1.setVersion(this.currentVersion);
      TryCatchUtil.tryRun(() -> this.migrationDao.update(var1));
   }

   private void checkAndCreateInfo() {
      if (!TryCatchUtil.tryAndReturn(() -> this.migrationDao.idExists(this.tag))) {
         TryCatchUtil.tryRun(() -> this.migrationDao.create(new MigrationEntity(this.tag, this.currentVersion)));
      }
   }
}
