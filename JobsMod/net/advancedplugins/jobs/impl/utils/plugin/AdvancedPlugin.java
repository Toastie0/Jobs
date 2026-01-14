package net.advancedplugins.jobs.impl.utils.plugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.files.ResourceFileManager;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedPlugin extends JavaPlugin implements Listener {
   private static AdvancedPlugin instance;
   private String startupError = null;
   private String pluginName = "";
   private boolean loaded = false;

   public void startup() {
   }

   public void unload() {
   }

   public void registerListeners() {
   }

   public void registerCommands() {
   }

   public void onDisable() {
      try {
         this.unload();
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public void onEnable() {
      loadConfig0();
      instance = this;
      ASManager.setInstance(this);
      super.onEnable();
      this.pluginName = this.getDescription().getName();

      try {
         this.startup();
      } catch (Exception var2) {
         var2.printStackTrace();
         this.updateError(var2);
      }

      if (this.startupError != null) {
         this.getServer().getPluginManager().registerEvents(this, this);
      }
   }

   private void updateError(Exception var1) {
      if (var1.getClass().equals(ClassCastException.class)) {
         this.startupError = "Configuration error: A value of the wrong type was found. Please check your config files.";
      } else if (var1.getClass().equals(IOException.class)) {
         this.startupError = "File I/O error while loading configurations. Please check file permissions and paths, whether configuration file is not missing.";
      } else if (var1.getClass().equals(InvalidConfigurationException.class)) {
         this.startupError = "The configuration file is improperly formatted. Please verify the syntax of your config files (tools: https://yaml.helpch.at)";
      } else {
         this.startupError = "An unexpected error occurred while loading the plugin. ";
      }
   }

   public void registerEvents(Listener var1) {
      Bukkit.getPluginManager().registerEvents(var1, instance);
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      if (var1.getPlayer().isOp()) {
         if (this.startupError != null) {
            FoliaScheduler.runTaskLater(
               this,
               () -> {
                  var1.getPlayer().sendMessage(Text.modify("&c[" + this.pluginName + "] Unable to load the plugin correctly due to errors:"));
                  var1.getPlayer().sendMessage(Text.modify("&c&o" + this.startupError));
                  var1.getPlayer()
                     .sendMessage(
                        Text.modify("&cIf the problem persists after checking the config files, please seek assistance at: https://discord.gg/advancedplugins")
                     );
               },
               20L
            );
         }
      }
   }

   public void saveFiles(String... var1) {
      for (String var5 : var1) {
         this.saveResource(var5);
      }
   }

   public void saveResource(String var1) {
      if (!new File(this.getDataFolder(), var1).isFile()) {
         if (!var1.contains("_internal")) {
            this.saveResource(var1, false);
         }
      }
   }

   protected CompletableFuture<Void> initializeMaterialSupport(boolean var1) {
      Executor var2 = var1 ? new CompletableFuture().defaultExecutor() : Runnable::run;
      return CompletableFuture.runAsync(() -> {
         try {
            Material.matchMaterial("", true);
            this.getLogger().info("Legacy material support initialized. Ignore any error or warn message.");
         } catch (Exception var2x) {
            this.getLogger().log(Level.SEVERE, "Cannot initialize legacy material support", (Throwable)var2x);
         }
      }, var2);
   }

   public void saveAllFiles(String var1) {
      if (!new File(this.getDataFolder(), var1).exists()) {
         ResourceFileManager.saveAllResources(this, var1, null);
      }
   }

   public static AdvancedPlugin getInstance() {
      return instance;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public void setLoaded(boolean var1) {
      this.loaded = var1;
   }
}
