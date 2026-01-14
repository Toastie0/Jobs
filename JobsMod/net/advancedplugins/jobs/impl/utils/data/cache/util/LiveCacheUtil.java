package net.advancedplugins.jobs.impl.utils.data.cache.util;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.concurrent.CompletableFuture;
import net.advancedplugins.jobs.impl.utils.data.cache.LiveCache;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IAsyncSavableCache;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ICached;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;

public class LiveCacheUtil {
   private static boolean isLiveCache(IAsyncSavableCache<?, ?> var0) {
      return var0 instanceof LiveCache;
   }

   public static <V extends ICached<?>> CompletableFuture<Void> saveAsyncFull(IAsyncSavableCache<?, V> var0, V var1, boolean var2) {
      return var0 instanceof LiveCache var3
         ? var3.saveAsyncFull((V)var1, var2 ? var3.getAsyncPriorityMap().getSavePriority() : var3.getAsyncPriorityMap().getSaveAllPriority())
         : var0.saveAsyncValue((V)var1);
   }

   public static <K, T> void addPendingChange(IAsyncSavableCache<K, ?> var0, K var1, String var2, T var3, ForeignCollection<T> var4) {
      if (var0 instanceof LiveCache var5) {
         var5.addPendingChange(var1, var2, var3, var4);
      }
   }

   public static <T> void createForeign(IAsyncSavableCache<?, ?> var0, T var1, ForeignCollection<T> var2) {
      if (var0 instanceof LiveCache var3) {
         var3.runAsync(() -> TryCatchUtil.tryRun(() -> var2.add(var1)), var3.getAsyncLivePriorityMap().getLiveActionPriority(), System.currentTimeMillis());
      }
   }

   public static <T> void removeForeign(IAsyncSavableCache<?, ?> var0, T var1, ForeignCollection<T> var2) {
      if (var0 instanceof LiveCache var3) {
         var3.runAsync(
            () -> TryCatchUtil.tryRun(() -> var2.getDao().delete(var1)), var3.getAsyncLivePriorityMap().getLiveActionPriority(), System.currentTimeMillis()
         );
      }
   }

   public static void clearForeign(IAsyncSavableCache<?, ?> var0, ForeignCollection<?> var1) {
      if (var0 instanceof LiveCache var2) {
         var2.runAsync(() -> TryCatchUtil.tryRun(var1::clear), var2.getAsyncLivePriorityMap().getLiveActionPriority(), System.currentTimeMillis());
      }
   }
}
