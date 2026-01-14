package net.advancedplugins.jobs.impl.utils.editor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.SchedulerUtils;
import net.advancedplugins.jobs.impl.utils.SkullCreator;
import net.advancedplugins.jobs.impl.utils.items.ItemBuilder;
import net.advancedplugins.jobs.impl.utils.items.ItemFlagFix;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigEditorMenu implements Listener {
   private static ConfigEditorHandler handler;
   private Inventory inv;
   private int page = 0;
   private final int totalPages;
   private final int entriesPerPage = 45;
   private final Player editor;
   private final LinkedList<KeyInfo> keyInfos;
   private final String inventoryName;
   private boolean creatingNew = false;

   public ConfigEditorMenu(Player var1, String var2, LinkedList<KeyInfo> var3, JavaPlugin var4) {
      this.editor = var1;
      this.keyInfos = var3;
      this.inventoryName = var2;
      this.totalPages = ASManager.getPages(var3.size(), 45);
      Bukkit.getPluginManager().registerEvents(this, var4);
   }

   public void open() {
      this.inv = Bukkit.createInventory(null, 54, Text.modify(this.inventoryName));
      this.inv.setMaxStackSize(ThreadLocalRandom.current().nextInt(64, 256));
      List var1 = this.keyInfos.subList(this.page * 45, Math.min((this.page + 1) * 45, this.keyInfos.size()));
      int var2 = 1;

      for (KeyInfo var4 : var1) {
         ItemBuilder var5 = new ItemBuilder(var4.displayMaterial);
         var5.setName(var4.name);
         var5.addLoreLine(Text.modify(" &7&l(!) &7" + var4.description));
         var5.addLoreLine(" ");
         var5.addLoreLine(Text.modify(" &7➤ &nLeft Click&7 here to start editing."));
         if (var4.wikiLink != null) {
            var5.addLoreLine(Text.modify(" &7ⓘ &nRight Click&7 here to read more."));
         }

         var5.addItemFlag(ItemFlagFix.hideAllAttributes());
         ItemStack var6 = var5.toItemStack();
         var6 = NBTapi.addNBTTag("editKey", var4.name, var6);
         var6.setAmount(var2);
         this.inv.addItem(new ItemStack[]{var6});
         var2++;
      }

      for (int var7 = this.inv.getSize() - 9; var7 < this.inv.getSize(); var7++) {
         this.inv
            .setItem(var7, new ItemBuilder(Material.matchMaterial(handler.getGlassColor() + "_STAINED_GLASS_PANE")).setGlowing(true).setName(" ").toItemStack());
      }

      if (getHandler().canCreateNewEntries()) {
         this.inv
            .setItem(
               this.inv.getSize() - 1,
               NBTapi.addNBTTag(
                  "action",
                  "create",
                  new ItemBuilder(Material.LIME_WOOL)
                     .setName(Text.modify("&a&lCreate a new one!"))
                     .addLoreLine(Text.modify("&e&lClick here to start the creation process."))
                     .toItemStack()
               )
            );
      }

      if (this.page + 1 < this.totalPages) {
         this.inv
            .setItem(
               this.inv.getSize() - 4,
               NBTapi.addNBTTag(
                  "action",
                  "next",
                  new ItemBuilder(
                        SkullCreator.itemFromBase64(
                           "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"
                        )
                     )
                     .setName(Text.modify("&8>> &6Next Page"))
                     .setAmount(this.page + 1)
                     .toItemStack()
               )
            );
      }

      if (this.page > 0) {
         this.inv
            .setItem(
               this.inv.getSize() - 6,
               NBTapi.addNBTTag(
                  "action",
                  "back",
                  new ItemBuilder(
                        SkullCreator.itemFromBase64(
                           "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ"
                        )
                     )
                     .setName(Text.modify("&8<< &6Previous Page"))
                     .setAmount(Math.max(1, this.page))
                     .toItemStack()
               )
            );
      }

      placeFiller(this.inv, this.editor);
      this.editor.openInventory(this.inv);
   }

   static void placeFiller(Inventory var0, Player var1) {
      ItemStack var2 = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();

      for (int var3 = 0; var3 < var0.getSize() - 9; var3++) {
         if (var0.getItem(var3) == null) {
            var0.setItem(var3, var2);
         }
      }
   }

   private KeyInfo matchKeyInfoWithName(String var1) {
      return this.keyInfos.stream().filter(var1x -> var1.equals(var1x.name)).findFirst().orElse(null);
   }

   @EventHandler
   public void onCreate(AsyncPlayerChatEvent var1) {
      if (this.creatingNew && var1.getPlayer().equals(this.editor)) {
         this.creatingNew = false;
         var1.setCancelled(true);
         HandlerList.unregisterAll(this);
         String var2 = var1.getMessage();
         if (var2.equalsIgnoreCase("cancel")) {
            this.editor.sendMessage(Text.modify("cancelled"));
            return;
         }

         SchedulerUtils.runTask(() -> handler.create(var2, this.editor).open());
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getInventory().equals(this.inv)) {
         var1.setCancelled(true);
         ItemStack var2 = var1.getCurrentItem();
         if (NBTapi.contains("action", var2)) {
            String var4 = NBTapi.get("action", var2);
            if (var4.equalsIgnoreCase("back")) {
               this.page--;
               this.open();
            } else if (var4.equalsIgnoreCase("next")) {
               this.page++;
               this.open();
            } else if (var4.equalsIgnoreCase("create")) {
               var1.getWhoClicked().closeInventory();
               this.editor.sendMessage(Text.modify("&fPlease enter a name for the enchantment or type &e'cancel'&f to cancel."));
               this.creatingNew = true;
            }

            return;
         }

         if (NBTapi.contains("editKey", var2)) {
            ConfigEditorGui var3 = handler.openEditor(this.matchKeyInfoWithName(NBTapi.get("editKey", var2)), (Player)var1.getWhoClicked());
            var3.open();
            HandlerList.unregisterAll(this);
         }
      }
   }

   public static ConfigEditorHandler getHandler() {
      return handler;
   }

   public static void setHandler(ConfigEditorHandler var0) {
      handler = var0;
   }
}
