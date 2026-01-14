package net.advancedplugins.jobs.actions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import net.advancedplugins.jobs.menus.MenuFactory;
import net.advancedplugins.jobs.menus.PageMethods;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.simplespigot.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class MenuAction extends Action {
   public MenuAction(String var1, String var2) {
      super(var1, var2);
   }

   public synchronized void accept(MenuFactory var1, Menu var2, Player var3) {
      AtomicBoolean var4 = new AtomicBoolean(false);
      this.checkCondition(var2, "!", (var0, var1x) -> !var0.equals(var1x), () -> {
         this.runAction(var1, var2, var3);
         var4.set(true);
      });
      if (!var4.get()) {
         this.checkCondition(var2, "=", Integer::equals, () -> this.runAction(var1, var2, var3));
      }
   }

   private void runAction(MenuFactory var1, Menu var2, Player var3) {
      if (this.value.equalsIgnoreCase("close")) {
         var2.close();
      } else {
         Menu var4 = var1.createMenu(this.value, var3);
         if (var4 == null) {
            if (var2 instanceof PageMethods) {
               if (this.value.equalsIgnoreCase("previous-page")) {
                  ((PageMethods)var2).previousPage(var2::redraw);
               } else if (this.value.equalsIgnoreCase("next-page")) {
                  ((PageMethods)var2).nextPage(var2::redraw);
               }
            }
         } else if (!(var4 instanceof UserDependent) || ((UserDependent)var4).isUserViable()) {
            var4.show();
         }
      }
   }

   private void checkCondition(Menu var1, String var2, BiPredicate<Integer, Integer> var3, Runnable var4) {
      if (this.condition.contains(var2)) {
         String[] var5 = this.condition.replace(" ", "").split(var2);
         if (var5[0].equalsIgnoreCase("page")
            && StringUtils.isNumeric(var5[1])
            && var1 instanceof PageMethods
            && var3.test(((PageMethods)var1).getPage(), Integer.parseInt(var5[1]))) {
            var4.run();
         }
      } else if (this.condition.isEmpty()) {
         var4.run();
      }
   }
}
