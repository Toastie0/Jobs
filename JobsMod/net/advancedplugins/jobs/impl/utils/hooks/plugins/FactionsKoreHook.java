package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.expansionstacker.features.MobStackingFeature;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.LivingEntity;

public class FactionsKoreHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.FACTIONSKORE.getPluginName();
   }

   public boolean isStackedEntity(LivingEntity var1) {
      return !this.isStackingEnabled() ? false : MobStackingFeature.isStackedEntity(var1);
   }

   public boolean isStackingEnabled() {
      FactionsKore var1 = FactionsKore.get();
      return var1.featureExists("mob-stacking") ? this.getMobStackingFeature().isOn() : false;
   }

   private MobStackingFeature getMobStackingFeature() {
      return (MobStackingFeature)FactionsKore.get().getFeature(MobStackingFeature.class);
   }
}
