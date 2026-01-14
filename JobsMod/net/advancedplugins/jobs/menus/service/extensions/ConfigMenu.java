package net.advancedplugins.jobs.menus.service.extensions;

import java.util.Map;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.menus.service.MenuIllustrator;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public abstract class ConfigMenu extends Menu {
   protected final Core plugin;
   protected final Config config;
   protected final MenuIllustrator illustrator;
   private Map<String, Runnable> dynamicActions;

   public ConfigMenu(Core var1, Config var2, Player var3) {
      super(var3, var2.string("menu-title"), var2.integer("menu-rows"));
      this.plugin = var1;
      this.config = var2;
      this.illustrator = var1.getMenuIllustrator();
   }

   @Override
   public void redraw() {
      this.drawConfigItems(var1 -> var1.tryAddPapi(this.player));
   }

   public void drawConfigItems(Replace var1) {
      this.illustrator.draw(this, this.config, this.plugin.getMenuFactory(), this.player, this.plugin.getActionCache(), this.dynamicActions, var1);
   }

   public void dynamicAction(String var1, Runnable var2) {
      this.dynamicActions.put(var1, var2);
   }
}
