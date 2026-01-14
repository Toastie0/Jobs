package net.advancedplugins.jobs.commands.jobsAdmin;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSub extends JobSubCommand<CommandSender> {
   private final Map<UUID, Long> confirmations = Maps.newHashMap();
   private final UserCache userCache;

   public ResetSub(Core var1) {
      super(var1, true);
      this.userCache = var1.getUserCache();
      this.inheritPermission();
      this.addFlats(new String[]{"reset", "jobs"});
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      UUID var3 = var1 instanceof Player ? ((Player)var1).getUniqueId() : null;
      if (var3 != null && (!this.confirmations.containsKey(var3) || System.currentTimeMillis() - this.confirmations.get(var3) >= 30000L)) {
         this.confirmations.put(var3, System.currentTimeMillis());
         Text.sendMessage(var1, this.locale.getString("admin-cmd.confirm"));
      } else {
         this.userCache.asyncModifyAll(var0 -> {
            var0.getJobStore().clear();
            var0.setPoints(0.0);
            var0.getJobStore().clear();
         });
         Text.sendMessage(var1, this.locale.getString("admin-cmd.jobs-reset"));
      }
   }
}
