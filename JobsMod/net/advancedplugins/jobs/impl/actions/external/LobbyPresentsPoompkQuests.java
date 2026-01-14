package net.advancedplugins.jobs.impl.actions.external;

import com.poompk.LobbyPresents.Presents.EventAPI.PlayerClickClaimedPresentEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyPresentsPoompkQuests extends ActionQuestExecutor {
   public LobbyPresentsPoompkQuests(JavaPlugin var1) {
      super(var1, "lobbypresents");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPresentClaim(PlayerClickClaimedPresentEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = String.valueOf(var1.getID());
      this.execute("find", var2, var1x -> var1x.root(var3), var1x -> var1x.set("id", var3));
   }
}
