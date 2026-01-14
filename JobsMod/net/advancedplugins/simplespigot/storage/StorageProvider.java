package net.advancedplugins.simplespigot.storage;

import java.util.function.Function;
import net.advancedplugins.simplespigot.plugin.SimplePlugin;
import net.advancedplugins.simplespigot.storage.storage.Storage;

public class StorageProvider {
   public static <T> Storage<T> provide(SimplePlugin var0, Function<BackendFactory, Backend> var1, Function<Backend, Storage<T>> var2) {
      return (Storage<T>)var2.apply((Backend)var1.apply(new BackendFactory(var0)));
   }

   public static <T> Storage<T> provide(BackendFactory var0, Function<BackendFactory, Storage<T>> var1) {
      return (Storage<T>)var1.apply(var0);
   }
}
