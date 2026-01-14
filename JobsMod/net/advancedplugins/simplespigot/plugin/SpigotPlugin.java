package net.advancedplugins.simplespigot.plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.advancedplugins.simplespigot.config.ConfigStore;
import net.advancedplugins.simplespigot.registry.Registry;
import net.advancedplugins.simplespigot.save.SavingController;
import net.advancedplugins.simplespigot.storage.BackendFactory;
import net.advancedplugins.simplespigot.storage.StorageSettings;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpigotPlugin extends JavaPlugin implements SimplePlugin {
   protected final BackendFactory storageFactory = new BackendFactory(this);
   protected final StorageSettings storageSettings = new StorageSettings();
   protected final ConfigStore configStore = new ConfigStore(this);
   protected final SavingController savingController = new SavingController(this);

   @Override
   public void runAsync(Runnable var1) {
      Bukkit.getScheduler().runTaskAsynchronously(this, var1);
   }

   @Override
   public <T> CompletableFuture<T> asyncCallback(Supplier<T> var1) {
      return CompletableFuture.supplyAsync(var1, this::runAsync);
   }

   @Override
   public void runSync(Runnable var1) {
      Bukkit.getScheduler().runTask(this, var1);
   }

   @Override
   public <T> CompletableFuture<T> syncCallback(Supplier<T> var1) {
      return CompletableFuture.supplyAsync(var1, this::runSync);
   }

   @Override
   public void registerRegistries(Registry... var1) {
      for (Registry var5 : var1) {
         var5.register();
      }
   }

   @Override
   public void registerListeners(Listener... var1) {
      for (Listener var5 : var1) {
         Bukkit.getPluginManager().registerEvents(var5, this);
      }
   }

   @Override
   public StorageSettings getStorageSettings() {
      return this.storageSettings;
   }

   @Override
   public ConfigStore getConfigStore() {
      return this.configStore;
   }

   @Override
   public SavingController getSavingController() {
      return this.savingController;
   }
}
