package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.Core;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction extends Action {
   public SoundAction(String var1, String var2) {
      super(var1, var2);
   }

   public synchronized void accept(Player var1) {
      String var2 = this.value.toUpperCase();
      float var3 = 1.0F;
      float var4 = 0.0F;
      if (var2.contains(":")) {
         String[] var5 = this.value.replace(" ", "").split(":");
         var2 = var5[0];

         try {
            var3 = Float.parseFloat(var5[1]);
            var4 = var5.length == 3 ? Float.parseFloat(var5[2]) : 1.0F;
         } catch (NumberFormatException var7) {
            Core.getInstance().getLogger().severe("Incorrect sound format. Must me specified as such: NAME:volume:pitch.");
         }
      }

      var1.playSound(var1.getLocation(), Sound.valueOf(var2), var3, var4);
   }
}
