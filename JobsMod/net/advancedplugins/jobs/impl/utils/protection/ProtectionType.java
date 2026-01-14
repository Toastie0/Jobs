package net.advancedplugins.jobs.impl.utils.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionType {
   String getName();

   boolean canBreak(Player var1, Location var2);

   boolean canAttack(Player var1, Player var2);

   boolean isProtected(Location var1);
}
