package net.advancedplugins.jobs.impl.actions.external;

import net.Indyuce.mmocore.api.event.PlayerExperienceGainEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MMOCoreQuests extends ActionQuestExecutor {
   public MMOCoreQuests(JavaPlugin var1) {
      super(var1, "MMOCore");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerLevelUp(PlayerLevelUpEvent var1) {
      int var2 = var1.getNewLevel() - var1.getOldLevel();
      String var3 = var1.hasProfession() ? var1.getProfession().getId() : "";
      String var4 = this.getXpTable(var1.getProfession());
      this.executionBuilder("level_up").player(var1.getPlayer()).root(var3).subRoot("xp-table", var4).progress(var2).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerExperienceGain(PlayerExperienceGainEvent var1) {
      int var2 = (int)Math.round(var1.getExperience());
      if (var2 < 1) {
         var2 = 1;
      }

      String var3 = var1.hasProfession() ? var1.getProfession().getId() : "";
      String var4 = this.getXpTable(var1.getProfession());
      EXPSource var5 = var1.getSource() == null ? EXPSource.OTHER : var1.getSource();
      this.executionBuilder("gain_exp")
         .player(var1.getPlayer())
         .root(var3)
         .subRoot("source", var5.name())
         .subRoot("xp-table", var4)
         .progress(var2)
         .buildAndExecute();
   }

   private String getXpTable(Profession var1) {
      return var1 != null && var1.hasExperienceTable() ? var1.getExperienceTable().getId() : "";
   }
}
