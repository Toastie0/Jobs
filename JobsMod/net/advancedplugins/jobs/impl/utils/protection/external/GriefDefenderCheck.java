package net.advancedplugins.jobs.impl.utils.protection.external;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.flag.Flags;
import java.util.Collections;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefDefenderCheck implements ProtectionType {
   @Override
   public String getName() {
      return "GriefDefender";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      Claim var3 = GriefDefender.getCore().getClaimAt(var2);
      if (var3 != null && !var3.isWilderness()) {
         User var4 = GriefDefender.getCore().getUser(var1.getUniqueId());
         Tristate var5 = GriefDefender.getPermissionManager().getActiveFlagPermissionValue(var3, var4, Flags.BLOCK_BREAK, null, null, Collections.emptySet());
         return var5 == Tristate.TRUE;
      } else {
         return true;
      }
   }

   @Override
   public boolean isProtected(Location var1) {
      return GriefDefender.getCore().getClaimAt(var1) != null;
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }
}
