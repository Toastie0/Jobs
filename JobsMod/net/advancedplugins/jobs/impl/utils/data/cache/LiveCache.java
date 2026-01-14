package net.advancedplugins.jobs.impl.utils.data.cache;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.data.DatabaseController;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ICached;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ISavableLifecycle;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class LiveCache<K, V extends ICached<K>> extends AsyncDataCache<K, V> {
   private final long changesWaitingTIme;
   private final long changesMaxWaitingTime;
   private final LiveCache.AsyncLivePriorityMap asyncLivePriorityMap;
   private final ConcurrentMap<K, ConcurrentMap<String, LiveCache<K, V>.PendingChange<?>>> pendingChanges;

   public LiveCache(DatabaseController var1, JavaPlugin var2, long var3, long var5, LiveCache.AsyncLivePriorityMap var7) {
      super(var1, var2, var7.getAsyncPriorityMap());
      this.changesWaitingTIme = var3;
      this.changesMaxWaitingTime = var5;
      this.asyncLivePriorityMap = var7;
      this.pendingChanges = new ConcurrentHashMap<>();
   }

   public LiveCache(DatabaseController var1, Class<K> var2, Class<V> var3, JavaPlugin var4, long var5, long var7, LiveCache.AsyncLivePriorityMap var9) {
      super(var1, var2, var3, var4, var9.getAsyncPriorityMap());
      this.changesWaitingTIme = var5;
      this.changesMaxWaitingTime = var7;
      this.asyncLivePriorityMap = var9;
      this.pendingChanges = new ConcurrentHashMap<>();
   }

   public <T> void addPendingChange(K var1, String var2, T var3, ForeignCollection<T> var4) {
      this.pendingChanges.putIfAbsent((K)var1, new ConcurrentHashMap<>());
      Map var5 = this.pendingChanges.get(var1);
      LiveCache.PendingChange var6;
      if (var5.containsKey(var2)) {
         var6 = (LiveCache.PendingChange)var5.get(var2);
         var6.task.cancel();
         if (System.currentTimeMillis() - var6.firstChangeTime >= this.changesMaxWaitingTime) {
            var6.saveAsync(this.asyncLivePriorityMap.progressPriority, System.currentTimeMillis());
            var5.remove(var2);
            return;
         }
      } else {
         var6 = new LiveCache.PendingChange<>(var3, var4, System.currentTimeMillis(), null);
      }

      FoliaScheduler.Task var7 = FoliaScheduler.runTaskLater(this.plugin, () -> {
         var6.saveAsync(this.asyncLivePriorityMap.progressPriority, System.currentTimeMillis());
         var5.remove(var2);
      }, this.changesWaitingTIme / 50L);
      var6.setTask(var7);
      var5.put(var2, var6);
   }

   public CompletableFuture<Void> saveAllPending(K var1, int var2) {
      long var3 = System.currentTimeMillis();
      return CompletableFuture.allOf(this.pendingChanges.getOrDefault(var1, new ConcurrentHashMap<>()).values().stream().map(var3x -> {
         var3x.task.cancel();
         return var3x.saveAsync(var2, var3);
      }).toArray(CompletableFuture[]::new)).thenRun(() -> this.pendingChanges.remove(var1));
   }

   public void cancelAllPending(K var1) {
      if (this.pendingChanges.containsKey(var1)) {
         this.pendingChanges.get(var1).forEach((var0, var1x) -> var1x.task.cancel());
         this.pendingChanges.remove(var1);
      }
   }

   public void cancelPending(K var1, String var2) {
      if (this.pendingChanges.containsKey(var1)) {
         LiveCache.PendingChange var3 = this.pendingChanges.get(var1).get(var2);
         if (var3 != null) {
            var3.task.cancel();
            this.pendingChanges.get(var1).remove(var2);
         }
      }
   }

   @Override
   protected CompletableFuture<Void> saveToDbAsync(V var1, int var2, long var3) {
      return this.runAsync(() -> {
         if (this.contains(var1.getKey())) {
            if (var1 instanceof ISavableLifecycle var2x) {
               var2x.beforeSave();
            }
         }
      }, var2, var3).thenCompose(var5 -> this.runAsync(() -> {
         if (this.contains(var1.getKey())) {
            TryCatchUtil.tryRun(() -> this.dao.update(var1));
         }
      }, var2, var3)).thenCompose(var3x -> this.saveAllPending((K)var1.getKey(), var2));
   }

   @Override
   protected void saveToDb(V var1) {
      if (var1 instanceof ISavableLifecycle var2) {
         var2.beforeSave();
      }

      TryCatchUtil.tryRun(() -> this.dao.update(var1));
      this.saveAllPending((K)var1.getKey(), this.getAsyncPriorityMap().getSavePriority());
   }

   public void saveFull(V var1) {
      super.saveToDb((V)var1);
   }

   public CompletableFuture<Void> saveAsyncFull(V var1, int var2) {
      return super.saveToDbAsync((V)var1, var2, System.currentTimeMillis());
   }

   @Override
   public void remove(K var1) {
      super.remove((K)var1);
      this.cancelAllPending((K)var1);
   }

   @Override
   public void removeAll() {
      super.removeAll();
      this.pendingChanges.keySet().forEach(this::cancelAllPending);
   }

   public LiveCache.AsyncLivePriorityMap getAsyncLivePriorityMap() {
      return this.asyncLivePriorityMap;
   }

   public static class AsyncLivePriorityMap {
      private AsyncDataCache.AsyncPriorityMap asyncPriorityMap;
      private int progressPriority;
      private int liveActionPriority;

      public AsyncLivePriorityMap(int var1) {
         this.asyncPriorityMap = new AsyncDataCache.AsyncPriorityMap(var1);
         this.progressPriority = var1;
         this.liveActionPriority = var1;
      }

      public static LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder builder() {
         return new LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder();
      }

      public AsyncDataCache.AsyncPriorityMap getAsyncPriorityMap() {
         return this.asyncPriorityMap;
      }

      public int getProgressPriority() {
         return this.progressPriority;
      }

      public int getLiveActionPriority() {
         return this.liveActionPriority;
      }

      public AsyncLivePriorityMap() {
      }

      public AsyncLivePriorityMap(AsyncDataCache.AsyncPriorityMap var1, int var2, int var3) {
         this.asyncPriorityMap = var1;
         this.progressPriority = var2;
         this.liveActionPriority = var3;
      }

      public static class AsyncLivePriorityMapBuilder {
         private AsyncDataCache.AsyncPriorityMap asyncPriorityMap;
         private int progressPriority;
         private int liveActionPriority;

         AsyncLivePriorityMapBuilder() {
         }

         public LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder asyncPriorityMap(AsyncDataCache.AsyncPriorityMap var1) {
            this.asyncPriorityMap = var1;
            return this;
         }

         public LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder progressPriority(int var1) {
            this.progressPriority = var1;
            return this;
         }

         public LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder liveActionPriority(int var1) {
            this.liveActionPriority = var1;
            return this;
         }

         public LiveCache.AsyncLivePriorityMap build() {
            return new LiveCache.AsyncLivePriorityMap(this.asyncPriorityMap, this.progressPriority, this.liveActionPriority);
         }

         @Override
         public String toString() {
            return "LiveCache.AsyncLivePriorityMap.AsyncLivePriorityMapBuilder(asyncPriorityMap="
               + this.asyncPriorityMap
               + ", progressPriority="
               + this.progressPriority
               + ", liveActionPriority="
               + this.liveActionPriority
               + ")";
         }
      }
   }

   public class PendingChange<T> {
      private final T value;
      private final ForeignCollection<T> collection;
      private final long firstChangeTime;
      private FoliaScheduler.Task task;

      public void save() {
         TryCatchUtil.tryAndReturn(() -> this.collection.update(this.value));
      }

      public CompletableFuture<Void> saveAsync(int var1, long var2) {
         return LiveCache.this.runAsync(this::save, var1, var2);
      }

      public T getValue() {
         return this.value;
      }

      public ForeignCollection<T> getCollection() {
         return this.collection;
      }

      public long getFirstChangeTime() {
         return this.firstChangeTime;
      }

      public FoliaScheduler.Task getTask() {
         return this.task;
      }

      public void setTask(FoliaScheduler.Task var1) {
         this.task = var1;
      }

      public PendingChange(final T nullx, final ForeignCollection<T> nullxx, final long nullxxx, final FoliaScheduler.Task nullxxxx) {
         this.value = (T)nullx;
         this.collection = nullxx;
         this.firstChangeTime = nullxxx;
         this.task = nullxxxx;
      }
   }
}
