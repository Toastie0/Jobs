package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public abstract class Action {
   protected final String condition;
   protected String value;

   public Action(String var1, String var2) {
      this.condition = var1;
      this.value = var2;
   }

   public static Action parse(String var0) {
      String var1 = var0.contains("(") && var0.contains(")") ? var0.substring(var0.indexOf("(") + 1, var0.indexOf(")")).toLowerCase() : "";
      String var2 = var0.contains("{") && var0.contains("}") ? var0.substring(var0.indexOf("{") + 1, lastIndexOf(var0, '}')) : "";
      String var3 = var0.contains("[") && var0.contains("]") ? var0.substring(var0.indexOf("[") + 1, var0.indexOf("]")).toLowerCase() : "";
      switch (var3) {
         case "menu":
            return new MenuAction(var1, var2);
         case "title":
            return new TitleAction(var1, var2);
         case "subtitle":
            return new SubtitleAction(var1, var2);
         case "message":
            return new MessageAction(var1, var2);
         case "sound":
            return new SoundAction(var1, var2);
         case "command":
            return new CommandAction(var1, var2);
         case "console-command":
            return new ConsoleCommandAction(var1, var2);
         default:
            return null;
      }
   }

   private static int lastIndexOf(String var0, char var1) {
      for (int var2 = var0.length() - 1; var2 >= 0; var2--) {
         if (var0.charAt(var2) == var1) {
            return var2;
         }
      }

      return -1;
   }

   public static void executeSimple(Player var0, Iterable<Action> var1, Core var2, Replacer var3) {
      for (Action var5 : var1) {
         var5.value = Text.parsePapi(var5.value, var0);
         if (var5 instanceof MessageAction) {
            ((MessageAction)var5).accept(var0, var3);
         } else if (var5 instanceof SoundAction) {
            ((SoundAction)var5).accept(var0);
         } else if (var5 instanceof TitleAction) {
            ((TitleAction)var5).accept(var0, var3);
         } else if (var5 instanceof SubtitleAction) {
            ((SubtitleAction)var5).accept(var0, var3);
         } else if (var5 instanceof CommandAction) {
            ((CommandAction)var5).accept(var0, var3);
         } else if (var5 instanceof ConsoleCommandAction) {
            ((ConsoleCommandAction)var5).accept(var3);
         } else if (var5 instanceof MenuAction && var2 != null) {
            Menu var6 = var2.getMenuFactory().getOpenMenus().get(var0.getUniqueId());
            if (var6 == null) {
               return;
            }

            ((MenuAction)var5).accept(var2.getMenuFactory(), var6, var0);
         }
      }
   }
}
