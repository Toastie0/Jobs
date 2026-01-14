package net.advancedplugins.jobs.util.bossbar;

import net.advancedplugins.jobs.Core;
import org.bukkit.entity.Player;

public interface BossBar {
   void show();

   void hide();

   void setTitle(String var1);

   void setProgress(double var1);

   void schedule(int var1);

   void endDisplay();

   void updatePlayer(Player var1);

   public static class Builder {
      public static BossBar create(Core var0, Player var1, String var2) {
         return new NewBossBar(var0, var1, var2);
      }
   }
}
