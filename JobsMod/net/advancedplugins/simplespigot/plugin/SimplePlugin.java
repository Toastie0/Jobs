package net.advancedplugins.simplespigot.plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.advancedplugins.simplespigot.config.ConfigStore;
import net.advancedplugins.simplespigot.registry.Registry;
import net.advancedplugins.simplespigot.save.SavingController;
import net.advancedplugins.simplespigot.storage.StorageSettings;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface SimplePlugin extends Plugin {
   void runAsync(Runnable var1);

   <T> CompletableFuture<T> asyncCallback(Supplier<T> var1);

   void runSync(Runnable var1);

   <T> CompletableFuture<T> syncCallback(Supplier<T> var1);

   void registerRegistries(Registry... var1);

   void registerListeners(Listener... var1);

   StorageSettings getStorageSettings();

   ConfigStore getConfigStore();

   SavingController getSavingController();
}
