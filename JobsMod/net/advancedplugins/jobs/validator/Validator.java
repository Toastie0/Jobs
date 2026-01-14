package net.advancedplugins.jobs.validator;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import org.bukkit.entity.Player;

public class Validator {
   public static void fixUser(User var0) {
      ConcurrentHashMap var1 = var0.getJobStore().asMap();
      Player var2 = var0.getPlayer();
      JobCache var3 = Core.getInstance().getJobCache();
      int var4 = Core.getInstance().getConfig("config").getConfiguration().getInt("max-level", -1);
      int var5 = var4 == -1 ? Integer.MAX_VALUE : var4;
      var1.keySet().forEach(var4x -> {
         if (!validateJob(var4x)) {
            var1.remove(var4x);
         }

         Job var5x = var3.getJob(var4x);
         if (var5x.getRequiredPermission() != null && !var2.hasPermission(var5x.getRequiredPermission())) {
            var1.remove(var4x);
         }

         UserJobInfo var6 = (UserJobInfo)var1.get(var4x);
         if (var6.getProgress().compareTo(var5x.getRequiredProgress(var6.getLevel())) > 0) {
            var6.setProgress(var5x.getRequiredProgress(var6.getLevel()).subtract(BigDecimal.ONE));
         }

         if (var6.getLevel() > var5) {
            var6.setLevel(var5);
         }
      });
   }

   public static boolean validateJob(String var0) {
      JobCache var1 = Core.getInstance().getJobCache();
      return var1.getJob(var0) != null;
   }
}
