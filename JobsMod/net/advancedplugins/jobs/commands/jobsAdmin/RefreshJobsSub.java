package net.advancedplugins.jobs.commands.jobsAdmin;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import org.bukkit.command.CommandSender;

public class RefreshJobsSub extends JobSubCommand<CommandSender> {
   public RefreshJobsSub(Core var1) {
      super(var1);
      this.inheritPermission();
      this.addFlats(new String[]{"refresh", "jobs"});
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      boolean var3 = this.plugin.getJobsResetHandler().reset();
      if (!var3) {
         var1.sendMessage(this.locale.getString("cant-reset"));
      }
   }
}
