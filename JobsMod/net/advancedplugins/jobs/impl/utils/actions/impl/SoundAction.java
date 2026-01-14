package net.advancedplugins.jobs.impl.utils.actions.impl;

import net.advancedplugins.jobs.impl.utils.actions.Action;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction extends Action {
   public SoundAction(String var1, String var2, double var3) {
      super(var1, var2, var3);
   }

   @Override
   public void accept(Player var1, Replacer var2) {
      String var3 = this.value.toUpperCase();
      float var4 = 1.0F;
      float var5 = 0.0F;
      if (var3.contains(":")) {
         String[] var6 = this.value.replace(" ", "").split(":");
         var3 = var6[0];

         try {
            var4 = Float.parseFloat(var6[1]);
            var5 = var6.length == 3 ? Float.parseFloat(var6[2]) : 1.0F;
         } catch (NumberFormatException var8) {
            System.out.println("Incorrect sound format. Must me specified as such: NAME:volume:pitch.");
         }
      }

      var1.playSound(var1.getLocation(), Sound.valueOf(var3), var4, var5);
   }
}
