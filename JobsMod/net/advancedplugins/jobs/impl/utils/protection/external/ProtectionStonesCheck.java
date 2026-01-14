package net.advancedplugins.jobs.impl.utils.protection.external;

import dev.espi.protectionstones.PSRegion;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionStonesCheck implements ProtectionType {
   @Override
   public String getName() {
      return "ProtectionStones";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      PSRegion var3 = PSRegion.fromLocation(var2);
      return var3 == null ? true : var3.isMember(var1.getUniqueId()) || var3.isOwner(var1.getUniqueId());
   }

   @Override
   public boolean isProtected(Location var1) {
      return false;
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }
}
