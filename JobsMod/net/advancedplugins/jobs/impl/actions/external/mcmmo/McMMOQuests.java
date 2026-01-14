package net.advancedplugins.jobs.impl.actions.external.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class McMMOQuests extends ActionQuestExecutor {
   public McMMOQuests(JavaPlugin var1) {
      super(var1, "McMMO");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onExpGain(McMMOPlayerXpGainEvent var1) {
      Player var2 = var1.getPlayer();
      int var3 = (int)var1.getRawXpGained();
      PrimarySkillType var4 = var1.getSkill();
      this.executionBuilder("gain_exp").player(var2).root(var4.name()).progress(var3).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLevelUp(McMMOPlayerLevelUpEvent var1) {
      Player var2 = var1.getPlayer();
      int var3 = var1.getSkillLevel();
      PrimarySkillType var4 = var1.getSkill();
      this.executionBuilder("level_up_skill").player(var2).root(var4.name()).progressSingle().buildAndExecute();
      this.executionBuilder("level_total_skill").player(var2).root(var4.name()).progress(var3).overrideUpdate().buildAndExecute();
   }
}
