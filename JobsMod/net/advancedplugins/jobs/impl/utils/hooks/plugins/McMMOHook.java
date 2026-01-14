package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.SkillAPI;
import com.gmail.nossr50.api.TreeFellerBlockBreakEvent;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakePlayerFishEvent;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.SchedulerUtils;
import net.advancedplugins.jobs.impl.utils.abilities.SmeltMaterial;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class McMMOHook extends PluginHookInstance implements Listener {
   public static final Map<Block, Long> treeFellerTelepathyBlocks = new HashMap<>();

   public McMMOHook() {
      SchedulerUtils.runTaskTimer(() -> {
         long var0 = System.currentTimeMillis();
         treeFellerTelepathyBlocks.entrySet().removeIf(var2 -> {
            if (var0 - var2.getValue() > 500L) {
               var2.getKey().removeMetadata("ae-mcmmo-treefeller-tpdrops", ASManager.getInstance());
               return true;
            } else {
               return false;
            }
         });
      }, 20L, 20L);
   }

   public int getSkillLevel(Player var1, String var2) {
      return ExperienceAPI.getLevel(var1, var2);
   }

   public void addSkillExperience(Player var1, String var2, int var3) {
      ExperienceAPI.addXP(var1, var2, var3, "UNKNOWN");
   }

   public boolean isBleeding(Player var1) {
      return AbilityAPI.isBleeding(var1);
   }

   public List<String> getSkills() {
      return SkillAPI.getSkills();
   }

   public boolean isTreeFellerEvent(Event var1) {
      return var1 instanceof TreeFellerBlockBreakEvent;
   }

   public boolean isFakeBlockBreak(Event var1) {
      return false;
   }

   public boolean isFakeFishEvent(Event var1) {
      return var1 instanceof FakePlayerFishEvent;
   }

   public boolean callFakeEvent(Block var1, Player var2) {
      return true;
   }

   public boolean herbalismCheck(Player var1, BlockBreakEvent var2) {
      Block var3 = var2.getBlock();
      BlockState var4 = var3.getState();
      if (BlockUtils.affectedByGreenTerra(var4) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(var1, PrimarySkillType.HERBALISM)) {
         this.processHerbalismBlockBreakEvent(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void processHerbalismBlockBreakEvent(Player var1, BlockBreakEvent var2) {
      McMMOPlayer var3 = UserManager.getPlayer(var1);
      if (var3 != null) {
         var3.getHerbalismManager().processHerbalismBlockBreakEvent(var2);
      }
   }

   public void processBlockBreakEvent(Player var1, BlockBreakEvent var2, boolean var3, boolean var4) {
      Block var5 = var2.getBlock();
      BlockState var6 = var5.getState();
      ItemStack var7 = var1.getInventory().getItemInMainHand();
      if (BlockUtils.affectedBySuperBreaker(var6)
         && (ItemUtils.isPickaxe(var7) || ItemUtils.isHoe(var7))
         && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(var1, PrimarySkillType.MINING)
         && mcMMO.getChunkManager().isEligible(var6)) {
         this.miningCheck(var1, var2, var3, var4);
      }
   }

   private void miningCheck(Player var1, BlockBreakEvent var2, boolean var3, boolean var4) {
      Block var5 = var2.getBlock();
      BlockState var6 = var5.getState();
      McMMOPlayer var7 = UserManager.getPlayer(var1);
      if (var7 != null) {
         if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_DOUBLE_DROPS, var7)) {
            boolean var8 = var7.getAbilityMode(mcMMO.p.getSkillTools().getSuperAbility(PrimarySkillType.MINING))
               && mcMMO.p.getAdvancedConfig().getAllowMiningTripleDrops();
            BlockUtils.markDropsAsBonus(var5, var8);
            if (var5.getMetadata("mcMMO: Double Drops").size() > 0) {
               BonusDropMeta var9 = (BonusDropMeta)var5.getMetadata("mcMMO: Double Drops").get(0);
               int var10 = var9.asInt();

               for (ItemStack var12 : var5.getDrops()) {
                  for (int var13 = 0; var13 < var10; var13++) {
                     if (var3) {
                        var6.setMetadata("ae_mcmmoTP_DROPS", new FixedMetadataValue(ASManager.getInstance(), true));
                     }

                     if (var4) {
                        var6.setMetadata("ae_mcmmoSMELT", new FixedMetadataValue(ASManager.getInstance(), true));
                     }

                     ItemUtils.spawnItems(var2.getPlayer(), var6.getLocation(), var12, var12.getAmount(), ItemSpawnReason.BONUS_DROPS);
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onBonusDrop(McMMOItemSpawnEvent var1) {
      if (var1.getItemSpawnReason() == ItemSpawnReason.BONUS_DROPS) {
         Block var2 = var1.getLocation().getBlock();
         if (var2.hasMetadata("ae_mcmmoSMELT")) {
            var2.removeMetadata("ae_mcmmoSMELT", ASManager.getInstance());
            ItemStack var3 = SmeltMaterial.material(var1.getItemStack());
            if (var3 != null) {
               var1.setItemStack(var3);
               if (!var2.hasMetadata("ae_mcmmoTP_DROPS")) {
                  ASManager.dropItem(var1.getLocation(), var3);
                  var1.getLocation().subtract(0.0, 10000.0, 0.0);
               }
            }
         }

         if (var2.hasMetadata("ae_mcmmoTP_DROPS")) {
            var2.removeMetadata("ae_mcmmoTP_DROPS", ASManager.getInstance());
            var1.getLocation().subtract(0.0, 10000.0, 0.0);
            ItemStack var4 = var1.getItemStack().clone();
            ASManager.giveItem(var1.getPlayer(), var4);
         }
      }
   }

   public boolean blockHasHerbalismBonusDrops(Block var1) {
      return var1.hasMetadata("mcMMO: Double Drops");
   }

   public int getHerbalismBonusDropMultiplier(Block var1) {
      BonusDropMeta var2 = (BonusDropMeta)var1.getMetadata("mcMMO: Double Drops").get(0);
      return var2.asInt();
   }

   @Override
   public String getName() {
      return "mcMMO";
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTelepathyTreeFellerBonusItemSpawn(McMMOItemSpawnEvent var1) {
      Block var2 = var1.getLocation().getBlock();
      if (treeFellerTelepathyBlocks.containsKey(var2)) {
         var1.setCancelled(true);
         if (var1.getItemSpawnReason() == ItemSpawnReason.BONUS_DROPS) {
            ASManager.giveItem(var1.getPlayer(), var1.getItemStack());
         }
      }
   }
}
