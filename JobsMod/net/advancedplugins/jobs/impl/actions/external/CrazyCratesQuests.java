package net.advancedplugins.jobs.impl.actions.external;

import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCratesQuests extends ActionQuestExecutor {
   private final JavaPlugin plugin;

   public CrazyCratesQuests(JavaPlugin var1) {
      super(var1, "crazycrates");
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerPrize(PlayerPrizeEvent var1) {
      Player var2;
      String var3;
      try {
         Method var4 = PlayerPrizeEvent.class.getDeclaredMethod("getPlayer");
         var2 = (Player)var4.invoke(var1);
         Method var5 = PlayerPrizeEvent.class.getDeclaredMethod("getCrateName");
         var3 = (String)var5.invoke(var1);
      } catch (NoSuchMethodException var6) {
         this.plugin.getLogger().severe("Error: getPlayer or getCrateName method not found in PlayerPrizeEvent: " + var6.getMessage());
         var6.printStackTrace();
         return;
      } catch (IllegalAccessException var7) {
         this.plugin.getLogger().severe("Error: Illegal access to getPlayer or getCrateName method: " + var7.getMessage());
         var7.printStackTrace();
         return;
      } catch (InvocationTargetException var8) {
         this.plugin.getLogger().severe("Error: Exception during getPlayer or getCrateName method invocation: " + var8.getMessage());
         var8.printStackTrace();
         if (var8.getCause() != null) {
            this.plugin.getLogger().severe("  Cause: " + var8.getCause().getMessage());
            var8.getCause().printStackTrace();
         }

         return;
      } catch (ClassCastException var9) {
         this.plugin.getLogger().severe("Error:  ClassCastException during getPlayer or getCrateName method invocation: " + var9.getMessage());
         var9.printStackTrace();
         return;
      }

      if (var3 != null) {
         this.execute("open", var2, var1x -> var1x.root(var3));
      }
   }
}
