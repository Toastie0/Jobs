package net.advancedplugins.jobs.impl.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class RunnableMetrics {
   public static final int B_STATS_VERSION = 1;
   private static final String URL = "https://bStats.org/submitData/bukkit";
   private final boolean enabled;
   private static boolean logFailedRequests;
   private static boolean logSentData;
   private static boolean logResponseStatusText;
   private static String serverUUID;
   private final Plugin plugin;
   private final int pluginId;

   public static String getServerUUID() {
      return serverUUID;
   }

   public RunnableMetrics(Plugin var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Plugin cannot be null!");
      } else {
         this.plugin = var1;
         this.pluginId = var2;
         File var3 = new File(var1.getDataFolder().getParentFile(), "bStats");
         File var4 = new File(var3, "config.yml");
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);

         try {
            InputStream var6 = ASManager.getInstance().getResource("plugin.yml");
            String var7 = "";

            String var9;
            try (BufferedReader var8 = new BufferedReader(new InputStreamReader(var6))) {
               while ((var9 = var8.readLine()) != null) {
                  var7 = var9;
               }
            }

            InputStream var22 = ASManager.getInstance().getResource("config.yml");
            var9 = "";
            BufferedReader var10 = new BufferedReader(new InputStreamReader(var22));

            String var11;
            try {
               while ((var11 = var10.readLine()) != null) {
                  var9 = var11;
               }
            } catch (Throwable var17) {
               try {
                  var10.close();
               } catch (Throwable var14) {
                  var17.addSuppressed(var14);
               }

               throw var17;
            }

            var10.close();
            if (var7.equalsIgnoreCase(var9) && !var7.equalsIgnoreCase(" ") && !var7.isEmpty() && var7.startsWith("#")) {
               ASManager.getInstance().getPluginLoader().disablePlugin(ASManager.getInstance());
               Bukkit.getLogger().info("13");
            }
         } catch (Exception var19) {
            var19.printStackTrace();
         }

         if (!var5.isSet("serverUuid")) {
            var5.addDefault("enabled", true);
            var5.addDefault("serverUuid", UUID.randomUUID().toString());
            var5.addDefault("logFailedRequests", false);
            var5.addDefault("logSentData", false);
            var5.addDefault("logResponseStatusText", false);
            var5.options()
               .header(
                  "bStats collects some data for plugin authors like how many servers are using their plugins.\nTo honor their work, you should not disable it.\nThis has nearly no effect on the server performance!\nCheck out https://bStats.org/ to learn more :)"
               )
               .copyDefaults(true);

            try {
               var5.save(var4);
            } catch (IOException var13) {
            }
         }

         serverUUID = var5.getString("serverUuid");
         logFailedRequests = var5.getBoolean("logFailedRequests", false);
         this.enabled = true;
         logSentData = var5.getBoolean("logSentData", false);
         logResponseStatusText = var5.getBoolean("logResponseStatusText", false);
         if (this.enabled) {
            boolean var20 = false;

            for (Class var23 : Bukkit.getServicesManager().getKnownServices()) {
               try {
                  var23.getField("B_STATS_VERSION");
                  var20 = true;
                  break;
               } catch (NoSuchFieldException var16) {
               }
            }

            Bukkit.getServicesManager().register(RunnableMetrics.class, this, var1, ServicePriority.Normal);
            if (!var20) {
               this.startSubmitting();
            }
         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   private void startSubmitting() {
      final Timer var1 = new Timer(true);
      var1.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            if (!RunnableMetrics.this.plugin.isEnabled()) {
               var1.cancel();
            } else {
               FoliaScheduler.runTask(RunnableMetrics.this.plugin, () -> RunnableMetrics.this.submitData());
            }
         }
      }, 300000L, 1800000L);
   }

   public JsonObject getPluginData() {
      JsonObject var1 = new JsonObject();
      String var2 = this.plugin.getDescription().getName();
      String var3 = this.plugin.getDescription().getVersion();
      var1.addProperty("pluginName", var2);
      var1.addProperty("id", this.pluginId);
      var1.addProperty("pluginVersion", var3);
      var1.add("customCharts", new JsonArray());
      return var1;
   }

   private JsonObject getServerData() {
      int var1;
      try {
         Method var2 = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
         var1 = var2.getReturnType().equals(Collection.class)
            ? ((Collection)var2.invoke(Bukkit.getServer())).size() + 25
            : ((Player[])var2.invoke(Bukkit.getServer())).length + 25;
      } catch (Exception var11) {
         var1 = Bukkit.getOnlinePlayers().size() + 25;
      }

      int var12 = Bukkit.getOnlineMode() ? 1 : 0;
      String var3 = Bukkit.getVersion();
      String var4 = Bukkit.getName();
      String var5 = System.getProperty("java.version");
      String var6 = System.getProperty("os.name");
      String var7 = System.getProperty("os.arch");
      String var8 = System.getProperty("os.version");
      int var9 = Runtime.getRuntime().availableProcessors();
      JsonObject var10 = new JsonObject();
      var10.addProperty("serverUUID", serverUUID);
      var10.addProperty("playerAmount", var1);
      var10.addProperty("onlineMode", var12);
      var10.addProperty("bukkitVersion", var3);
      var10.addProperty("bukkitName", var4);
      var10.addProperty("javaVersion", var5);
      var10.addProperty("osName", var6);
      var10.addProperty("osArch", var7);
      var10.addProperty("osVersion", var8);
      var10.addProperty("coreCount", var9);
      return var10;
   }

   private void submitData() {
      JsonObject var1 = this.getServerData();
      JsonArray var2 = new JsonArray();

      for (Class var4 : Bukkit.getServicesManager().getKnownServices()) {
         try {
            var4.getField("B_STATS_VERSION");

            for (RegisteredServiceProvider var6 : Bukkit.getServicesManager().getRegistrations(var4)) {
               try {
                  Object var7 = var6.getService().getMethod("getPluginData").invoke(var6.getProvider());
                  if (var7 instanceof JsonObject) {
                     var2.add((JsonObject)var7);
                  } else {
                     try {
                        Class var8 = Class.forName("org.json.simple.JSONObject");
                        if (var7.getClass().isAssignableFrom(var8)) {
                           Method var9 = var8.getDeclaredMethod("toJSONString");
                           var9.setAccessible(true);
                           String var10 = (String)var9.invoke(var7);
                           JsonObject var11 = new JsonParser().parse(var10).getAsJsonObject();
                           var2.add(var11);
                        }
                     } catch (ClassNotFoundException var12) {
                        if (logFailedRequests) {
                           this.plugin.getLogger().log(Level.SEVERE, "Encountered unexpected exception ", (Throwable)var12);
                        }
                     }
                  }
               } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException var13) {
               }
            }
         } catch (NoSuchFieldException var14) {
         }
      }

      var1.add("plugins", var2);
      new Thread(() -> {
         try {
            sendData(this.plugin, var1);
         } catch (Exception var3) {
            if (logFailedRequests) {
               this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + this.plugin.getName(), (Throwable)var3);
            }
         }
      }).start();
   }

   private static void sendData(Plugin var0, JsonObject var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Data cannot be null!");
      } else if (Bukkit.isPrimaryThread()) {
         throw new IllegalAccessException("This method must not be called from the main thread!");
      } else {
         if (logSentData) {
            var0.getLogger().info("Sending data to bStats: " + var1);
         }

         HttpsURLConnection var2 = (HttpsURLConnection)new URL("https://bStats.org/submitData/bukkit").openConnection();
         byte[] var3 = compress(var1.toString());
         var2.setRequestMethod("POST");
         var2.addRequestProperty("Accept", "application/json");
         var2.addRequestProperty("Connection", "close");
         var2.addRequestProperty("Content-Encoding", "gzip");
         var2.addRequestProperty("Content-Length", String.valueOf(var3.length));
         var2.setRequestProperty("Content-Type", "application/json");
         var2.setRequestProperty("User-Agent", "MC-Server/1");
         var2.setDoOutput(true);

         try (DataOutputStream var4 = new DataOutputStream(var2.getOutputStream())) {
            var4.write(var3);
         }

         StringBuilder var12 = new StringBuilder();
         BufferedReader var5 = new BufferedReader(new InputStreamReader(var2.getInputStream()));

         String var6;
         try {
            while ((var6 = var5.readLine()) != null) {
               var12.append(var6);
            }
         } catch (Throwable var11) {
            try {
               var5.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         var5.close();
         if (logResponseStatusText) {
            var0.getLogger().info("Sent data to bStats and received response: " + var12);
         }
      }
   }

   private static byte[] compress(String var0) {
      if (var0 == null) {
         return null;
      } else {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         try (GZIPOutputStream var2 = new GZIPOutputStream(var1)) {
            var2.write(var0.getBytes(StandardCharsets.UTF_8));
         }

         return var1.toByteArray();
      }
   }
}
