package net.advancedplugins.jobs.impl.utils.protection;

import java.util.HashMap;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.LocalLocation;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.protection.external.FactionsCheck;
import net.advancedplugins.jobs.impl.utils.protection.external.LandsCheck;
import net.advancedplugins.jobs.impl.utils.protection.external.ProtectionStonesCheck;
import net.advancedplugins.jobs.impl.utils.protection.external.SlimeFunCheck;
import net.advancedplugins.jobs.impl.utils.protection.external.WorldGuardCheck;
import net.advancedplugins.jobs.impl.utils.protection.internal.GlobalProtCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectionHandler {
   private final HashMap<String, ProtectionType> protectionMap = new HashMap<>();

   public ProtectionHandler(JavaPlugin var1) {
      if (HooksHandler.isEnabled(HookPlugin.WORLDGUARD)) {
         this.register(var1, new WorldGuardCheck());
      }

      if (HooksHandler.isEnabled(HookPlugin.SLIMEFUN)) {
         this.register(var1, new SlimeFunCheck());
      }

      if (HooksHandler.isEnabled(HookPlugin.FACTIONS)) {
         this.register(var1, new FactionsCheck());
      }

      if (HooksHandler.isEnabled(HookPlugin.LANDS)) {
         this.register(var1, new LandsCheck());
      }

      if (HooksHandler.isEnabled(HookPlugin.PROTECTIONSTONES)) {
         this.register(var1, new ProtectionStonesCheck());
      }

      this.register(var1, new GlobalProtCheck());
   }

   public void register(JavaPlugin var1, ProtectionType var2) {
      if (!var1.equals(ASManager.getInstance())) {
         ASManager.getInstance().getLogger().info(var1.getName() + " register a new protection check: " + var2.getName());
      }

      this.protectionMap.put(var2.getName(), var2);
   }

   public boolean canBreak(Location var1, Player var2) {
      boolean var3 = this.protectionMap.values().stream().allMatch(var2x -> var2x.canBreak(var2, var1));
      ASManager.debug("[LAND PROT] Can " + var2.getName() + " break at " + new LocalLocation(var1).getEncode() + "? " + var3);
      return var3;
   }

   public boolean canAttack(Player var1, Player var2) {
      return this.protectionMap.values().stream().anyMatch(var2x -> !var2x.canAttack(var1, var2));
   }

   public boolean isProtected(Location var1) {
      return this.protectionMap.values().stream().anyMatch(var1x -> var1x.isProtected(var1));
   }
}
