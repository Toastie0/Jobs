package net.advancedplugins.jobs.commands.jobsAdmin;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import org.bukkit.command.CommandSender;

public class SyncJobsSub extends JobSubCommand<CommandSender> {
   public SyncJobsSub(Core var1) {
      super(var1);
      this.inheritPermission();
      this.addFlats(new String[]{"sync", "jobs"});
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      boolean var3 = this.plugin.getJobsResetHandler().refreshBungee();
      if (var3) {
         var1.sendMessage(this.locale.getString("jobs-sync"));
      } else {
         var1.sendMessage(this.locale.getString("cant-sync"));
      }
   }
}
