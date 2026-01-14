package net.advancedplugins.jobs.impl.actions.external;

import me.angeschossen.lands.api.events.ChunkPostClaimEvent;
import me.angeschossen.lands.api.events.LandCreateEvent;
import me.angeschossen.lands.api.events.LandDeleteEvent;
import me.angeschossen.lands.api.events.LandInvitePlayerEvent;
import me.angeschossen.lands.api.events.LandTrustPlayerEvent;
import me.angeschossen.lands.api.events.LandUntrustPlayerEvent;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class LandsQuests extends ExternalActionContainer {
   public LandsQuests(JavaPlugin var1) {
      super(var1, "lands");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandJoin(LandTrustPlayerEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getTargetUUID());
      this.executionBuilder("join").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandLeave(LandUntrustPlayerEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getTargetUUID());
      this.executionBuilder("leave").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandCreate(LandCreateEvent var1) {
      Player var2 = var1.getLandPlayer().getPlayer();
      this.executionBuilder("create").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandDisband(LandDeleteEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getLand().getOwnerUID());
      this.executionBuilder("disband").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandInvite(LandInvitePlayerEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getTargetUUID());
      this.executionBuilder("invited").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLandClaim(ChunkPostClaimEvent var1) {
      Player var2 = var1.getLandPlayer().getPlayer();
      this.executionBuilder("claim_chunk").player(var2).progressSingle().canBeAsync(!Bukkit.isPrimaryThread()).buildAndExecute();
   }
}
