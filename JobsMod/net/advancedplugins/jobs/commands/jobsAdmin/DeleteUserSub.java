package net.advancedplugins.jobs.commands.jobsAdmin;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.listeners.UserConnectListener;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteUserSub extends JobSubCommand<CommandSender> {
   private final UserCache userCache;

   public DeleteUserSub(Core var1) {
      super(var1);
      this.userCache = var1.getUserCache();
      this.inheritPermission();
      this.addFlats(new String[]{"delete", "user"});
      this.addArgument(
         User.class, "player", var0 -> Bukkit.getOnlinePlayers().stream().<String>map(OfflinePlayer::getName).collect(Collectors.toList()), new String[0]
      );
   }

   @Override
   public void onExecute(CommandSender var1, String[] var2) {
      Optional var3 = this.parseArgument(var2, 2);
      if (!var3.isPresent()) {
         Text.sendMessage(var1, Text.modify(this.locale.getString("admin-cmd.could-not-find-user"), var1x -> var1x.set("player", var2[2])));
      } else {
         User var4 = (User)var3.get();
         UUID var5 = var4.getUuid();
         Player var6 = var4.getPlayer();
         this.userCache.invalidate(var5);
         this.userCache.set(var5, new User(var5));
         if (var6 != null) {
            UserConnectListener.getConnectionListener().loadPlayer(var6);
            Text.sendMessage(var6, this.locale.getString("admin-cmd.target-user-data-deleted"));
         }

         Text.sendMessage(
            var1, Text.modify(this.locale.getString("admin-cmd.user-data-deleted"), var2x -> var2x.set("player", var6 != null ? var6.getName() : var2[2]))
         );
      }
   }
}
