package net.advancedplugins.jobs.menus.menus;

import java.util.Map;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.jobs.menus.service.extensions.ConfigMenu;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.entity.Player;

public class PortalMenu extends ConfigMenu implements UserDependent {
   private final User user;
   private final LocaleHandler locale;
   private final Config settings;
   private final JobCache jobCache;
   private final JobController controller;
   private final JobsResetHandler jobsResetHandler;

   public PortalMenu(Core var1, Config var2, Player var3) {
      super(var1, var2, var3);
      this.locale = var1.getLocale();
      this.settings = var1.getConfig("config");
      this.jobCache = var1.getJobCache();
      this.controller = var1.getJobController();
      this.user = var1.getUserCache().getOrThrow(var3.getUniqueId());
      this.jobsResetHandler = var1.getJobsResetHandler();
   }

   @Override
   public void redraw() {
      this.drawConfigItems(
         var1 -> {
            Map var2 = this.controller.getActiveJobs(this.user);
            String var3 = String.join(
               "&7, ",
               var2.values().stream().map(var1x -> this.jobCache.getJob(var1x.getJob())).map(var0 -> "&f" + var0.getName()).collect(Collectors.toList())
            );
            if (var3.isEmpty()) {
               var3 = "&f-";
            }

            return var1.set("points", String.valueOf(this.user.getPoints()))
               .set("my_jobs", var3.toString())
               .set("active", var2.size())
               .set("limit", this.controller.getMaxJobs(this.player))
               .set("time_left", this.jobsResetHandler.asString())
               .tryAddPapi(this.player);
         }
      );
   }

   @Override
   public boolean isUserViable() {
      return this.user != null;
   }
}
