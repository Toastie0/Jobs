package net.advancedplugins.jobs.impl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DataHandler {
   private File file = null;
   private FileConfiguration fileConfiguration;
   private String fileName;
   private int loopNumber;
   private JavaPlugin instance;
   private List<Integer> activeTasks = new ArrayList<>();
   private List<Listener> listeners = new ArrayList<>();

   public DataHandler(File var1, JavaPlugin var2) {
      this.file = var1;
      this.instance = var2;
      this.populateFile(false);
   }

   public DataHandler(String var1, JavaPlugin var2) {
      this(var1, var2, false);
   }

   public DataHandler(String var1, JavaPlugin var2, boolean var3) {
      this.instance = var2;
      if (var1 != null) {
         this.fileName = var1;
         this.populateFile(var3);
      }
   }

   public DataHandler() {
   }

   private void populateFile(boolean var1) {
      this.fileConfiguration = new YamlConfiguration();
      File var2 = this.instance.getDataFolder();
      if (!var2.isDirectory()) {
         var2.mkdirs();
      }

      if (this.file == null) {
         this.file = new File(this.instance.getDataFolder(), this.fileName + ".yml");
      }

      if (this.instance.getResource(this.fileName + ".yml") != null) {
         if (!this.file.exists()) {
            this.instance.saveResource(this.fileName + ".yml", true);
         }
      } else if (var1) {
         String[] var3 = this.fileName.split("/");
         String var4 = "";
         if (var3.length > 1) {
            for (int var5 = 0; var5 < var3.length - 1; var5++) {
               var4 = var4 + var3[var5] + "/";
               File var6 = new File(this.instance.getDataFolder(), var4);
               if (!var6.isDirectory()) {
                  var6.mkdirs();
               }
            }
         }

         if (!this.file.exists()) {
            try {
               this.file.createNewFile();
            } catch (IOException var8) {
               var8.printStackTrace();
            }
         }
      }

      try {
         this.fileConfiguration.load(this.file);
      } catch (InvalidConfigurationException | IOException var7) {
         var7.printStackTrace();
      }
   }

   public void reloadConfig() {
      this.file = new File(this.file.getPath());

      try {
         this.fileConfiguration = new YamlConfiguration();
         this.fileConfiguration.load(new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8));
      } catch (InvalidConfigurationException | IOException var2) {
         var2.printStackTrace();
      }
   }

   public FileConfiguration getConfig() {
      return this.fileConfiguration;
   }

   public File getFile() {
      return this.file;
   }

   public void save() {
      try {
         this.fileConfiguration.save(this.file);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   public void saveAsync() {
      FoliaScheduler.runTaskAsynchronously(this.instance, this::save);
   }

   public int increaseLoop() {
      this.loopNumber++;
      return this.loopNumber;
   }

   public int getLoopNumber() {
      return this.loopNumber;
   }

   public void clearLoopNumer() {
      this.loopNumber = 0;
   }

   public boolean isPath(String var1) {
      return this.fileConfiguration.isConfigurationSection(var1);
   }

   public Set<String> getKeys(String var1) {
      return !this.fileConfiguration.isConfigurationSection(var1)
         ? Collections.emptySet()
         : this.fileConfiguration.getConfigurationSection(var1).getKeys(false);
   }

   public Set<String> getKeys(FileConfiguration var1, String var2) {
      return var1.getConfigurationSection(var2).getKeys(false);
   }

   public <T extends Enum<T>> T getEnum(String var1, Class<T> var2) {
      String var3 = this.fileConfiguration.getString(var1);

      try {
         return Enum.valueOf(var2, var3);
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

   public LocalLocation getLocation(String var1) {
      String var2 = this.getConfig().getString(var1);
      return LocalLocation.getFromEncode(var2);
   }

   public void setLocation(String var1, Location var2) {
      this.setLocation(var1, new LocalLocation(var2));
   }

   public void setLocation(String var1, LocalLocation var2) {
      this.getConfig().set(var1, var2.getEncode());
   }

   public void tick() {
   }

   public void unload() {
      for (int var2 : this.activeTasks) {
         Bukkit.getScheduler().cancelTask(var2);
      }

      for (Listener var4 : this.listeners) {
         HandlerList.unregisterAll(var4);
      }
   }

   public UUID stringToId(String var1) {
      return UUID.fromString(var1);
   }

   public UUID getUUID(String var1) {
      return UUID.fromString(this.getConfig().getString(var1));
   }

   public int getInt(String var1) {
      return this.getConfig().getInt(var1);
   }

   public List<String> getStringList(String var1) {
      List var2 = this.getConfig().getStringList(var1);
      if (!var2.isEmpty()) {
         return var2;
      } else {
         String var3 = this.getString(var1);
         return var3 != null && !var3.isEmpty() && !var3.equalsIgnoreCase("[]") ? new ArrayList<>(Collections.singletonList(var3)) : new ArrayList<>();
      }
   }

   public String getString(String var1) {
      return this.getConfig().getString(var1);
   }

   public String getString(String var1, String var2) {
      return this.getConfig().getString(var1, var2);
   }

   public String getStringColored(String var1) {
      return ColorUtils.format(this.getString(var1));
   }

   public boolean getBoolean(String var1, boolean var2) {
      return this.getConfig().getBoolean(var1, var2);
   }

   public boolean getBoolean(String var1) {
      return this.getConfig().getBoolean(var1);
   }

   public <T> HashMap<String, T> sectionToMap(String var1, Class<T> var2) {
      HashMap var3 = new HashMap();

      for (String var5 : this.getKeys(var1)) {
         var3.put(var5, this.getConfig().get(var1 + "." + var5));
      }

      return var3;
   }

   public boolean isEnabled() {
      return this.getBoolean("enabled", true);
   }

   public void addTask(int var1) {
      this.activeTasks.add(var1);
   }

   public void registerListener(Listener var1) {
      Bukkit.getPluginManager().registerEvents(var1, ASManager.getInstance());
      this.listeners.add(var1);
   }

   public String getFileName() {
      return this.fileName;
   }

   public JavaPlugin getInstance() {
      return this.instance;
   }

   public List<Integer> getActiveTasks() {
      return this.activeTasks;
   }

   public List<Listener> getListeners() {
      return this.listeners;
   }
}
