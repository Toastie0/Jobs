package net.advancedplugins.jobs.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.simplespigot.cache.SimpleCache;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class JobCache extends SimpleCache<String, Map<String, Job>> {
   private final Core plugin;
   private final Path dataFolder;
   private Set<Job> jobs;

   public JobCache(Core var1) {
      this.plugin = var1;
      this.dataFolder = var1.getDataFolder().toPath();
      this.jobs = Sets.newHashSet();
   }

   public Set<Job> getAllJobs() {
      return this.jobs;
   }

   public Job getJob(String var1) {
      for (Job var3 : this.jobs) {
         if (var3.getId().equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   public void cache() {
      try {
         this.createFiles();

         for (File var3 : Files.walk(this.dataFolder.resolve("jobs"))
            .map(Path::toFile)
            .filter(var0 -> !var0.getName().equals("jobs"))
            .filter(var0 -> !var0.getName().startsWith("__"))
            .collect(Collectors.toSet())) {
            try {
               String var4 = var3.getName().replace(".yml", "");
               Config var5 = new Config(this.plugin, var3, true);
               this.migrateToNewActions(var5);
               this.jobs.add(new Job(var4, var5));
            } catch (Exception var6) {
               var6.printStackTrace();
               Bukkit.getLogger().severe("Could not load job '" + var3.getName() + "' due to invalid configuration.");
            }
         }
      } catch (Throwable var7) {
         throw var7;
      }
   }

   private void migrateToNewActions(Config var1) {
      if (!var1.has("actions-version") || var1.integer("actions-version") <= 1) {
         File var2 = var1.getFile();
         YamlConfiguration var3 = var1.getConfiguration();
         File var4 = new File(this.plugin.getDataFolder(), "jobs-backup-actions-v1");
         var4.mkdirs();
         File var5 = new File(var4, var2.getName());
         Files.copy(var2.toPath(), var5.toPath(), StandardCopyOption.REPLACE_EXISTING);
         Bukkit.getLogger().info("Migrating " + var2.getName() + " to a new actions system. Backup saved in " + var5.getAbsolutePath());
         String var6 = "variable";
         if (var1.has("variable.root")) {
            var6 = "variable.root";
         }

         List var7 = var1.get(var6) instanceof List ? var1.stringList(var6) : Arrays.stream(var1.string(var6).split(" OR ")).collect(Collectors.toList());
         boolean var8 = !var7.isEmpty() && ((String)var7.get(0)).startsWith("!");
         ArrayList var9 = new ArrayList();
         var7.forEach(var2x -> {
            String[] var3x = var2x.split(":", 2);
            String var4x = var3x[0];
            int var5x = var3x.length == 2 ? Integer.parseInt(var3x[1]) : 0;
            if (var8 && var4x.startsWith("!")) {
               var4x = var4x.substring(1);
            }

            String var6x = var4x;

            try {
               Material var7x = Material.valueOf(var4x.toUpperCase());
               if (Tag.CROPS.isTagged(var7x) && var5x > 0) {
                  var6x = var6x + "{age: " + var5x + "}";
               }
            } catch (Exception var8x) {
            }

            var9.add(var6x);
         });
         String var10 = String.join(" OR ", var9);
         if (var8) {
            var10 = "!" + var10;
         }

         HashMap var11 = Maps.newHashMap();
         if (var1.get("variable") instanceof MemorySection) {
            var3.getConfigurationSection("variable").getValues(true).forEach((var1x, var2x) -> {
               if (!var1x.equalsIgnoreCase("root") && !(var2x instanceof MemorySection)) {
                  String[] var3x = var2x.toString().split(" OR ");
                  if (var3x.length != 0) {
                     if (var3x[0].contains(":")) {
                        String[] var4x = var3x[0].split(":", 2);

                        try {
                           Material.valueOf(var4x[0].toUpperCase());
                        } catch (Exception var9x) {
                           return;
                        }

                        var11.put("variable." + var1x, new ArrayList());

                        for (String var8x : var3x) {
                           ((List)var11.get("variable." + var1x)).add(var8x.split(":")[0]);
                        }
                     }
                  }
               }
            });
         }

         var3.set(var6, var10);
         var11.forEach(var3::set);
         var3.set("actions-version", 2);
         var3.save(var2);
         var1.reload();
      }
   }

   private void createFiles() {
      Path var1 = this.dataFolder.resolve("jobs");
      if (!var1.toFile().isDirectory()) {
         this.plugin.saveResource("jobs/beekeeper.yml", false);
         this.plugin.saveResource("jobs/breeder.yml", false);
         this.plugin.saveResource("jobs/brewer.yml", false);
         this.plugin.saveResource("jobs/builder.yml", false);
         this.plugin.saveResource("jobs/crafter.yml", false);
         this.plugin.saveResource("jobs/customenchanter.yml", false);
         this.plugin.saveResource("jobs/enchanter.yml", false);
         this.plugin.saveResource("jobs/explorer.yml", false);
         this.plugin.saveResource("jobs/farmer.yml", false);
         this.plugin.saveResource("jobs/fighter.yml", false);
         this.plugin.saveResource("jobs/fisherman.yml", false);
         this.plugin.saveResource("jobs/glider.yml", false);
         this.plugin.saveResource("jobs/hunter.yml", false);
         this.plugin.saveResource("jobs/lumberjack.yml", false);
         this.plugin.saveResource("jobs/miner.yml", false);
         this.plugin.saveResource("jobs/smelter.yml", false);
         this.plugin.saveResource("jobs/tamer.yml", false);
         this.plugin.saveResource("jobs/gourmet.yml", false);
         this.plugin.saveResource("jobs/runner.yml", false);
         this.plugin.saveResource("jobs/swimmer.yml", false);
      }
   }
}
