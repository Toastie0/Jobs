package net.advancedplugins.jobs.impl.utils.menus;

import java.util.HashMap;
import net.advancedplugins.jobs.impl.utils.PlayASound;
import net.advancedplugins.jobs.impl.utils.menus.item.ClickAction;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedMenusHandler {
   private final HashMap<String, String> paths = new HashMap<>();
   private static AdvancedMenusHandler instance = null;
   private final HashMap<String, ClickAction> defaultActions = new HashMap<>();
   private final AdvancedMenuClick clickHandler = new AdvancedMenuClick();

   public AdvancedMenusHandler(JavaPlugin var1) {
      instance = this;
      this.paths.put("name", "name");
      this.paths.put("size", "size");
      this.paths.put("items", "items");
      this.loadDefaultActions();
      var1.getServer().getPluginManager().registerEvents(this.clickHandler, var1);
   }

   private void loadDefaultActions() {
      this.defaultActions.put("CLOSE", (var0, var1, var2, var3, var4) -> var0.closeInventory());
      this.defaultActions.put("PREVIOUS_PAGE", (var0, var1, var2, var3, var4) -> {
         var1.openInventory(var1.getPage() - 1);
         PlayASound.playSound("ENTITY_EXPERIENCE_ORB_PICKUP", var0);
      });
      this.defaultActions.put("NEXT_PAGE", (var0, var1, var2, var3, var4) -> {
         var1.openInventory(var1.getPage() + 1);
         PlayASound.playSound("ENTITY_EXPERIENCE_ORB_PICKUP", var0);
      });
   }

   public String getPath(String var1) {
      return this.paths.getOrDefault(var1, var1);
   }

   public void unload() {
      HandlerList.unregisterAll(this.clickHandler);
   }

   public static AdvancedMenusHandler getInstance() {
      return instance;
   }

   public HashMap<String, ClickAction> getDefaultActions() {
      return this.defaultActions;
   }
}
