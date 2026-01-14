package net.advancedplugins.jobs.commands.jobsAdmin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class BoosterAddCommand extends JobSubCommand<CommandSender> {
   public BoosterAddCommand(Core var1) {
      super(var1);
      this.inheritPermission();
      this.addFlats(new String[]{"boosters", "add"});
      this.addArgument(OfflinePlayer.class, "player/all", var0 -> {
         List var1x = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
         var1x.add(0, "all");
         return var1x;
      }, new String[0]);
      this.addArgument(
         BoostersController.BoosterType.class,
         "type",
         var0 -> Arrays.stream(BoostersController.BoosterType.values()).map(Enum::name).collect(Collectors.toList()),
         new String[0]
      );
      this.addArgument(Double.class, "percent", new String[0]);
      this.addArgument(Double.class, "hours", new String[0]);
      this.addArgument(String.class, "affects", new String[0]);
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      OfflinePlayer var3 = this.parseArgument(var2, 2);
      Optional var4 = this.parseArgument(var2, 3);
      double var5 = this.<Double>parseArgument(var2, 4);
      double var7 = this.<Double>parseArgument(var2, 5);
      String var9 = this.parseArgument(var2, 6);
      BoostersController.BoosterTarget var10 = var3.getName().equalsIgnoreCase("all")
         ? BoostersController.BoosterTarget.SERVER
         : BoostersController.BoosterTarget.PLAYER;
      if (var4.isPresent() && !(var5 <= 0.0) && !(var7 <= 0.0)) {
         BoostersController.Booster var11 = new BoostersController.Booster(
            var1.getName(), var5, var9, System.currentTimeMillis() + Math.round(var7 * 60.0 * 60.0 * 1000.0), (BoostersController.BoosterType)var4.get(), var10
         );
         this.plugin.getBoostersController().addBooster(var11, var10, var3.getUniqueId());
         var1.sendMessage(this.locale.getString("boosters.updated"));
      } else {
         var1.sendMessage(Text.modify("&cUsage: /aja boosters add <player/all> <PROGRESS/POINTS/REWARDS> <percent> <hours> <affects>"));
      }
   }
}
