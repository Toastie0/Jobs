package net.advancedplugins.jobs.commands.jobs;

import java.util.HashMap;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.controller.BoostersController;
import org.bukkit.entity.Player;

public class BoostersSub extends JobSubCommand<Player> {
   public BoostersSub(Core var1) {
      super(var1, "jobs.use");
      this.addFlat("boosters");
   }

   public void onExecute(Player var1, String[] var2) {
      BoostersController var3 = this.plugin.getBoostersController();
      this.locale.to("boosters.command.server", var0 -> var0, var1);
      var3.getServerBoosters()
         .forEach(
            (var3x, var4) -> var4.forEach(
               var3xx -> this.locale
                  .to(
                     "boosters.command.booster",
                     var3xxx -> var3xxx.set("booster", var3xx.getPercent())
                        .set("type", this.locale.getString("boosters." + var3xx.getType().name().toLowerCase()))
                        .set("time", var3.getFormattedExpirationDate(var3xx))
                        .set("affects", var3.formatBoosterAffects(var3xx)),
                     var1
                  )
            )
         );
      this.locale.to("boosters.command.player", var0 -> var0, var1);
      var3.getPlayerBoosters()
         .getOrDefault(var1.getUniqueId(), new HashMap<>())
         .forEach(
            (var3x, var4) -> var4.forEach(
               var3xx -> this.locale
                  .to(
                     "boosters.command.booster",
                     var3xxx -> var3xxx.set("booster", var3xx.getPercent())
                        .set("type", this.locale.getString("boosters." + var3xx.getType().name().toLowerCase()))
                        .set("time", var3.getFormattedExpirationDate(var3xx))
                        .set("affects", var3.formatBoosterAffects(var3xx)),
                     var1
                  )
            )
         );
      this.locale.to("boosters.command.permission", var0 -> var0, var1);
      var3.getPermissionBoosters()
         .forEach(
            (var3x, var4) -> var4.forEach(
               (var3xx, var4x) -> {
                  if (var1.hasPermission(var3xx)) {
                     this.locale
                        .to(
                           "boosters.command.booster",
                           var3xxx -> var3xxx.set("booster", var4x.getPercent())
                              .set("type", this.locale.getString("boosters." + var4x.getType().name().toLowerCase()))
                              .set("time", "∞")
                              .set("affects", var3.formatBoosterAffects(var4x)),
                           var1
                        );
                  }
               }
            )
         );
   }
}
