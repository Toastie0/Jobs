package net.advancedplugins.jobs.impl.utils.data.cache;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.advancedplugins.jobs.impl.utils.data.DatabaseController;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IAsyncSavableCache;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ICached;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IForeignMapping;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ISavableLifecycle;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class AsyncDataCache<K, V extends ICached<K>> extends DataCache<K, V> implements IAsyncSavableCache<K, V> {
   private final AsyncDataCache.AsyncPriorityMap asyncPriorityMap;
   private ExecutorService executor;

   public AsyncDataCache(DatabaseController var1, JavaPlugin var2) {
      this(var1, var2, new AsyncDataCache.AsyncPriorityMap(0));
   }

   public AsyncDataCache(DatabaseController var1, JavaPlugin var2, AsyncDataCache.AsyncPriorityMap var3) {
      super(var1, var2);
      this.executor = var1.getExecutor();
      this.asyncPriorityMap = var3;
   }

   public AsyncDataCache(DatabaseController var1, Class<K> var2, Class<V> var3, JavaPlugin var4) {
      this(var1, var2, var3, var4, new AsyncDataCache.AsyncPriorityMap(0));
   }

   public AsyncDataCache(DatabaseController var1, Class<K> var2, Class<V> var3, JavaPlugin var4, AsyncDataCache.AsyncPriorityMap var5) {
      super(var1, var4, var2, var3);
      this.executor = var1.getExecutor();
      this.asyncPriorityMap = var5;
   }

   @Override
   public CompletableFuture<V> getAsync(K var1) {
      return this.supplyAsync(() -> this.get((K)var1), this.asyncPriorityMap.getPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<V> getAsyncOrNull(K var1) {
      return this.supplyAsync(() -> this.getOrNull((K)var1), this.asyncPriorityMap.getPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<V> loadAsync(K var1) {
      return this.supplyAsync(() -> this.load((K)var1), this.asyncPriorityMap.loadPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<Collection<V>> loadAsyncAll() {
      return this.loadAsyncAll(false);
   }

   @Override
   public CompletableFuture<Collection<V>> loadAsyncAll(boolean var1) {
      long var2 = System.currentTimeMillis();
      return this.<List>supplyAsync(() -> TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList()), this.asyncPriorityMap.loadAllPriority, var2)
         .thenCompose(
            var4 -> CompletableFuture.allOf(
               var4.stream()
                  .map(
                     var4x -> var1 && this.contains((K)var4x.getKey())
                        ? CompletableFuture.completedFuture(null)
                        : this.runAsync(() -> this.loadValueIntoCache((K)var4x.getKey(), (V)var4x), this.asyncPriorityMap.loadAllPriority, var2)
                  )
                  .toArray(CompletableFuture[]::new)
            )
         )
         .thenApply(var1x -> this.values());
   }

   @Override
   public CompletableFuture<Void> modifyAsync(K var1, Consumer<V> var2) {
      return this.runAsync(() -> this.modify((K)var1, var2), this.asyncPriorityMap.modifyPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<Void> modifyAsyncMultiple(Set<K> var1, Consumer<V> var2) {
      return this.loopAsyncAll(var3 -> {
         if (var1.contains(var3.getKey())) {
            var2.accept(var3);
            if (!this.contains(var3.getKey())) {
               this.saveValue(var3);
            }
         }
      });
   }

   @Override
   public CompletableFuture<Void> modifyAsyncAll(Consumer<V> var1) {
      return this.loopAsyncAll(var2 -> {
         var1.accept(var2);
         if (!this.contains(var2.getKey())) {
            this.saveValue(var2);
         }
      });
   }

   @Override
   public CompletableFuture<Void> loopAsyncAll(Consumer<V> var1) {
      long var2 = System.currentTimeMillis();
      return this.loopAsyncAll()
         .thenCompose(
            var4 -> CompletableFuture.allOf(
               var4.stream()
                  .map(var4x -> this.runAsync(() -> var1.accept(var4x), this.asyncPriorityMap.modifyAllPriority, var2))
                  .toArray(CompletableFuture[]::new)
            )
         );
   }

   @Override
   public CompletableFuture<Collection<V>> loopAsyncAll() {
      long var1 = System.currentTimeMillis();
      HashSet var3 = new HashSet<>(this.values());
      return this.<List>supplyAsync(() -> TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList()), this.asyncPriorityMap.modifyAllPriority, var1)
         .thenCompose(
            var4 -> CompletableFuture.allOf(
               var4.stream().map(var4x -> this.contains((K)var4x.getKey()) ? CompletableFuture.completedFuture(null) : this.runAsync(() -> {
                  this.loadValue((K)var4x.getKey(), (V)var4x);
                  var3.add(var4x);
               }, this.asyncPriorityMap.loadAllPriority, var1)).toArray(CompletableFuture[]::new)
            )
         )
         .thenApply(var1x -> var3);
   }

   @Override
   public CompletableFuture<Void> saveAsync(K var1) {
      return this.getAsyncOrNull((K)var1).thenCompose(var1x -> var1x == null ? CompletableFuture.completedFuture(null) : this.saveAsyncValue((V)var1x));
   }

   @Override
   public CompletableFuture<Void> saveAsyncValue(V var1) {
      long var2 = System.currentTimeMillis();
      return this.saveToDbAsync((V)var1, this.asyncPriorityMap.savePriority, var2);
   }

   @Override
   public CompletableFuture<Void> saveAsyncAll() {
      long var1 = System.currentTimeMillis();
      return CompletableFuture.allOf(
         this.values().stream().map(var3 -> this.saveToDbAsync((V)var3, this.asyncPriorityMap.saveAllPriority, var1)).toArray(CompletableFuture[]::new)
      );
   }

   protected CompletableFuture<Void> saveToDbAsync(V var1, int var2, long var3) {
      return this.runAsync(() -> {
            if (this.contains(var1.getKey())) {
               if (var1 instanceof ISavableLifecycle var2x) {
                  var2x.beforeSave();
               }
            }
         }, var2, var3)
         .thenCompose(
            var5 -> this.contains((K)var1.getKey()) && var1 instanceof IForeignMapping var6
               ? this.javaToDbAsync(var6, var2, var3)
               : CompletableFuture.completedFuture(null)
         )
         .thenCompose(var5 -> this.runAsync(() -> {
            if (this.contains(var1.getKey())) {
               TryCatchUtil.tryRun(() -> this.dao.update(var1));
            }
         }, var2, var3));
   }

   @Override
   public CompletableFuture<V> createAsync(K var1, V var2) {
      return this.supplyAsync(() -> this.create((K)var1, (V)var2), this.asyncPriorityMap.createPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<V> createAsync(V var1) {
      return this.supplyAsync(() -> this.create((V)var1), this.asyncPriorityMap.createPriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<Void> removeAsync(K var1) {
      return this.runAsync(() -> this.remove((K)var1), this.asyncPriorityMap.removePriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<Void> removeAsyncIf(BiPredicate<K, V> var1) {
      long var2 = System.currentTimeMillis();
      return CompletableFuture.allOf(
         this.entrySet()
            .stream()
            .map(
               var4 -> var1.test(var4.getKey(), var4.getValue())
                  ? this.runAsync(() -> this.remove((K)var4.getKey()), this.asyncPriorityMap.removePriority, var2)
                  : CompletableFuture.completedFuture(null)
            )
            .toArray(CompletableFuture[]::new)
      );
   }

   @Override
   public CompletableFuture<Void> removeAsyncAll() {
      return this.runAsync(this::removeAll, this.asyncPriorityMap.removePriority, System.currentTimeMillis());
   }

   @Override
   public CompletableFuture<Boolean> existsAsync(K var1) {
      return this.supplyAsync(() -> this.exists((K)var1), this.asyncPriorityMap.existsPriority, System.currentTimeMillis());
   }

   public <T> CompletableFuture<T> supplyAsync(Supplier<T> var1, int var2, long var3) {
      if (DisableLock.IS_LOCKED) {
         var1.get();
         return null;
      } else {
         CompletableFuture var5 = new CompletableFuture();
         this.executor.execute(new AsyncJob(var2, var3, var1, var5));
         return var5;
      }
   }

   public CompletableFuture<Void> runAsync(Runnable var1, int var2, long var3) {
      return this.supplyAsync(() -> {
         var1.run();
         return null;
      }, var2, var3);
   }

   @Override
   public ExecutorService getExecutor() {
      return this.executor;
   }

   @Override
   public void setExecutor(ExecutorService var1) {
      this.executor = var1;
   }

   public CompletableFuture<Void> javaToDbAsync(IForeignMapping var1, int var2, long var3) {
      return CompletableFuture.allOf(Stream.concat(var1.getForeignMapping().entrySet().stream().map(var4 -> this.runAsync(() -> {
         ((ForeignCollection)var4.getKey()).removeIf(var1xxx -> !((Collection)var4.getValue()).contains(var1xxx));
         ((Collection)var4.getValue()).forEach(var2xx -> {
            if (var2xx.getKey() != null) {
               this.updateForeign((ForeignCollection)var4.getKey(), var2xx);
            } else {
               this.addToForeign((ForeignCollection)var4.getKey(), var2xx);
            }
         });
      }, var2, var3)), var1.getForeignMappers().stream().map(var4 -> this.runAsync(() -> {
         var4.foreign().removeIf(var2xx -> !var4.cache().containsKey(this.extractKeyFromMapper(var4, var2xx)));
         var4.cache().forEach((var2xx, var3x) -> {
            if (var3x.getKey() != null) {
               this.updateForeign(var4.foreign(), var3x);
            } else {
               this.addToForeign(var4.foreign(), var3x);
            }
         });
      }, var2, var3))).toArray(CompletableFuture[]::new));
   }

   public AsyncDataCache.AsyncPriorityMap getAsyncPriorityMap() {
      return this.asyncPriorityMap;
   }

   public static class AsyncPriorityMap {
      private int getPriority;
      private int loadPriority;
      private int loadAllPriority;
      private int savePriority;
      private int saveAllPriority;
      private int modifyPriority;
      private int modifyAllPriority;
      private int createPriority;
      private int removePriority;
      private int existsPriority;

      public AsyncPriorityMap(int var1) {
         this.getPriority = var1;
         this.loadPriority = var1;
         this.loadAllPriority = var1;
         this.savePriority = var1;
         this.saveAllPriority = var1;
         this.modifyPriority = var1;
         this.modifyAllPriority = var1;
         this.createPriority = var1;
         this.removePriority = var1;
         this.existsPriority = var1;
      }

      public static AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder builder() {
         return new AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder();
      }

      public int getGetPriority() {
         return this.getPriority;
      }

      public int getLoadPriority() {
         return this.loadPriority;
      }

      public int getLoadAllPriority() {
         return this.loadAllPriority;
      }

      public int getSavePriority() {
         return this.savePriority;
      }

      public int getSaveAllPriority() {
         return this.saveAllPriority;
      }

      public int getModifyPriority() {
         return this.modifyPriority;
      }

      public int getModifyAllPriority() {
         return this.modifyAllPriority;
      }

      public int getCreatePriority() {
         return this.createPriority;
      }

      public int getRemovePriority() {
         return this.removePriority;
      }

      public int getExistsPriority() {
         return this.existsPriority;
      }

      public AsyncPriorityMap() {
      }

      public AsyncPriorityMap(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         this.getPriority = var1;
         this.loadPriority = var2;
         this.loadAllPriority = var3;
         this.savePriority = var4;
         this.saveAllPriority = var5;
         this.modifyPriority = var6;
         this.modifyAllPriority = var7;
         this.createPriority = var8;
         this.removePriority = var9;
         this.existsPriority = var10;
      }

      public static class AsyncPriorityMapBuilder {
         private int getPriority;
         private int loadPriority;
         private int loadAllPriority;
         private int savePriority;
         private int saveAllPriority;
         private int modifyPriority;
         private int modifyAllPriority;
         private int createPriority;
         private int removePriority;
         private int existsPriority;

         AsyncPriorityMapBuilder() {
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder getPriority(int var1) {
            this.getPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder loadPriority(int var1) {
            this.loadPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder loadAllPriority(int var1) {
            this.loadAllPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder savePriority(int var1) {
            this.savePriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder saveAllPriority(int var1) {
            this.saveAllPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder modifyPriority(int var1) {
            this.modifyPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder modifyAllPriority(int var1) {
            this.modifyAllPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder createPriority(int var1) {
            this.createPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder removePriority(int var1) {
            this.removePriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder existsPriority(int var1) {
            this.existsPriority = var1;
            return this;
         }

         public AsyncDataCache.AsyncPriorityMap build() {
            return new AsyncDataCache.AsyncPriorityMap(
               this.getPriority,
               this.loadPriority,
               this.loadAllPriority,
               this.savePriority,
               this.saveAllPriority,
               this.modifyPriority,
               this.modifyAllPriority,
               this.createPriority,
               this.removePriority,
               this.existsPriority
            );
         }

         @Override
         public String toString() {
            return "AsyncDataCache.AsyncPriorityMap.AsyncPriorityMapBuilder(getPriority="
               + this.getPriority
               + ", loadPriority="
               + this.loadPriority
               + ", loadAllPriority="
               + this.loadAllPriority
               + ", savePriority="
               + this.savePriority
               + ", saveAllPriority="
               + this.saveAllPriority
               + ", modifyPriority="
               + this.modifyPriority
               + ", modifyAllPriority="
               + this.modifyAllPriority
               + ", createPriority="
               + this.createPriority
               + ", removePriority="
               + this.removePriority
               + ", existsPriority="
               + this.existsPriority
               + ")";
         }
      }
   }
}
