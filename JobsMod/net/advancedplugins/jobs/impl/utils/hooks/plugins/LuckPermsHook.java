package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PermissionHook;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

public class LuckPermsHook extends PluginHookInstance implements PermissionHook {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.LUCKPERMS.getPluginName();
   }

   @Override
   public boolean removePerm(Player var1, String var2) {
      UserManager var3 = LuckPermsProvider.get().getUserManager();
      User var4 = var3.getUser(var1.getUniqueId());
      boolean var5 = var4.data().remove(PermissionNode.builder(var2).build()).wasSuccessful();
      var3.saveUser(var4);
      return var5;
   }

   @Override
   public boolean addPerm(Player var1, String var2) {
      UserManager var3 = LuckPermsProvider.get().getUserManager();
      User var4 = var3.getUser(var1.getUniqueId());
      boolean var5 = var4.data().add(PermissionNode.builder(var2).build()).wasSuccessful();
      var3.saveUser(var4);
      return var5;
   }

   public String getGroup(Player var1) {
      UserManager var2 = LuckPermsProvider.get().getUserManager();
      User var3 = var2.getUser(var1.getUniqueId());

      try {
         return var3.getPrimaryGroup();
      } catch (NullPointerException var5) {
         return null;
      }
   }

   @Override
   public boolean isPermEnabled() {
      return this.isEnabled();
   }
}
