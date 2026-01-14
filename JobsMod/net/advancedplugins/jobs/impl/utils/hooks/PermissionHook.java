package net.advancedplugins.jobs.impl.utils.hooks;

import org.bukkit.entity.Player;

public interface PermissionHook {
   boolean removePerm(Player var1, String var2);

   boolean addPerm(Player var1, String var2);

   boolean isPermEnabled();
}
