package net.advancedplugins.jobs.impl.utils.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.logger.LogBackendType;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableInfo;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.advancedplugins.jobs.impl.utils.ReflectionUtil;
import net.advancedplugins.jobs.impl.utils.data.connection.ConnectionType;
import net.advancedplugins.jobs.impl.utils.data.connection.IConnectionHandler;
import net.advancedplugins.jobs.impl.utils.data.connection.MySQLConnectionHandler;
import net.advancedplugins.jobs.impl.utils.data.connection.PostgreSQLConnectionHandler;
import net.advancedplugins.jobs.impl.utils.data.connection.SQLiteConnectionHandler;
import net.advancedplugins.jobs.impl.utils.data.persister.base.ItemStackPersister;
import net.advancedplugins.jobs.impl.utils.data.persister.base.ListPersister;
import net.advancedplugins.jobs.impl.utils.data.persister.base.LocationPersister;
import net.advancedplugins.jobs.impl.utils.data.persister.base.MapPersister;
import net.advancedplugins.jobs.impl.utils.data.persister.base.OfflinePlayerPersister;
import net.advancedplugins.jobs.impl.utils.data.persister.base.WorldPersister;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.postgresql.util.PSQLException;

public class DatabaseController {
   private final JavaPlugin plugin;
   private final File jarFile;
   private final ConfigurationSection options;
   private ConnectionType connectionType;
   private IConnectionHandler handler;
   private BaseConnectionSource source;
   private ExecutorService executor;
   private boolean debug;
   private boolean useHikari;
   private Map<Class<?>, Dao<?, ?>> daoMap;

   public DatabaseController(JavaPlugin var1, File var2, ConnectionType var3, ConfigurationSection var4, boolean var5) {
      this.plugin = var1;
      this.jarFile = var2;
      this.connectionType = var3;
      this.options = var4;
      this.useHikari = var5;
      this.debug = false;
      this.daoMap = new HashMap<>();
   }

   public void connect() {
      int var1 = this.options.getInt("threads", 10);
      switch (this.connectionType) {
         case MYSQL:
            this.handler = new MySQLConnectionHandler();
            break;
         case POSTGRESQL:
            this.handler = new PostgreSQLConnectionHandler();
            break;
         case SQLITE:
            this.handler = new SQLiteConnectionHandler(this.plugin.getDataFolder());
            var1 = 1;
            break;
         default:
            throw new UnsupportedOperationException("Unsupported connection type: " + this.connectionType.name());
      }

      this.executor = new ThreadPoolExecutor(var1, var1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());
      this.handler.retrieveCredentials(this.options);
      this.source = TryCatchUtil.tryAndReturn(() -> this.useHikari ? this.handler.connectHikari() : this.handler.connect());
      this.registerDefaultPersisters();
      if (!this.debug) {
         LoggerFactory.setLogBackendFactory(LogBackendType.NULL);
      }
   }

   public void close() {
      try {
         if (this.executor != null) {
            this.executor.shutdownNow();
         }

         if (this.source != null) {
            this.source.close();
            this.handler.close();
            this.source = null;
            this.daoMap = new HashMap<>();
         }
      } catch (Throwable var2) {
         throw var2;
      }
   }

   public void registerEntities(String var1) {
      if (this.source != null) {
         for (Class var3 : ReflectionUtil.getAllClassesInPackage(this.jarFile, var1)) {
            this.registerEntity(var3);
         }
      }
   }

