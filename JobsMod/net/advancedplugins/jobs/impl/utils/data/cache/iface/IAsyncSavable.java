package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface IAsyncSavable<K, V extends ICached<K>> extends ISavable<K, V> {
   CompletableFuture<V> loadAsync(K var1);

   CompletableFuture<Collection<V>> loadAsyncAll();

   CompletableFuture<Collection<V>> loadAsyncAll(boolean var1);

   CompletableFuture<Void> modifyAsync(K var1, Consumer<V> var2);

   CompletableFuture<Void> modifyAsyncMultiple(Set<K> var1, Consumer<V> var2);

   CompletableFuture<Void> modifyAsyncAll(Consumer<V> var1);

   CompletableFuture<Void> loopAsyncAll(Consumer<V> var1);

   CompletableFuture<Collection<V>> loopAsyncAll();

   CompletableFuture<Void> saveAsync(K var1);

   CompletableFuture<Void> saveAsyncValue(V var1);

   CompletableFuture<Void> saveAsyncAll();

   CompletableFuture<V> createAsync(K var1, V var2);

   CompletableFuture<V> createAsync(V var1);

   CompletableFuture<Void> removeAsync(K var1);

   CompletableFuture<Void> removeAsyncIf(BiPredicate<K, V> var1);

   CompletableFuture<Void> removeAsyncAll();

   CompletableFuture<Boolean> existsAsync(K var1);
}
