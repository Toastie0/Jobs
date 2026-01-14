package net.advancedplugins.jobs.commands.jobs;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JobLeaderboardPoints extends JobSubCommand<Player> {
   private final Core core;

   public JobLeaderboardPoints(Core var1) {
      super(var1);
      this.core = var1;
      this.inheritPermission();
      this.addFlats(new String[]{"leaderboard", "points"});
   }

   public void onExecute(Player var1, String[] var2) {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
         Bukkit.getLogger().warning("To enable leaderboard, please install PlaceholderAPI");
         var1.sendMessage(Text.modify("Leaderboard is not supported on this server!"));
      } else {
         Text.modify(this.core.getLocale().getStringList("leaderboard-command.points-leaderboard"), var1x -> var1x.tryAddPapi(var1)).forEach(var1::sendMessage);
      }
   }
}
