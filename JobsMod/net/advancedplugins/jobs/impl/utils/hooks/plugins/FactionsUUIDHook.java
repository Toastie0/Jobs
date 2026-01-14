package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.iface.RelationParticipator;
import java.lang.reflect.Method;
import net.advancedplugins.jobs.impl.utils.hooks.factions.FactionsPluginHook;
import org.bukkit.entity.Player;

public class FactionsUUIDHook extends FactionsPluginHook {
   private final Method relationToLocationMethod;
   private final Method relationMethod;

   public FactionsUUIDHook() {
      try {
         this.relationToLocationMethod = FPlayer.class.getMethod("getRelationToLocation");
         this.relationMethod = FPlayer.class.getMethod("getRelationTo", RelationParticipator.class);
      } catch (Throwable var2) {
         throw var2;
      }
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return "FactionsUUID";
   }

   @Override
   public String getRelation(Player var1, Player var2) {
      try {
         FPlayer var3 = FPlayers.getInstance().getByPlayer(var1);
         FPlayer var4 = FPlayers.getInstance().getByPlayer(var2);
         return this.relationMethod.invoke(var3, var4).toString();
      } catch (Throwable var5) {
         throw var5;
      }
   }

   @Override
   public String getRelationOfLand(Player var1) {
      try {
         FPlayer var2 = FPlayers.getInstance().getByPlayer(var1);
         return this.relationToLocationMethod.invoke(var2).toString();
      } catch (Throwable var3) {
         throw var3;
      }
   }
}
