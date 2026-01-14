package net.advancedplugins.jobs.commands.jobsAdmin;

import java.util.Optional;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class LevelUpSub extends JobSubCommand<CommandSender> {
   private final Core plugin;

   public LevelUpSub(Core var1) {
      super(var1, true);
      this.plugin = var1;
      this.inheritPermission();
      this.addFlats(new String[]{"levelup"});
      this.addArgument(
         User.class, "player", var0 -> Bukkit.getOnlinePlayers().stream().<String>map(OfflinePlayer::getName).collect(Collectors.toList()), new String[0]
      );
      this.addArgument(Job.class, "job", var1x -> var1.getJobCache().getAllJobs().stream().map(Job::getId).collect(Collectors.toList()), new String[0]);
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      Optional var3 = this.parseArgument(var2, 1);
      Optional var4 = this.parseArgument(var2, 2);
      if (!var3.isPresent()) {
         Text.sendMessage(var1, Text.modify(this.locale.getString("admin-cmd.could-not-find-user"), var1x -> var1x.set("player", var2[1])));
      } else if (!var4.isPresent()) {
         Text.sendMessage(var1, Text.modify(this.locale.getString("admin-cmd.could-not-find-job"), var1x -> var1x.set("job", var2[2])));
      } else {
         this.plugin.getJobController().levelUp((User)var3.get(), (Job)var4.get());
         Text.sendMessage(var1, this.locale.getString("admin-cmd.levelup"));
      }
   }
}
