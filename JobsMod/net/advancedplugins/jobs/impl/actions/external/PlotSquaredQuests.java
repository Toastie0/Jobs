package net.advancedplugins.jobs.impl.actions.external;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerAutoPlotEvent;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerPlotTrustedEvent;
import com.plotsquared.core.events.PlayerTeleportToPlotEvent;
import com.plotsquared.core.events.PlotRateEvent;
import java.util.Set;
import java.util.UUID;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlotSquaredQuests extends ExternalActionContainer {
   public PlotSquaredQuests(JavaPlugin var1) {
      super(var1, "plotsquared");
   }

   @Subscribe
   public void onPlayerAutoPlot(PlayerAutoPlotEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getPlayer().getUUID());
      this.executionBuilder("claim").player(var2).root("auto").canBeAsync();
   }

   @Subscribe
   public void onPlotClaim(PlayerClaimPlotEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getPlotPlayer().getUUID());
      this.executionBuilder("claim").player(var2).root("manual").canBeAsync();
   }

   @Subscribe
   public void onPlotEnter(PlayerEnterPlotEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getPlotPlayer().getUUID());
      Set var3 = var1.getPlot().getOwners();
      if (var3.size() == 1) {
         String var4 = Bukkit.getOfflinePlayer((UUID)var3.toArray()[0]).getName();
         this.executionBuilder("visit").player(var2).root(var4).canBeAsync();
      } else {
         this.executionBuilder("visit").player(var2).canBeAsync();
      }
   }

   @Subscribe
   public void onPlotTrust(PlayerPlotTrustedEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getInitiator().getUUID());
      Player var3 = Bukkit.getPlayer(var1.getPlayer());
      this.executionBuilder("trust_player").player(var2).canBeAsync();
      this.executionBuilder("become_trusted").player(var3).canBeAsync();
   }

   @Subscribe
   public void onPlotRate(PlotRateEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getRater().getUUID());
      this.executionBuilder("rate").player(var2).canBeAsync();
   }

   @Subscribe
   public void onTeleportToPlot(PlayerTeleportToPlotEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getPlotPlayer().getUUID());
      Set var3 = var1.getPlot().getOwners();
      if (var3.size() == 1) {
         String var4 = Bukkit.getOfflinePlayer((UUID)var3.toArray()[0]).getName();
         this.executionBuilder("teleport").player(var2).root(var4).canBeAsync();
      } else {
         this.executionBuilder("teleport").player(var2).canBeAsync();
      }
   }
}
