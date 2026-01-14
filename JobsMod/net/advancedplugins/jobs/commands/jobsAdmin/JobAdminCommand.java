package net.advancedplugins.jobs.commands.jobsAdmin;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.commands.SimpleCommand;
import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.locale.LocaleHandler;
import org.bukkit.command.CommandSender;

public class JobAdminCommand extends SimpleCommand<CommandSender> {
   private final LocaleHandler locale;

   public JobAdminCommand(Core var1) {
      super(var1, "advancedjobsadmin", "jobs.admin", true);
      this.locale = var1.getLocale();
      this.setSubCommands(
         new SubCommand[]{
            new ReloadSub(var1),
            new DeleteUserSub(var1),
            new GivePointsSub(var1),
            new SetPointsSub(var1),
            new RefreshJobsSub(var1),
            new ResetSub(var1),
            new QuestEditorSub(var1),
            new SyncJobsSub(var1),
            new LevelUpSub(var1),
            new BoosterAddCommand(var1),
            new BoosterClearCommand(var1)
         }
      );
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      Text.sendMessage(var1, this.locale.getString("admin-cmd.main"));
   }
}
