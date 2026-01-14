package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface IAsyncCache<K, V extends ICached<K>> extends ICache<K, V> {
   CompletableFuture<V> getAsync(K var1);

   CompletableFuture<V> getAsyncOrNull(K var1);

   ExecutorService getExecutor();

   void setExecutor(ExecutorService var1);
}
