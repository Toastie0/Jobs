package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import java.util.HashSet;
import java.util.Set;
import net.advancedplugins.jobs.impl.utils.SchedulerUtils;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicMobsHook extends PluginHookInstance implements Listener {
   private static final Set<Entity> ignoreEnchantsMobs = new HashSet<>();

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.MYTHICMOBS.getPluginName();
   }

   public boolean isMythicMob(LivingEntity var1) {
      return MythicBukkit.inst().getAPIHelper().isMythicMob(var1.getUniqueId());
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   private void onMythicDamage(MythicDamageEvent var1) {
      if (var1.getDamageMetadata().getIgnoreEnchantments()) {
         ignoreEnchantsMobs.add(var1.getCaster().getEntity().getBukkitEntity());
         SchedulerUtils.runTaskLater(() -> ignoreEnchantsMobs.remove(var1.getCaster().getEntity().getBukkitEntity()), 4L);
      }
   }

   public static Set<Entity> getIgnoreEnchantsMobs() {
      return ignoreEnchantsMobs;
   }
}
