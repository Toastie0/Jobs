package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PermissionHook;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHook extends PluginHookInstance implements PermissionHook {
   @NotNull
   private final Permission permission;

   public VaultHook() {
      RegisteredServiceProvider var1 = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
      this.permission = (Permission)(var1 != null ? (Permission)var1.getProvider() : VaultHook.NoOpPermission.INSTANCE);
   }

   @Override
   public boolean isEnabled() {
      return this.permission != VaultHook.NoOpPermission.INSTANCE;
   }

   @Override
   public String getName() {
      return HookPlugin.VAULT.getPluginName();
   }

   @Override
   public boolean removePerm(Player var1, String var2) {
      return this.permission.playerRemove(var1, var2);
   }

   @Override
   public boolean addPerm(Player var1, String var2) {
      return this.permission.playerAdd(var1, var2);
   }

   @Override
   public boolean isPermEnabled() {
      return this.permission.isEnabled();
   }

   public static class NoOpPermission extends Permission {
      public static final VaultHook.NoOpPermission INSTANCE = new VaultHook.NoOpPermission();

      private NoOpPermission() {
      }

      public String getName() {
         return "NoOpPermission";
      }

      public boolean isEnabled() {
         return false;
      }

      public boolean hasSuperPermsCompat() {
         return false;
      }

      public boolean playerHas(String var1, String var2, String var3) {
         return false;
      }

      public boolean playerAdd(String var1, String var2, String var3) {
         return false;
      }

      public boolean playerRemove(String var1, String var2, String var3) {
         return false;
      }

      public boolean groupHas(String var1, String var2, String var3) {
         return false;
      }

      public boolean groupAdd(String var1, String var2, String var3) {
         return false;
      }

      public boolean groupRemove(String var1, String var2, String var3) {
         return false;
      }

      public boolean playerInGroup(String var1, String var2, String var3) {
         return false;
      }

      public boolean playerAddGroup(String var1, String var2, String var3) {
         return false;
      }

      public boolean playerRemoveGroup(String var1, String var2, String var3) {
         return false;
      }

      public String[] getPlayerGroups(String var1, String var2) {
         return new String[0];
      }

      public String getPrimaryGroup(String var1, String var2) {
         return "";
      }

      public String[] getGroups() {
         return new String[0];
      }

      public boolean hasGroupSupport() {
         return false;
      }
   }
}
