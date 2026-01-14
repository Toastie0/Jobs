package net.advancedplugins.jobs.impl.actions.external;

import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import com.sucy.skill.api.event.PlayerManaLossEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import com.sucy.skill.api.skills.Skill;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillApiQuests extends ActionQuestExecutor {
   public SkillApiQuests(JavaPlugin var1) {
      super(var1, "skillapi");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDamageDeal(TrueDamageEvent var1) {
      if (var1.getDamager() instanceof Player) {
         Player var2 = (Player)var1.getDamager();
         int var3 = (int)Math.round(var1.getDamage());
         Skill var4 = var1.getSkill();
         String var5 = var4.getName();
         int var6 = var4.getCombo();
         this.execute("damage", var2, var3, var1x -> var1x.root(var5));
         this.execute("combo", var2, var6, var1x -> var1x.root(var5), var0 -> var0, true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onSkillUpgrade(PlayerSkillUpgradeEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      String var3 = var1.getUpgradedSkill().getData().getName();
      this.execute("upgrade", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onSkillUnlock(PlayerSkillUnlockEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      String var3 = var1.getUnlockedSkill().getData().getName();
      this.execute("unlock", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onManaLoss(PlayerManaLossEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      int var3 = (int)Math.round(var1.getAmount());
      ManaCost var4 = var1.getSource();
      this.execute("loose_mana", var2, var3, var1x -> var1x.root(var4.toString()));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLevelUp(PlayerLevelUpEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      int var3 = var1.getLevel();
      this.execute("reach_level", var2, var3, var0 -> var0, var0 -> var0, true);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onClassChange(PlayerClassChangeEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      String var3 = var1.getNewClass().getName();
      this.execute("change_class", var2, var1x -> var1x.root(var3));
   }
}
