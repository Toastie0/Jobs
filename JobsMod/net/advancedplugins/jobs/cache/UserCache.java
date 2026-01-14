package net.advancedplugins.jobs.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.exceptions.NoOnlineUserException;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.impl.utils.uuid.FastUuid;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.simplespigot.cache.FutureCache;
import net.advancedplugins.simplespigot.save.Savable;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UserCache extends FutureCache<UUID, User> implements Savable {
   private final Core plugin;
   private final Storage<User> storage;
   private final JobCache jobCache;
   private final JobController jobController;
   private final ExecutorService executorService = Executors.newFixedThreadPool(50);

   public UserCache(Core var1) {
      super(var1);
      this.plugin = var1;
      this.storage = var1.getUserStorage();
      this.jobCache = var1.getJobCache();
      this.jobController = var1.getJobController();
   }

   public User getOrThrow(UUID var1) {
      User var2 = this.getSync(var1).orElse(null);
      if (var2 == null) {
         Player var3 = Bukkit.getPlayer(var1);
         if (var3 != null && var3.isOnline()) {
            Text.sendMessage(var3, "&ePlease re-log for this feature to work. We're sorry for the inconvenience.");
            return null;
         } else {
            throw new NoOnlineUserException("Could not find an online user with the uuid ".concat(FastUuid.toString(var1)));
         }
      } else {
         this.applyPermissionComputation(var2);
         return var2;
      }
   }

   public CompletableFuture<User> load(UUID var1) {
      return this.get(var1).thenApplyAsync(var2 -> {
         User var3 = var2.orElseGet(() -> {
            User var2x = this.storage.load(FastUuid.toString(var1));
            if (var2x == null) {
               var2x = new User(var1);
            }

            this.set(var1, var2x);
            return var2x;
         });
         this.applyPermissionComputation(var3);
         return var3;
      }, this.executorService).exceptionally(var0 -> {
         var0.printStackTrace();
         return null;
      });
   }

   public void asyncModifyMultiple(Consumer<User> var1, Set<UUID> var2) {
      for (UUID var4 : var2) {
         this.load(var4).thenAccept(var1);
      }
   }

   public void asyncModifyAll(Consumer<User> var1) {
      this.plugin.runAsync(() -> {
         for (User var3 : this.values()) {
            var1.accept(var3);
         }

         for (User var5 : this.storage.loadAll()) {
            if (var5 != null && !this.keySet().contains(var5.getUuid())) {
               var1.accept(var5);
               this.storage.save(var5.getUuid().toString(), var5);
            }
         }
      });
   }

   public List<User> getAll() {
      ArrayList var1 = new ArrayList<>(this.values());
      var1.addAll(this.storage.loadAll().stream().filter(var1x -> !this.keySet().contains(var1x.getUuid())).collect(Collectors.toList()));
      return var1;
   }

   public void unload(UUID var1, boolean var2) {
      this.get(var1).thenAccept(var3 -> {
         var3.ifPresent(var1xx -> this.storage.save(FastUuid.toString(var1xx.getUuid()), var1xx));
         if (var2) {
            this.invalidate(var1);
         }
      });
   }

   public void loadOnline() {
      for (Player var2 : Bukkit.getOnlinePlayers()) {
         this.load(var2.getUniqueId());
      }
   }

   @Override
   public void save() {
      if (!this.plugin.isStop()) {
         this.plugin.runAsync(() -> {
            for (User var2x : this.values()) {
               this.storage.save(FastUuid.toString(var2x.getUuid()), var2x);
            }
         });
      } else {
         for (User var2 : this.values()) {
            this.storage.save(FastUuid.toString(var2.getUuid()), var2);
         }
      }
   }

   private void applyPermissionComputation(User var1) {
      Player var2 = Bukkit.getPlayer(var1.getUuid());
      if (var2 != null) {
         if (!var1.getPassId().equals("premium") && var2.hasPermission("advancedjobs.premium")) {
            var1.setPremium(true);
         } else if (var1.getPassId().equals("premium") && !var2.hasPermission("advancedjobs.premium")) {
            var1.setPremium(false);
            this.jobController.getActiveJobs(var1).keySet().forEach(var2x -> {
               Job var3 = this.jobCache.getJob(var2x);
               if (var3.isPremiumJob()) {
                  UserJobInfo var4 = this.jobController.getJobInfo(var1, var3);
                  var4.setActive(false);
               }
            });
         }
      }
   }

   public ExecutorService getExecutorService() {
      return this.executorService;
   }
}
