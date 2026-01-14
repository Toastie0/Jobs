package net.advancedplugins.jobs.commands.jobs;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class JobLeaderboard extends JobSubCommand<Player> {
   private final Core core;

   public JobLeaderboard(Core var1) {
      super(var1);
      this.core = var1;
      this.inheritPermission();
      this.addFlats(new String[]{"leaderboard"});
   }

   public void onExecute(Player var1, String[] var2) {
      Menu var3 = this.plugin.getMenuFactory().createMenu("leaderboard-menu", var1);
      if (var3 != null) {
         if (!(var3 instanceof UserDependent) || ((UserDependent)var3).isUserViable()) {
            var3.show();
         }
      }
   }
}
