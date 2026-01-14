package net.advancedplugins.jobs.bstats.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetricsConfig {
   private final File file;
   private final boolean defaultEnabled;
   private String serverUUID;
   private boolean enabled;
   private boolean logErrors;
   private boolean logSentData;
   private boolean logResponseStatusText;
   private boolean didExistBefore = true;

   public MetricsConfig(File var1, boolean var2) {
      this.file = var1;
      this.defaultEnabled = var2;
      this.setupConfig();
   }

   public String getServerUUID() {
      return this.serverUUID;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isLogErrorsEnabled() {
      return this.logErrors;
   }

   public boolean isLogSentDataEnabled() {
      return this.logSentData;
   }

   public boolean isLogResponseStatusTextEnabled() {
      return this.logResponseStatusText;
   }

   public boolean didExistBefore() {
      return this.didExistBefore;
   }

   private void setupConfig() {
      if (!this.file.exists()) {
         this.didExistBefore = false;
         this.writeConfig();
      }

      this.readConfig();
      if (this.serverUUID == null) {
         this.writeConfig();
         this.readConfig();
      }
   }

   private void writeConfig() {
      ArrayList var1 = new ArrayList();
      var1.add("# bStats (https://bStats.org) collects some basic information for plugin authors, like");
      var1.add("# how many people use their plugin and their total player count. It's recommended to keep");
      var1.add("# bStats enabled, but if you're not comfortable with this, you can turn this setting off.");
      var1.add("# There is no performance penalty associated with having metrics enabled, and data sent to");
      var1.add("# bStats is fully anonymous.");
      var1.add("enabled=" + this.defaultEnabled);
      var1.add("server-uuid=" + UUID.randomUUID().toString());
      var1.add("log-errors=false");
      var1.add("log-sent-data=false");
      var1.add("log-response-status-text=false");
      this.writeFile(this.file, var1);
   }

   private void readConfig() {
      List var1 = this.readFile(this.file);
      if (var1 == null) {
         throw new AssertionError("Content of newly created file is null");
      } else {
         this.enabled = this.getConfigValue("enabled", var1).map("true"::equals).orElse(true);
         this.serverUUID = this.getConfigValue("server-uuid", var1).orElse(null);
         this.logErrors = this.getConfigValue("log-errors", var1).map("true"::equals).orElse(false);
         this.logSentData = this.getConfigValue("log-sent-data", var1).map("true"::equals).orElse(false);
         this.logResponseStatusText = this.getConfigValue("log-response-status-text", var1).map("true"::equals).orElse(false);
      }
   }

   private Optional<String> getConfigValue(String var1, List<String> var2) {
      return var2.stream().filter(var1x -> var1x.startsWith(var1 + "=")).map(var1x -> var1x.replaceFirst(Pattern.quote(var1 + "="), "")).findFirst();
   }

   private List<String> readFile(File var1) {
      if (!var1.exists()) {
         return null;
      } else {
         FileReader var2 = new FileReader(var1);

         List var4;
         try {
            BufferedReader var3 = new BufferedReader(var2);

            try {
               var4 = var3.lines().collect(Collectors.toList());
            } catch (Throwable var8) {
               try {
                  var3.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            var3.close();
         } catch (Throwable var9) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         var2.close();
         return var4;
      }
   }

   private void writeFile(File var1, List<String> var2) {
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         var1.createNewFile();
      }

      FileWriter var3 = new FileWriter(var1);

      try {
         BufferedWriter var4 = new BufferedWriter(var3);

         try {
            for (String var6 : var2) {
               var4.write(var6);
               var4.newLine();
            }
         } catch (Throwable var9) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         var4.close();
      } catch (Throwable var10) {
         try {
            var3.close();
         } catch (Throwable var7) {
            var10.addSuppressed(var7);
         }

         throw var10;
      }

      var3.close();
   }
}
