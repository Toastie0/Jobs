package net.advancedplugins.jobs.impl.utils.menus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.menus.item.AdvancedMenuItem;
import net.advancedplugins.jobs.impl.utils.menus.item.ClickAction;
import net.advancedplugins.jobs.impl.utils.menus.item.ClickActionArgs;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class AdvancedMenu implements InventoryHolder {
   private final AdvancedMenusHandler handler = AdvancedMenusHandler.getInstance();
   private Inventory inventory;
   private final LinkedHashMap<Integer, AdvancedMenuItem> itemHashMap = new LinkedHashMap<>();
   private final LinkedHashMap<Integer, ClickAction> actionMap = new LinkedHashMap<>();
   private AdvancedMenuItem fillerItem = null;
   private final Player player;
   private Replace replace;
   private int page = 0;
   private String title;
   private final int invSize;
   private int maxPages = -1;
   private ClickAction closeAction = null;
   private final ConfigurationSection section;

   public AdvancedMenu(Player var1, ConfigurationSection var2, Replace var3) {
      this.player = var1;
      this.title = Text.modify(Text.parsePapi(var2.getString(this.handler.getPath("name")), var1), var3);
      this.invSize = var2.getInt(this.handler.getPath("size"));
      this.replace = var3;
      this.section = var2;
      this.populateItemHashMap(var2, this.itemHashMap, var3);
   }

   public AdvancedMenu(Player var1, ConfigurationSection var2, Replace var3, int var4) {
      this.player = var1;
      this.title = Text.modify(Text.parsePapi(var2.getString(this.handler.getPath("name")), var1), var3);
      this.invSize = var2.getInt(this.handler.getPath("size"));
      this.replace = var3;
      this.section = var2;
      this.maxPages = var4;
      this.populateItemHashMap(var2, this.itemHashMap, var3);
   }

   public AdvancedMenu(Player var1, String var2, int var3, Replace var4) {
      this.player = var1;
      this.title = Text.modify(Text.parsePapi(var2, var1), var4);
      this.invSize = var3;
      this.replace = var4;
      this.section = null;
   }

   public void refreshItems() {
      this.itemHashMap.clear();
      this.populateItemHashMap(this.section, this.itemHashMap, this.replace);
   }

   public void openInventory() {
      this.openInventory(null);
   }

   public void openInventory(Integer var1) {
      this.inventory = Bukkit.createInventory(this, this.invSize, this.title);
      if (var1 != null) {
         var1 = Math.max(0, var1);
         this.page = var1;
         if (this.maxPages != -1) {
            var1 = Math.min(var1, this.maxPages - 1);
         }
      }

      this.itemHashMap.values().forEach(var1x -> {
         try {
            var1x.addToInventory(this.inventory);
         } catch (Exception var3x) {
            ASManager.log("[AdvancedMenu] Error adding item to inventory: [" + var1x.getSlots() + "] " + var1x.getItem());
            var3x.printStackTrace();
         }
      });
      if (this.fillerItem != null) {
         ASManager.fillEmptyInventorySlots(this.inventory, this.fillerItem.getItem());
      }

      this.player.openInventory(this.inventory);
   }

   protected void onClick(Player var1, int var2, ClickType var3) {
      AdvancedMenuItem var4 = this.itemHashMap.get(var2);
      if (var4 != null) {
         if (var4.getAction() == null) {
            ClickAction var8 = this.actionMap.get(var2);
            if (var8 != null) {
               var8.onClick(var1, this, var4, var2, var3);
            }
         } else {
            String var5 = var4.getAction();
            if (var5.contains(":")) {
               String[] var9 = var5.split(":");
               ClickActionArgs var7 = (ClickActionArgs)this.handler.getDefaultActions().get(var9[0]);
               if (var7 != null) {
                  var7.onClick(var1, this, var4, var2, var3, var9[1]);
               }
            } else {
               ClickAction var6 = this.handler.getDefaultActions().get(var5);
               if (var6 != null) {
                  var6.onClick(var1, this, var4, var2, var3);
               }
            }
         }
      }
   }

   protected void onClose(Player var1) {
      if (this.closeAction != null) {
         this.closeAction.onClick(var1, this, null, 0, null);
      }
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   private void populateItemHashMap(ConfigurationSection var1, HashMap<Integer, AdvancedMenuItem> var2, Replace var3) {
      String var4 = this.handler.getPath("items");
      if (var1.isConfigurationSection("items")) {
         ConfigurationSection var5 = var1.getConfigurationSection(var4);

         for (String var7 : var5.getKeys(false)) {
            this.processItemKey(var7, var5, var2, var3);
         }
      }
   }

   private void processItemKey(String var1, ConfigurationSection var2, HashMap<Integer, AdvancedMenuItem> var3, Replace var4) {
      this.processItemKey(var1, var2.getCurrentPath() + "." + var1, var2, var3, var4);
   }

   public void processItem(String var1, ConfigurationSection var2, Replace var3) {
      this.processItemKey(var1, var2.getCurrentPath() + "." + var1, var2, this.itemHashMap, var3);
   }

   public void processItem(String var1, ConfigurationSection var2) {
      this.processItemKey(var1, var2.getCurrentPath() + "." + var1, var2, this.itemHashMap, this.replace);
   }

   private void processItemKey(String var1, String var2, ConfigurationSection var3, HashMap<Integer, AdvancedMenuItem> var4, Replace var5) {
      ConfigurationSection var6 = var3.getConfigurationSection(var1);
      if (var1.equalsIgnoreCase("filler")) {
         this.fillerItem = new AdvancedMenuItem(var1, var6, var5);
      } else {
         for (int var10 : ASManager.getSlots(var1)) {
            assert var6 != null;

            var4.put(var10, new AdvancedMenuItem(var1, var6, var5));
         }
      }
   }

   public AdvancedMenu addItem(AdvancedMenuItem var1, int... var2) {
      if (var1.getSlots().equalsIgnoreCase("null")) {
         var1.setSlots(var2);
      }

      for (int var6 : var2) {
         this.itemHashMap.put(var6, var1);
      }

      return this;
   }

   public AdvancedMenu addAction(ClickAction var1, int... var2) {
      for (int var6 : var2) {
         this.actionMap.put(var6, var1);
      }

      return this;
   }

   public AdvancedMenu addCloseAction(ClickAction var1) {
      this.closeAction = var1;
      return this;
   }

   public void setFillerItem(AdvancedMenuItem var1) {
      this.fillerItem = var1;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Replace getReplace() {
      return this.replace;
   }

   public void setReplace(Replace var1) {
      this.replace = var1;
   }

   public int getPage() {
      return this.page;
   }

   public void setPage(int var1) {
      this.page = var1;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public int getInvSize() {
      return this.invSize;
   }

   public void setMaxPages(int var1) {
      this.maxPages = var1;
   }

   public int getMaxPages() {
      return this.maxPages;
   }
}
