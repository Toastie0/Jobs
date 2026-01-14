package net.advancedplugins.jobs.commands.jobsAdmin;

import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.controller.BoostersController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class BoosterClearCommand extends JobSubCommand<CommandSender> {
   public BoosterClearCommand(Core var1) {
      super(var1);
      this.inheritPermission();
      this.addFlats(new String[]{"boosters", "clear"});
      this.addArgument(OfflinePlayer.class, "player/all", var0 -> {
         List var1x = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
         var1x.add(0, "all");
         return var1x;
      }, new String[0]);
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      OfflinePlayer var3 = this.parseArgument(var2, 2);
      BoostersController.BoosterTarget var4 = var3.getName().equalsIgnoreCase("all")
         ? BoostersController.BoosterTarget.SERVER
         : BoostersController.BoosterTarget.PLAYER;
      this.plugin.getBoostersController().clearBoosters(var4, var3.getUniqueId());
      var1.sendMessage(this.locale.getString("boosters.updated"));
   }
}
