package net.advancedplugins.jobs.impl.utils.data.cache;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.misc.TransactionManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.advancedplugins.jobs.impl.utils.data.DatabaseController;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ICached;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IForeignMapping;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IForeignMappingHandler;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ISavableCache;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ISavableLifecycle;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class DataCache<K, V extends ICached<K>> implements ISavableCache<K, V>, IForeignMappingHandler {
   protected final JavaPlugin plugin;
   protected final Dao<V, K> dao;
   protected final ConcurrentMap<K, V> cache = new ConcurrentHashMap<>();

   public DataCache(DatabaseController var1, JavaPlugin var2) {
      this.plugin = var2;
      ParameterizedType var3 = (ParameterizedType)this.getClass().getGenericSuperclass();
      Type[] var4 = var3.getActualTypeArguments();
      Class var5 = (Class)var4[0];
      Class var6 = (Class)var4[1];
      this.dao = var1.getDao(var6, var5);
   }

   public DataCache(DatabaseController var1, JavaPlugin var2, Class<K> var3, Class<V> var4) {
      this.dao = var1.getDao(var4, var3);
      this.plugin = var2;
   }

   @Override
   public V get(K var1) {
      return this.contains((K)var1) ? this.cache.get(var1) : this.load((K)var1);
   }

   @Override
   public V getOrNull(K var1) {
      return this.cache.get(var1);
   }

   @Override
   public Set<K> keySet() {
      return this.cache.keySet();
   }

   @Override
   public Collection<V> values() {
      return this.cache.values();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.cache.entrySet();
   }

   @Override
   public void set(K var1, V var2) {
      this.cache.put((K)var1, (V)var2);
   }

   @Override
   public void invalidate(K var1) {
      this.cache.remove(var1);
   }

   @Override
   public void invalidateAll() {
      this.cache.clear();
   }

   @Override
   public boolean contains(K var1) {
      return this.cache.containsKey(var1);
   }

   @Override
   public void remove(K var1) {
      TryCatchUtil.tryRun(() -> {
         this.dao.deleteById(var1);
         this.invalidate((K)var1);
      });
   }

   @Override
   public void removeIf(BiPredicate<K, V> var1) {
      this.cache.forEach((var2, var3) -> {
         if (var1.test(var2, var3)) {
            this.remove((K)var2);
         }
      });
   }

   @Override
   public void removeAll() {
      this.invalidateAll();
      TryCatchUtil.tryRun(() -> this.dao.deleteBuilder().delete());
   }

   @Override
   public V load(K var1) {
      ICached var2 = TryCatchUtil.tryAndReturn(() -> (ICached)this.dao.queryForId(var1));
      if (var2 == null) {
         return null;
      } else {
         this.loadValueIntoCache((K)var1, (V)var2);
         return (V)var2;
      }
   }

   @Override
   public Collection<V> loadAll() {
      return this.loadAll(false);
   }

   @Override
   public Collection<V> loadAll(boolean var1) {
      TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList<>())
         .stream()
         .filter(var2 -> !var1 || !this.contains((K)var2.getKey()))
         .forEach(var1x -> this.loadValueIntoCache((K)var1x.getKey(), (V)var1x));
      return this.cache.values();
   }

   protected void loadValue(K var1, V var2) {
      if (var2 instanceof IForeignMapping var3) {
         this.dbToJava(var3);
      }

      if (var2 instanceof ISavableLifecycle var4) {
         var4.afterLoad();
      }
   }

   protected void loadValueIntoCache(K var1, V var2) {
      this.loadValue((K)var1, (V)var2);
      this.cache.put((K)var1, (V)var2);
   }

   @Override
   public void modify(K var1, Consumer<V> var2) {
      boolean var3 = this.contains((K)var1);
      if (this.exists(var1) || var3) {
         var2.accept(this.get((K)var1));
         if (!var3) {
            this.save((K)var1);
            this.invalidate((K)var1);
         }
      }
   }

   @Override
   public void modifyMultiple(Set<K> var1, Consumer<V> var2) {
      this.cache.entrySet().stream().filter(var1x -> var1.contains(var1x.getKey())).forEach(var1x -> var2.accept(var1x.getValue()));
      HashSet var3 = new HashSet<>(this.keySet());
      this.loadAll(true);
      this.entrySet().stream().filter(var2x -> !var3.contains(var2x.getKey()) && var1.contains(var2x.getKey())).forEach(var2x -> {
         var2.accept(var2x.getValue());
         this.save(var2x.getKey());
         this.invalidate(var2x.getKey());
      });
   }

   @Override
   public void modifyAll(Consumer<V> var1) {
      this.cache.values().forEach(var1);
      HashSet var2 = new HashSet<>(this.keySet());
      this.loadAll(true);
      this.entrySet().stream().filter(var1x -> !var2.contains(var1x.getKey())).forEach(var2x -> {
         var1.accept(var2x.getValue());
         this.save(var2x.getKey());
         this.invalidate(var2x.getKey());
      });
   }

   @Override
   public void loopAll(Consumer<V> var1) {
      this.loopAll().forEach(var1);
   }

   @Override
   public Collection<V> loopAll() {
      HashSet var1 = new HashSet<>(this.keySet());
      this.loadAll(true);
      HashSet var2 = new HashSet<>(this.cache.values());
      this.keySet().stream().filter(var1x -> !var1.contains(var1x)).forEach(this::invalidate);
      return var2;
   }

   @Override
   public void save(K var1) {
      if (this.contains(var1)) {
         ICached var2 = this.get((K)var1);
         this.saveValue((V)var2);
      }
   }

   @Override
   public void saveValue(V var1) {
      this.saveToDb((V)var1);
   }

   protected void createInDb(V var1) {
      if (var1 instanceof ISavableLifecycle var2) {
         var2.beforeSave();
      }

      if (var1 instanceof IForeignMapping var3) {
         this.javaToDb(var3);
      }

      TryCatchUtil.tryRun(() -> this.dao.create(var1));
   }

   protected void saveToDb(V var1) {
      if (var1 instanceof ISavableLifecycle var2) {
         var2.beforeSave();
      }

      if (var1 instanceof IForeignMapping var3) {
         this.javaToDb(var3);
      }

      TryCatchUtil.tryRun(() -> this.dao.update(var1));
   }

   @Override
   public void saveAll() {
      this.cache.values().forEach(this::saveToDb);
   }

   @Override
   public V create(K var1, V var2) {
      this.set((K)var1, (V)var2);
      this.createInDb((V)var2);
      return this.load((K)var1);
   }

   @Override
   public V create(V var1) {
      this.createInDb((V)var1);
      return this.load((K)var1.getKey());
   }

   @Override
   public boolean exists(K var1) {
      return this.contains((K)var1) ? true : TryCatchUtil.tryOrDefault(() -> this.dao.idExists(var1), false);
   }

   protected void runInTransaction(Runnable var1) {
      TryCatchUtil.tryRun(() -> TransactionManager.callInTransaction(this.dao.getConnectionSource(), () -> {
         var1.run();
         return null;
      }));
   }

   @Override
   public void dbToJava(IForeignMapping var1) {
      var1.getForeignMapping().forEach(this::addAllForeignToCollection);
      var1.getForeignMappers().forEach(this::addAllMapper);
   }

   @Override
   public void javaToDb(IForeignMapping var1) {
      var1.getForeignMapping().forEach((var1x, var2) -> {
         var1x.removeIf(var1xx -> !var2.contains(var1xx));
         var2.forEach(var2x -> {
            if (var2x.getKey() != null) {
               this.updateForeign(var1x, var2x);
            } else {
               this.addToForeign(var1x, var2x);
            }
         });
      });
      var1.getForeignMappers().forEach(var1x -> {
         var1x.foreign().removeIf(var2 -> !var1x.cache().containsKey(this.extractKeyFromMapper((ForeignMapper<?>)var1x, var2)));
         var1x.cache().forEach((var2, var3) -> {
            if (var3.getKey() != null) {
               this.updateForeign(var1x.foreign(), var3);
            } else {
               this.addToForeign(var1x.foreign(), var3);
            }
         });
      });
   }

   @Override
   public void refreshForeign(ForeignCollection<? extends ICached<Integer>> var1, Collection<? extends ICached<Integer>> var2) {
      this.addAllForeignToCollection(var1, var2);
   }

   @Override
   public void refreshForeign(ForeignMapper<?> var1) {
      this.addAllMapper(var1);
   }

   private <T> void addAllForeignToCollection(ForeignCollection<?> var1, Collection<T> var2) {
      var2.clear();
      var1.forEach(var1x -> var2.add(var1x));
   }

   private <T extends ICached<Integer>> void addAllMapper(ForeignMapper<T> var1) {
      var1.cache().clear();
      var1.foreign().forEach(var1x -> var1.cache().put((K)((String)var1.keyExtractor().apply(var1x)), (V)var1x));
   }

   protected <T extends ICached<Integer>> String extractKeyFromMapper(ForeignMapper<T> var1, Object var2) {
      return (String)var1.keyExtractor().apply((ICached)var2);
   }

   protected <T> void updateForeign(ForeignCollection<T> var1, Object var2) {
      TryCatchUtil.tryRun(() -> var1.update(var2));
   }

   protected <T> void addToForeign(ForeignCollection<T> var1, Object var2) {
      TryCatchUtil.tryRun(() -> var1.add(var2));
   }

   public Dao<V, K> getDao() {
      return this.dao;
   }
}
