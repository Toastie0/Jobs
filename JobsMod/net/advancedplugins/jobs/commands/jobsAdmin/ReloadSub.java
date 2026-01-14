package net.advancedplugins.jobs.commands.jobsAdmin;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.command.CommandSender;

public class ReloadSub extends JobSubCommand<CommandSender> {
   private Core plugin;

   public ReloadSub(Core var1) {
      super(var1, "jobs.admin", true);
      this.plugin = var1;
      this.addFlatWithAliases("reload", new String[]{"rl"});
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      this.plugin.reload();
      Text.sendMessage(var1, this.locale.getString("admin-cmd.reload"));
   }
}
