package net.advancedplugins.jobs.registry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.impl.utils.commands.CommandBase;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.simplespigot.registry.Registry;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArgumentRegistry implements Registry {
   private final CommandBase commandBase;
   private final UserCache userCache;
   private final JobCache jobCache;

   public ArgumentRegistry(Core var1) {
      this.commandBase = var1.getCommandBase();
      this.userCache = var1.getUserCache();
      this.jobCache = var1.getJobCache();
   }

   @Override
   public void register() {
      this.commandBase
         .registerArgumentType(User.class, var1 -> {
            Player var2 = Bukkit.getPlayerExact(var1);
            return var2 == null ? Optional.empty() : this.userCache.getSync(var2.getUniqueId());
         })
         .registerArgumentType(Job.class, var1 -> Optional.ofNullable(this.jobCache.getJob(var1)))
         .registerArgumentType(BigInteger.class, var0 -> {
            try {
               return new BigInteger(var0);
            } catch (NumberFormatException var2) {
               return null;
            }
         })
         .registerArgumentType(BoostersController.BoosterType.class, var0 -> {
            Optional var1 = Optional.empty();

            try {
               var1 = Optional.of(BoostersController.BoosterType.valueOf(var0.toUpperCase()));
            } catch (Exception var3) {
            }

            return var1;
         })
         .registerArgumentType(Double.class, var0 -> NumberUtils.isNumber(var0) ? Double.parseDouble(var0) : 0.0)
         .registerArgumentType(BigDecimal.class, var0 -> {
            try {
               return new BigDecimal(var0);
            } catch (NumberFormatException var2) {
               return null;
            }
         });
   }
}
