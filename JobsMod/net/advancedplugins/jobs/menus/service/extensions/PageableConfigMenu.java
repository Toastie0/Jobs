package net.advancedplugins.jobs.menus.service.extensions;

import java.util.Map;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.menus.PageMethods;
import net.advancedplugins.jobs.menus.service.MenuIllustrator;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.PageableMenu;
import org.bukkit.entity.Player;

public abstract class PageableConfigMenu<T> extends PageableMenu<T> implements PageMethods {
   protected final Core plugin;
   protected final Config config;
   protected final MenuIllustrator illustrator;
   private Map<String, Runnable> customActions;

   public PageableConfigMenu(Core var1, Config var2, Player var3, UnaryOperator<Replacer> var4) {
      super(var3, var4.apply(new Replacer()).applyTo(var2.string("menu-title")), var2.integer("menu-rows"));
      this.plugin = var1;
      this.config = var2;
      this.illustrator = var1.getMenuIllustrator();
   }

   @Override
   public void redraw() {
      this.drawPageableItems(() -> this.drawConfigItems(null));
   }

   public void drawConfigItems(Replace var1) {
      this.illustrator.draw(this, this.config, this.plugin.getMenuFactory(), this.player, this.plugin.getActionCache(), this.customActions, var1);
   }

   public void customAction(String var1, Runnable var2) {
      this.customActions.put(var1, var2);
   }
}
