package net.advancedplugins.jobs.commands.jobs;

import java.util.Optional;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.objects.job.Job;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JobLeaderboardLevels extends JobSubCommand<Player> {
   private final Core core;

   public JobLeaderboardLevels(Core var1) {
      super(var1);
      this.core = var1;
      this.inheritPermission();
      this.addFlats(new String[]{"leaderboard", "levels"});
      this.addArgument(Job.class, "job", var1x -> this.core.getJobCache().getAllJobs().stream().map(Job::getId).collect(Collectors.toList()), new String[0]);
   }

   public void onExecute(Player var1, String[] var2) {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
         Bukkit.getLogger().warning("To enable leaderboard, please install PlaceholderAPI");
         var1.sendMessage(Text.modify("Leaderboard is not supported on this server!"));
      } else {
         Job var3 = this.<Optional<Job>>parseArgument(var2, 2).orElse(null);
         if (var3 != null) {
            Text.modify(
                  this.core.getLocale().getStringList("leaderboard-command.levels-leaderboard"),
                  var2x -> var2x.set("job", var3.getId()).set("job_name", var3.getName()).tryAddPapi(var1)
               )
               .forEach(var1::sendMessage);
         }
      }
   }
}
