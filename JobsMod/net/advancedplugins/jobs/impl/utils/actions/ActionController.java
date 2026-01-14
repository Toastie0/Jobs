package net.advancedplugins.jobs.impl.utils.actions;

import java.util.HashMap;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.actions.impl.CommandAction;
import net.advancedplugins.jobs.impl.utils.actions.impl.ConsoleCommandAction;
import net.advancedplugins.jobs.impl.utils.actions.impl.MessageAction;
import net.advancedplugins.jobs.impl.utils.actions.impl.SoundAction;
import net.advancedplugins.jobs.impl.utils.actions.impl.SubtitleAction;
import net.advancedplugins.jobs.impl.utils.actions.impl.TitleAction;

public class ActionController {
   private final Map<String, ActionConstructor> actions = new HashMap<>();

   private ActionController() {
      this.register("title", TitleAction::new);
      this.register("subtitle", SubtitleAction::new);
      this.register("message", MessageAction::new);
      this.register("sound", SoundAction::new);
      this.register("command", CommandAction::new);
      this.register("console-command", ConsoleCommandAction::new);
   }

   public static ActionController getInstance() {
      return ActionController.Holder.INSTANCE;
   }

   public Action parse(String var1) {
      String var2 = var1.contains("(") && var1.contains(")") ? var1.substring(var1.indexOf("(") + 1, var1.indexOf(")")).toLowerCase() : "";
      String var3 = var1.contains("{") && var1.contains("}") ? var1.substring(var1.indexOf("{") + 1, this.lastIndexOf(var1, '}')) : "";
      double var4 = var1.contains("<") && var1.contains(">") ? Double.valueOf(var1.substring(var1.indexOf("<") + 1, var1.indexOf(">"))) : 0.0;
      String var6 = var1.contains("[") && var1.contains("]") ? var1.substring(var1.indexOf("[") + 1, var1.indexOf("]")).toLowerCase() : "";
      return this.actions.containsKey(var6) ? this.actions.get(var6).construct(var2, var3, var4) : null;
   }

   private int lastIndexOf(String var1, char var2) {
      for (int var3 = var1.length() - 1; var3 >= 0; var3--) {
         if (var1.charAt(var3) == var2) {
            return var3;
         }
      }

      return -1;
   }

   public void register(String var1, ActionConstructor var2) {
      this.actions.put(var1.toLowerCase(), var2);
   }

   private final class Holder {
      private static final ActionController INSTANCE = new ActionController();
   }
}
