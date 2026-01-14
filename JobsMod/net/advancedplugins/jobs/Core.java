package net.advancedplugins.jobs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.StringConcatFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.advancedplugins.jobs.actions.Action;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.cache.RewardCache;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.commands.jobs.AliasCommand;
import net.advancedplugins.jobs.commands.jobs.JobCommand;
import net.advancedplugins.jobs.commands.jobsAdmin.JobAdminCommand;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.controller.LeaderboardController;
import net.advancedplugins.jobs.creator.GUICreatorHandler;
import net.advancedplugins.jobs.events.admin.JobsReloadEvent;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.Registry;
import net.advancedplugins.jobs.impl.utils.RunnableMetrics;
import net.advancedplugins.jobs.impl.utils.commands.CommandBase;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.plugin.FirstInstall;
import net.advancedplugins.jobs.jobs.ActionsHandler;
import net.advancedplugins.jobs.jobs.JobsPipeline;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.listeners.UserConnectListener;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.menus.MenuFactory;
import net.advancedplugins.jobs.menus.service.MenuIllustrator;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.placeholder.PlaceholdersPAPI;
import net.advancedplugins.jobs.registry.ArgumentRegistry;
import net.advancedplugins.jobs.storage.BoostersStorage;
import net.advancedplugins.jobs.storage.JobsStorage;
import net.advancedplugins.jobs.storage.UserStorage;
import net.advancedplugins.jobs.util.bossbar.BossBar;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.listener.MenuListener;
import net.advancedplugins.simplespigot.plugin.SpigotPlugin;
import net.advancedplugins.simplespigot.storage.StorageSettings;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Core extends SpigotPlugin {
   private static Core instance;
   private static Logger logger;
   private LocaleHandler locale;
   private Cache<String, Map<Integer, List<Action>>> actionCache;
   private ActionRegistry actionRegistry;
   private JobController jobController;
   private JobsResetHandler jobsResetHandler;
   private Storage<JobsResetHandler> resetStorage;
   private JobCache jobCache;
   private RewardCache rewardCache;
   private JobsPipeline jobsPipeline;
   private BoostersController boostersController;
   private Storage<BoostersController> boostersStorage;
   private UserCache userCache;
   private Storage<User> userStorage;
   private MenuIllustrator menuIllustrator;
   private MenuFactory menuFactory;
   public static boolean mysql;
   private List<Map<UUID, BossBar>> bossBars;
   private GUICreatorHandler creatorHandler;
   private CommandBase commandBase;
   private LeaderboardController leaderboardController;
   private boolean stop = false;
   private static String lastId = Registry.get();
   private String serverUUID;
   public static boolean Bukkit = false;
   private boolean lastTime = false;

   public void onEnable() {
      ASManager.setInstance(this);
      MinecraftVersion.init();
      new RunnableMetrics(this, 20960);
      instance = this;
      logger = this.getLogger();
      this.bossBars = new ArrayList<>();
      HooksHandler.hook(instance);
      this.configRelations();
      MinecraftVersion.init();
      this.locale = new LocaleHandler(this);
      this.locale.readLocaleFiles(this, "lang", "lang/en.yml");
      this.locale.setLocale(this.getConfig("config").string("language"));
      this.locale.setPrefix("prefix");
      this.load();
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         new PlaceholdersPAPI(this).register();
      }

      try {
         (new BukkitRunnable() {
               int conCount = 0;

               public void run() {
                  Core.lastId = Registry.get();

                  try {
                     String var1 = Registry.get();
                     if (var1 == null || var1.startsWith("0")) {
                        Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                     }

                     InputStream var2x = Core.getInstance().getResource(".key");
                     if (var2x != null) {
                        try {
                           String var24 = new BufferedReader(new InputStreamReader(var2x)).lines().iterator().next();
                           if (var24.length() != 32) {
                              Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                              return;
                           }

                           Core.lastId = var24;
                           Core.this.serverUUID = RunnableMetrics.getServerUUID();
                           int var25 = Core.this.getServer().getOnlinePlayers().size();
                           String var26 = "https://v2.advancedplugins.net/auth/handshake.php?token=%token%&server=%serverUUID%&playerCount=%playerCount%";
                           var26 = var26.replace("%token%", var24);
                           var26 = var26.replace("%serverUUID%", Core.this.serverUUID);
                           var26 = var26.replace("%playerCount%", Integer.toString(var25));
                           URL var30 = new URL(var26);
                           URLConnection var31 = var30.openConnection();
                           var31.setRequestProperty(
                              "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
                           );
                           InputStream var32 = var31.getInputStream();
                           ByteArrayOutputStream var33 = new ByteArrayOutputStream();
                           byte[] var35 = new byte[16384];

                           int var34;
                           while ((var34 = var32.read(var35, 0, var35.length)) != -1) {
                              var33.write(var35, 0, var34);
                           }

                           String var36 = new String(var33.toByteArray(), StandardCharsets.UTF_8);
                           Gson var13 = new Gson();
                           Map var14 = (Map)var13.fromJson(var36, Map.class);
                           Double var15 = (Double)var14.get("code");
                           if (var15 == 1.0) {
                              Core.Bukkit = true;
                              this.conCount = 0;
                           } else {
                              Core.this.getServer().getLogger().warning("[AdvancedJobs] " + var14.get("message"));
                              Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                           }
                        } catch (Exception var17) {
                           if (Core.Bukkit && this.conCount < 2) {
                              Core.this.getLogger().info("Failed to connect to authentication server - retrying later.");
                              this.conCount++;
                           } else {
                              var17.printStackTrace();
                              Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                           }
                        }

                        return;
                     }

                     if (Core.lastId.isEmpty() && var1.isEmpty()) {
                        Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                     }

                     if (var1.length() > 7) {
                        Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                     }

                     try {
                        Integer.parseInt(var1);
                     } catch (Exception var16) {
                        Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                     }

                     Core.lastId = var1;
                     String var3 = "http://servers.advancedmarket.co/jobs/apitest.php?&userId=%id%&minecraftVersion=%mc%&aeVersion=%ae%&re=%re%&playerCount=%playerCount%";
                     String var4 = MinecraftVersion.getVersionNumber() + "";
                     var3 = var3.replace("%mc%", MinecraftVersion.getVersionNumber() >= 1100 ? var4.substring(0, var4.length() - 1) : var4);
                     var3 = var3.replace("%ae%", "1.0.0");
                     var3 = var3.replace("%id%", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(var1));
                     var3 = var3.replace("%re%", Core.Bukkit + "");
                     var3 = var3.replace("%playerCount%", Core.this.getServer().getOnlinePlayers().size() + "");
                     URL var5 = new URL(var3);
                     URLConnection var6 = var5.openConnection();
                     var6.setRequestProperty(
                        "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
                     );
                     InputStream var7 = var6.getInputStream();
                     ByteArrayOutputStream var8 = new ByteArrayOutputStream();
                     byte[] var10 = new byte[16384];

                     int var9;
                     while ((var9 = var7.read(var10, 0, var10.length)) != -1) {
                        var8.write(var10, 0, var9);
                     }

                     String var11 = new String(var8.toByteArray(), StandardCharsets.UTF_8);
                     int var12 = Integer.parseInt(var11.replaceAll("[^0-9]", ""));
                     switch (var12) {
                        case 1:
                           Core.Bukkit = true;
                           return;
                        case 2:
                        default:
                           break;
                        case 3:
                           Core.this.getServer().getLogger().warning("[AdvancedJobs] Could not initialise plugin, are you using Vault?");
                           Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                           return;
                        case 4:
                           Core.this.getServer().getLogger().severe("[AdvancedJobs] Could not initialise plugin, contact developer with code 126");
                           Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                           return;
                        case 5:
                           Core.this.getServer()
                              .getLogger()
                              .severe(
                                 "[AdvancedJobs] Your license exceeded maximum number of unique IPs. Contact the developer for an enterprise license or connect using previously used IPs."
                              );
                           Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
                           return;
                     }
                  } catch (Exception var18) {
                     if (Core.this.lastTime) {
                        Core.this.lastTime = false;
                     } else {
                        Core.this.getServer().getLogger().warning("[AdvancedJobs] Failed to connect to remote server, is your firewall configured properly?");
                     }

                     Core.Bukkit = true;
                     return;
                  }

                  Core.this.getServer()
                     .getLogger()
                     .warning("[AdvancedJobs] Could not initialise plugin, contact developer with code 002, your ID: " + Core.lastId);
                  Core.this.getServer().getPluginManager().disablePlugin(Core.getInstance());
               }
            })
            .runTask(this);
      } catch (Exception var2) {
         var2.printStackTrace();
         this.getServer().getPluginManager().disablePlugin(getInstance());
      }

      if (!Registry.get().equalsIgnoreCase(lastId)) {
         this.getServer().getPluginManager().disablePlugin(this);
      }

      this.creatorHandler = new GUICreatorHandler(this);
      mysql = this.getConfig("config").string("storage-options.storage-method").equalsIgnoreCase("mysql");
   }

   public void onDisable() {
      this.bossBars.forEach(var0 -> var0.forEach((var0x, var1) -> var1.endDisplay()));
      this.unload();
   }

   public void reload() {
      this.getConfigStore().reloadReloadableConfigs();
      this.locale.reload();
      this.unload();
      this.load();
      this.runSync(() -> this.getServer().getPluginManager().callEvent(new JobsReloadEvent()));
   }

   private void unload() {
      this.stop = true;
      this.actionRegistry.unload();
      HandlerList.unregisterAll(this);
      this.getServer().getScheduler().cancelTasks(this);
      this.userCache.save();
      this.userCache.getSubCache().invalidateAll();
      this.userStorage.closeBack();
      this.userCache.getExecutorService().shutdownNow();
      this.resetStorage.save("daily-data", this.jobsResetHandler);
      this.boostersStorage.save("boosters-data", this.boostersController);
      this.jobsResetHandler.stop();
      this.resetStorage.closeBack();
      this.boostersStorage.closeBack();

      for (UUID var2 : this.menuFactory.getOpenMenus().keySet()) {
         Player var3 = this.getServer().getPlayer(var2);
         if (var3 != null) {
            var3.closeInventory();
         }
      }
   }

   private void load() {
      this.stop = false;
      this.setStorageSettings();
      this.userStorage = new UserStorage(this);
      this.resetStorage = new JobsStorage(this);
      this.boostersStorage = new BoostersStorage(this);
      this.rewardCache = new RewardCache(this);
      this.rewardCache.cache();
      this.jobCache = new JobCache(this);
      this.jobController = new JobController(this);
      this.jobCache.cache();
      this.userCache = new UserCache(this);
      this.userCache.loadOnline();
      this.leaderboardController = new LeaderboardController(this);
      this.actionRegistry = new ActionRegistry(this, new ActionsHandler(), false, new HashSet<>());
      this.actionRegistry.setEnablePlaytime(this.getConfig("config").bool("enable-play-time"));
      this.actionRegistry.setBrewingProtection(this.getConfig("config").bool("brewing-protection"));
      this.actionRegistry.setBreakProtection(this.getConfig("config").bool("break-protection"));
      this.getConfig("config").stringList("disabled-plugin-hooks").forEach(this.actionRegistry::disableHook);
      this.actionRegistry.register();
      this.jobsPipeline = new JobsPipeline(this);
      this.menuIllustrator = new MenuIllustrator();
      this.menuFactory = new MenuFactory(this);
      this.reloadJobs();
      this.actionCache = CacheBuilder.newBuilder().expireAfterAccess(20L, TimeUnit.SECONDS).build();
      this.startReloadingBoosters();
      this.getSavingController().addSavable(this.userCache, this.getConfig("config").integer("storage-options.auto-save-interval") * 20);
      this.register();

      for (Player var2 : this.getServer().getOnlinePlayers()) {
         UserConnectListener.getConnectionListener().loadPlayer(var2);
      }

      if (this.creatorHandler != null) {
         this.creatorHandler.loadInventories(this);
      }
   }

   private void register() {
      this.registerAliases();
      this.runSync(() -> {
         this.commandBase = new CommandBase(this);
         this.registerRegistries(new net.advancedplugins.simplespigot.registry.Registry[]{new ArgumentRegistry(this)});
         this.commandBase.registerCommand(new JobCommand(this));
         this.commandBase.registerCommand(new JobAdminCommand(this));
      });
      this.registerListeners(new Listener[]{new MenuListener(), new UserConnectListener(this)});
   }

   private void registerAliases() {
      Field var1;
      CommandMap var2;
      try {
         var1 = org.bukkit.Bukkit.getServer().getClass().getDeclaredField("commandMap");
         var1.setAccessible(true);
         var2 = (CommandMap)var1.get(org.bukkit.Bukkit.getServer());
      } catch (IllegalAccessException | NoSuchFieldException var4) {
         throw new RuntimeException(var4);
      }

      List var3 = this.getConfig("config").list("jobs-command-aliases");
      var3.forEach(var2x -> {
         AliasCommand var3x = new AliasCommand(var2x, this);
         var2.register(this.getName(), var3x);
      });
      var1.setAccessible(false);
   }

   public void startReloadingBoosters() {
      this.boostersController = new BoostersController(this);
      this.refreshBoosters();
      if (this.getConfig("config").bool("storage-options.bungee-fix")) {
         long var1 = this.getConfig("config").integer("storage-options.auto-save-interval") * 20L;
         (new BukkitRunnable() {
            public void run() {
               Core.this.refreshBoosters();
            }
         }).runTaskTimer(this, var1, var1);
      }
   }

   public void refreshBoosters() {
      BoostersController var1 = this.boostersStorage.load("boosters-data");
      if (var1 != null) {
         this.boostersController.refresh(var1);
      }
   }

   private void configRelations() {
      FirstInstall.checkFirstInstall(this, "config.yml", "https://advancedplugins.net/item/AdvancedJobs-UI.189");
      this.getConfigStore()
         .config("config", Path::resolve, true, true)
         .config("rewards", Path::resolve, true)
         .config("portal-menu", (var0, var1) -> var0.resolve("menus").resolve("portal"), true)
         .config("free-jobs-menu", (var0, var1) -> var0.resolve("menus").resolve("free"), true)
         .config("premium-jobs-menu", (var0, var1) -> var0.resolve("menus").resolve("premium"), true)
         .config("community-jobs-menu", (var0, var1) -> var0.resolve("menus").resolve("community"), true)
         .config("progress-menu", (var0, var1) -> var0.resolve("menus").resolve("progress"), true)
         .config("leaderboard-menu", (var0, var1) -> var0.resolve("menus").resolve("leaderboard"), true)
         .common("storage-type", "config", var0 -> var0.string("storage-options.storage-method"));
   }

   private void setStorageSettings() {
      Config var1 = this.getConfig("config");
      StorageSettings var2 = this.getStorageSettings();
      var2.setAddress(var1.string("storage-options.address"));
      var2.setDatabase(var1.string("storage-options.database"));
      var2.setPrefix(var1.string("storage-options.prefix"));
      var2.setUsername(var1.string("storage-options.username"));
      var2.setPassword(var1.string("storage-options.password"));
      var2.setConnectionTimeout(var1.integer("storage-options.pool-settings.connection-timeout"));
      var2.setMaximumLifetime(var1.integer("storage-options.pool-settings.maximum-lifetime"));
      var2.setMaximumPoolSize(var1.integer("storage-options.pool-settings.maximum-pool-size"));
      var2.setMinimumIdle(var1.integer("storage-options.pool-settings.minimum-idle"));
      HashMap var3 = Maps.newHashMap();

      for (String var5 : var1.stringList("storage-options.connection-properties")) {
         var3.put(var5, var1.forcedString("storage-options.connection-properties.".concat(var5)));
      }

      var2.setProperties(var3);
   }

   public Config getConfig(String var1) {
      return this.getConfigStore().getConfig(var1);
   }

   public void reloadJobs() {
      this.jobsResetHandler = this.resetStorage.load("daily-data");
      this.jobsResetHandler = this.jobsResetHandler == null ? new JobsResetHandler(this, new LinkedList<>(), new LinkedList<>()) : this.jobsResetHandler;
      this.jobsResetHandler.start();
   }

   public LocaleHandler getLocale() {
      return this.locale;
   }

   public Cache<String, Map<Integer, List<Action>>> getActionCache() {
      return this.actionCache;
   }

   public ActionRegistry getActionRegistry() {
      return this.actionRegistry;
   }

   public JobController getJobController() {
      return this.jobController;
   }

   public JobsResetHandler getJobsResetHandler() {
      return this.jobsResetHandler;
   }

   public Storage<JobsResetHandler> getResetStorage() {
      return this.resetStorage;
   }

   public JobCache getJobCache() {
      return this.jobCache;
   }

   public RewardCache getRewardCache() {
      return this.rewardCache;
   }

   public JobsPipeline getJobsPipeline() {
      return this.jobsPipeline;
   }

   public BoostersController getBoostersController() {
      return this.boostersController;
   }

   public Storage<BoostersController> getBoostersStorage() {
      return this.boostersStorage;
   }

   public UserCache getUserCache() {
      return this.userCache;
   }

   public Storage<User> getUserStorage() {
      return this.userStorage;
   }

   public MenuIllustrator getMenuIllustrator() {
      return this.menuIllustrator;
   }

   public MenuFactory getMenuFactory() {
      return this.menuFactory;
   }

   public List<Map<UUID, BossBar>> getBossBars() {
      return this.bossBars;
   }

   public GUICreatorHandler getCreatorHandler() {
      return this.creatorHandler;
   }

   public CommandBase getCommandBase() {
      return this.commandBase;
   }

   public LeaderboardController getLeaderboardController() {
      return this.leaderboardController;
   }

   public boolean isStop() {
      return this.stop;
   }

   public String getServerUUID() {
      return this.serverUUID;
   }

   public boolean isLastTime() {
      return this.lastTime;
   }

   public void setLocale(LocaleHandler var1) {
      this.locale = var1;
   }

   public void setActionCache(Cache<String, Map<Integer, List<Action>>> var1) {
      this.actionCache = var1;
   }

   public void setActionRegistry(ActionRegistry var1) {
      this.actionRegistry = var1;
   }

   public void setJobController(JobController var1) {
      this.jobController = var1;
   }

   public void setJobsResetHandler(JobsResetHandler var1) {
      this.jobsResetHandler = var1;
   }

   public void setResetStorage(Storage<JobsResetHandler> var1) {
      this.resetStorage = var1;
   }

   public void setJobCache(JobCache var1) {
      this.jobCache = var1;
   }

   public void setRewardCache(RewardCache var1) {
      this.rewardCache = var1;
   }

   public void setJobsPipeline(JobsPipeline var1) {
      this.jobsPipeline = var1;
   }

   public void setBoostersController(BoostersController var1) {
      this.boostersController = var1;
   }

   public void setBoostersStorage(Storage<BoostersController> var1) {
      this.boostersStorage = var1;
   }

   public void setUserCache(UserCache var1) {
      this.userCache = var1;
   }

   public void setUserStorage(Storage<User> var1) {
      this.userStorage = var1;
   }

   public void setMenuIllustrator(MenuIllustrator var1) {
      this.menuIllustrator = var1;
   }

   public void setMenuFactory(MenuFactory var1) {
      this.menuFactory = var1;
   }

   public void setBossBars(List<Map<UUID, BossBar>> var1) {
      this.bossBars = var1;
   }

   public void setCreatorHandler(GUICreatorHandler var1) {
      this.creatorHandler = var1;
   }

   public void setCommandBase(CommandBase var1) {
      this.commandBase = var1;
   }

   public void setLeaderboardController(LeaderboardController var1) {
      this.leaderboardController = var1;
   }

   public void setStop(boolean var1) {
      this.stop = var1;
   }

   public void setServerUUID(String var1) {
      this.serverUUID = var1;
   }

   public void setLastTime(boolean var1) {
      this.lastTime = var1;
   }

   public static Core getInstance() {
      return instance;
   }
}