   public void registerEntity(Class<?> var1) {
      if (this.source != null) {
         if (var1.getDeclaredAnnotation(DatabaseTable.class) != null) {
            Field var2 = Arrays.stream(var1.getDeclaredFields()).filter(var0 -> {
               DatabaseField var1x = var0.getDeclaredAnnotation(DatabaseField.class);
               return var1x == null ? false : var1x.id() || var1x.generatedId() || !var1x.generatedIdSequence().isEmpty();
            }).findAny().orElse(null);
            if (var2 != null) {
               TryCatchUtil.tryRun(() -> {
                  TableInfo var2x = new TableInfo(this.source.getDatabaseType(), var1);
                  TransactionManager.callInTransaction(this.source, () -> {
                     if (this.connectionType == ConnectionType.MYSQL) {
                        DatabaseConnection var3x = this.source.getReadWriteConnection(var2x.getTableName());

                        try {
                           var3x.executeStatement("SET sql_mode = 'ANSI_QUOTES'", -1);
                        } finally {
                           this.source.releaseConnection(var3x);
                        }
                     }

                     TableUtils.createTableIfNotExists(this.source, var1);
                     if (this.connectionType == ConnectionType.MYSQL) {
                        DatabaseConnection var12 = this.source.getReadWriteConnection(var2x.getTableName());

                        try {
                           var12.executeStatement("SET sql_mode = ''", -1);
                        } finally {
                           this.source.releaseConnection(var12);
                        }
                     }

                     return null;
                  });
               }, var1x -> {
                  Throwable var2x = var1x.getCause();
                  if (!(var2x instanceof PSQLException) || !var2x.getMessage().contains("already exists")) {
                     this.plugin.getLogger().log(Level.SEVERE, "Something went wrong!", (Throwable)var1x);
                  }
               });
               Dao var3 = TryCatchUtil.tryAndReturn(() -> DaoManager.createDao(this.source, var1));
               TryCatchUtil.tryRun(() -> var3.setObjectCache(false));
               this.daoMap.put(var1, var3);
            }
         }
      }
   }

   public <T, Z> Dao<T, Z> getDao(Class<T> var1, Class<Z> var2) {
      if (this.source == null) {
         return null;
      } else {
         Dao var3 = this.daoMap.get(var1);
         return var3 == null ? null : var3;
      }
   }

   public void registerPersisters(String var1) {
      if (this.source != null) {
         ReflectionUtil.getAllClassesInPackage(this.jarFile, var1)
            .stream()
            .filter(DataPersister.class::isAssignableFrom)
            .map(var0 -> TryCatchUtil.tryAndReturn(() -> (DataPersister)var0.getDeclaredConstructor().newInstance()))
            .filter(Objects::nonNull)
            .forEach(var0 -> DataPersisterManager.registerDataPersisters(new DataPersister[]{var0}));
      }
   }

   public void registerPersisters(DataPersister... var1) {
      DataPersisterManager.registerDataPersisters(var1);
   }

   private void registerDefaultPersisters() {
      this.registerPersisters(
         new ItemStackPersister(), new ListPersister(), new LocationPersister(), new MapPersister(), new OfflinePlayerPersister(), new WorldPersister()
      );
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   public File getJarFile() {
      return this.jarFile;
   }

   public ConfigurationSection getOptions() {
      return this.options;
   }

   public ConnectionType getConnectionType() {
      return this.connectionType;
   }

   public IConnectionHandler getHandler() {
      return this.handler;
   }

   public BaseConnectionSource getSource() {
      return this.source;
   }

   public ExecutorService getExecutor() {
      return this.executor;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public boolean isUseHikari() {
      return this.useHikari;
   }

   public Map<Class<?>, Dao<?, ?>> getDaoMap() {
      return this.daoMap;
   }

   public void setConnectionType(ConnectionType var1) {
      this.connectionType = var1;
   }

   public void setHandler(IConnectionHandler var1) {
      this.handler = var1;
   }

   public void setSource(BaseConnectionSource var1) {
      this.source = var1;
   }

   public void setExecutor(ExecutorService var1) {
      this.executor = var1;
   }

   public void setDebug(boolean var1) {
      this.debug = var1;
   }

   public void setUseHikari(boolean var1) {
      this.useHikari = var1;
   }

   public void setDaoMap(Map<Class<?>, Dao<?, ?>> var1) {
      this.daoMap = var1;
   }
}
