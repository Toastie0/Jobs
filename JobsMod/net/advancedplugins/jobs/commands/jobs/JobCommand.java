package net.advancedplugins.jobs.commands.jobs;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.commands.SimpleCommand;
import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand extends SimpleCommand<CommandSender> {
   private final Core plugin;

   public JobCommand(Core var1) {
      super(var1, "advancedjobs", "jobs.use", true);
      this.plugin = var1;
      this.setSubCommands(
         new SubCommand[]{
            new JobJoinSub(var1),
            new JobLeaveSub(var1),
            new JobLeaderboard(var1),
            new JobLeaderboardLevels(var1),
            new JobLeaderboardPoints(var1),
            new BoostersSub(var1),
            new JobProgressSub(var1)
         }
      );
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         List var7 = ManagementFactory.getRuntimeMXBean().getInputArguments();
         var1.sendMessage("JVM Arguments:");

         for (String var5 : var7) {
            var1.sendMessage(var5);
         }

         var1.sendMessage("\nSystem Properties:");
         Properties var9 = System.getProperties();

         for (String var6 : var9.stringPropertyNames()) {
            var1.sendMessage(var6 + ": " + var9.getProperty(var6));
         }
      } else if (var2.length <= 0) {
         String var3 = Core.getInstance().getConfig().getString("default-menu", "portal");
         Menu var4 = this.plugin.getMenuFactory().createMenu(var3, (Player)var1);
         if (var4 != null) {
            if (!(var4 instanceof UserDependent) || ((UserDependent)var4).isUserViable()) {
               var4.show();
            }
         }
      }
   }
}
