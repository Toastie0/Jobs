package net.advancedplugins.jobs.impl.utils.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.ColorUtils;
import net.advancedplugins.jobs.impl.utils.SchedulerUtils;
import net.advancedplugins.jobs.impl.utils.items.ItemBuilder;
import net.advancedplugins.jobs.impl.utils.items.ItemFlagFix;
import net.advancedplugins.jobs.impl.utils.nbt.NBTapi;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigEditorGui implements Listener {
   private final JavaPlugin plugin;
   private final Player editor;
   private String defaultDesc = "";
   private String defaultMaterial = "PAPER";
   private final ConfigurationSection config;
   private final String baseSection;
   private String currentSection = "";
   private String lastCurrentSection = "";
   private String editingKey;
   private final KeyInfo editObject;
   private final LinkedList<KeyInfo> keyInfos = new LinkedList<>();
   private final Inventory inventory;
   private int page = 0;
   private static final int ITEMS_PER_PAGE = 27;
   private boolean enabled = true;
   private int editingLine = -2;
   private List<String> originalList;
   private List<String> list;
   private Inventory listEditor = null;

   public ConfigEditorGui(String var1, String var2, ConfigurationSection var3, Player var4, LinkedList<KeyInfo> var5, KeyInfo var6, JavaPlugin var7) {
      this.config = var3;
      this.baseSection = var2;
      this.keyInfos.addAll(var5);
      this.editor = var4;
      this.plugin = var7;
      this.editObject = var6;
      this.inventory = Bukkit.createInventory(null, 18, Text.modify(var1));
      Bukkit.getPluginManager().registerEvents(this, var7);
   }

   public ConfigEditorGui open() {
      if (!this.lastCurrentSection.equalsIgnoreCase(this.currentSection)) {
         this.lastCurrentSection = this.currentSection;
         this.page = 0;
      }

      this.inventory.clear();
      int var1 = this.page * 27;
      int var2 = Math.min(var1 + 27, this.config.getConfigurationSection(this.currentSection).getKeys(false).size());
      List var3 = new ArrayList(this.config.getConfigurationSection(this.currentSection).getKeys(false)).subList(var1, var2);

      for (int var4 = this.inventory.getSize() - 9; var4 < this.inventory.getSize(); var4++) {
         this.inventory
            .setItem(
               var4,
               new ItemBuilder(Material.matchMaterial(ConfigEditorMenu.getHandler().getGlassColor() + "_STAINED_GLASS_PANE"))
                  .setName(" ")
                  .setGlowing(true)
                  .toItemStack()
            );
      }

      for (String var5 : var3) {
         var5 = this.getPathWithKey(var5);
         KeyInfo var6 = this.matchInfo(var5);
         this.inventory.addItem(new ItemStack[]{this.getItemForKey(var5, var6)});
      }

      for (KeyInfo var13 : this.getNotSetKeys()) {
         if (!var13.path.endsWith("*")
            && !var3.contains(this.getLastKey(var13.path))
            && !this.config.isConfigurationSection(var13.path)
            && !this.config.contains(var13.path)) {
            this.inventory.addItem(new ItemStack[]{this.getItemForKey(var13.path, var13)});
         }
      }

      if (this.matchInfo(this.currentSection + ".*") != null) {
         this.inventory.addItem(new ItemStack[]{new ItemBuilder(Material.GREEN_WOOL).setName(Text.modify("&a&lAdd new entry.")).toItemStack()});
      }

      if (this.page > 0) {
         ItemBuilder var9 = new ItemBuilder(Material.BOOK).setAmount(this.page).setName("Previous Page");
         this.inventory.setItem(this.inventory.getSize() - 6, var9.toItemStack());
      }

      if (var2 < var3.size()) {
         ItemBuilder var10 = new ItemBuilder(Material.BOOK).setAmount(this.page + 2).setName("Next Page");
         this.inventory.setItem(this.inventory.getSize() - 4, var10.toItemStack());
      }

      ItemBuilder var11 = new ItemBuilder(Material.ARROW).setName("Back");
      this.inventory.setItem(this.inventory.getSize() - 1, var11.toItemStack());
      ConfigEditorMenu.placeFiller(this.inventory, this.editor);
      this.editor.openInventory(this.inventory);
      return this;
   }

   public KeyInfo matchInfo(String var1) {
      return this.keyInfos
         .stream()
         .filter(
            var1x -> var1x.path.equalsIgnoreCase(var1)
               || Pattern.matches(
                  var1x.path.endsWith("*")
                     ? var1x.path.replace(".", "\\.").replace("*", "[^\\.]*") + "$"
                     : var1x.path.replace(".", "\\.").replace("*", "[^\\.]*"),
                  var1
               )
         )
         .findFirst()
         .orElse(null);
   }

   public String getDescription(String var1) {
      KeyInfo var2 = this.matchInfo(var1);
      return var2 != null && var2.description != null ? var2.description : this.defaultDesc;
   }

   public String getPathWithKey(String var1) {
      return this.currentSection.isEmpty() ? var1 : this.currentSection + "." + var1;
   }

   private KeyType getKeyType(String var1) {
      if (this.matchInfo(var1) != null) {
         KeyInfo var2 = this.matchInfo(var1);
         if (var2.type != null) {
            return var2.type;
         }
      }

      try {
         return KeyType.getKeyType(this.config.get(var1));
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private ItemStack getItemForKey(String var1, KeyInfo var2) {
      Material var3 = var2 != null ? var2.displayMaterial : Material.matchMaterial(this.defaultMaterial);
      ItemBuilder var4 = new ItemBuilder(var3).setName(Text.modify("&a&l" + this.getLastKey(var1)));
      String var5 = this.getDescription(var1);
      if (var5 != null && !var5.isEmpty()) {
         var4.addLoreLine(Text.modify(" &f&lⓘ &f" + var5));
      }

      if (this.config.contains(var1)) {
         if (this.config.get(var1) instanceof List) {
            this.addListValues(var4, var1);
         } else if (!(this.config.get(var1) instanceof ConfigurationSection)) {
            this.addNonSectionValues(var4, var1);
         } else {
            this.addSectionValues(var4, var1);
            var4.addLoreLine("");
            var4.addLoreLine(Text.modify("&fOpen a sub-menu to edit."));
         }
      } else {
         var4.addLoreLine(Text.modify("&eValue is not set in the configuration."));
      }

      this.addTypeInfo(var4, var1);
      var4.addLoreLine(" ").addLoreLine(Text.modify(" &7➤ &lLeft Click&7 here to edit this value"));
      if (this.config.contains(var1)) {
         var4.addLoreLine(Text.modify(" &c✕ &lShift Left Click&c here to delete this value"));
      }

      if (var2 != null && var2.wikiLink != null) {
         var4.addLoreLine(Text.modify(" &fⓘ &lRight Click&7 here to learn more"));
      }

      var4.addItemFlag(ItemFlagFix.hideAllAttributes());
      return var4.toItemStack();
   }

   private void addListValues(ItemBuilder var1, String var2) {
      List var3 = (List)this.config.get(var2);
      var1.addLoreLine(Text.modify("&eCurrent values: "));

      for (String var5 : var3) {
         var1.addLoreLine(Text.modify("&6&l \ud83d\udd39 &f" + ASManager.limit(var5, 40, "...")));
      }
   }

   private void addNonSectionValues(ItemBuilder var1, String var2) {
      var1.addLoreLine(Text.modify("&eCurrent value&f: " + this.config.get(var2).toString()));
      KeyType var3 = this.getKeyType(var2);
      if (var3 != null) {
         if (var3.equals(KeyType.MATERIAL)) {
            Material var4 = Material.matchMaterial(this.config.getString(var2));
            if (var4 != null) {
               var1.setType(var4);
            }
         } else if (var3.equals(KeyType.INTEGER)) {
            int var5 = Math.max(1, Math.min(64, this.config.getInt(var2)));
            var1.setAmount(var5);
         }
      }
   }

   private void addSectionValues(ItemBuilder var1, String var2) {
      for (String var4 : (List)this.config.getConfigurationSection(var2).getKeys(false).stream().limit(8L).collect(Collectors.toList())) {
         Object var5 = this.config.get(var2 + "." + var4);
         KeyType var6 = KeyType.getKeyType(var5);
         if (var6 != null) {
            var5 = this.formatSectionValue(var5, var6);
            var1.addLoreLine(Text.modify("&e♦ &6&l" + var4 + " &f" + var5));
         }
      }
   }

   private Object formatSectionValue(Object var1, KeyType var2) {
      if (var2.equals(KeyType.LIST)) {
         var1 = "List";
      } else if (var2.equals(KeyType.KEY)) {
         var1 = "sub-menu";
      } else if (var2.equals(KeyType.STRING)) {
         var1 = ((String)var1).length() > 32 ? ((String)var1).substring(0, 31) + "..." : var1;
      }

      return var1;
   }

   private void addTypeInfo(ItemBuilder var1, String var2) {
      KeyType var3 = this.getKeyType(var2);
      if (var3 != null) {
         var1.addLoreLine(Text.modify("&aValue Type: &f" + var3.getFriendlyName()));
      }
   }

   private List<KeyInfo> getNotSetKeys() {
      String var1 = this.currentSection;
      return var1 == null ? Collections.emptyList() : this.keyInfos.stream().filter(this::isSameBlock).collect(Collectors.toList());
   }

   private boolean isSameBlock(KeyInfo var1) {
      if (var1 == null) {
         return false;
      } else if (this.currentSection.isEmpty()) {
         return !var1.path.contains(".");
      } else {
         String var2 = var1.path.replace("*", this.getLastKey(this.currentSection)).replaceFirst(this.currentSection + ".", "");
         return var1.path.replace("*", this.getLastKey(this.currentSection)).startsWith(this.currentSection) && !var2.contains(".");
      }
   }

   public void close() {
      this.enabled = false;
      HandlerList.unregisterAll(this);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getCurrentItem() != null) {
         boolean var2 = false;
         if (var1.getInventory().equals(this.listEditor)) {
            var1.setCancelled(true);
            ItemStack var3 = var1.getCurrentItem();
            String var4 = ColorUtils.stripColor(var3.getItemMeta().getDisplayName());
            if ("Go back".equals(var3.getItemMeta().getDisplayName())) {
               this.open();
               return;
            }

            if (var1.isRightClick()) {
               this.list.remove(var4);
               this.config.set(this.editingKey, this.list);
               this.close();
               ConfigEditorMenu.getHandler().updateFiles(this.editObject, this.editor, this.editingKey, this.currentSection);
               return;
            }

            if ("Add new line".equals(var4)) {
               this.editingLine = -1;
            } else {
               this.editingLine = this.list.indexOf(var4);
            }

            var2 = true;
         }

         if (var1.getInventory().equals(this.inventory) || var2) {
            var1.setCancelled(true);
            ItemStack var10 = var1.getCurrentItem();
            if (var10 == null) {
               return;
            }

            String var11 = ColorUtils.stripColor(var10.getItemMeta().getDisplayName());
            if (var11.isEmpty() || var11.equals(" ")) {
               return;
            }

            if ("Back".equals(var11)) {
               if (this.baseSection.length() < this.currentSection.length()) {
                  this.currentSection = this.getPreviousSection();
                  this.open();
               } else {
                  this.close();
                  ConfigEditorMenu.getHandler().openMainMenu(this.editor);
               }

               return;
            }

            if ("Next Page".equals(var11)) {
               this.page++;
               this.open();
               return;
            }

            if ("Previous Page".equals(var11)) {
               this.page--;
               this.open();
               return;
            }

            var11 = this.getPathWithKey(var11);
            if (var11.endsWith("Add new entry.")) {
               var11 = this.currentSection + ".*";
            }

            KeyInfo var5 = this.matchInfo(var11);
            if (var1.getClick().isRightClick() && var5 != null && var5.wikiLink != null) {
               TextComponent var13 = new TextComponent(Text.modify("&7&lⓘ &aClick here to open the page explaining '&f" + var11 + "&a' setting."));
               var13.setClickEvent(new ClickEvent(Action.OPEN_URL, var5.wikiLink));
               this.editor.spigot().sendMessage(var13);
               this.editor.closeInventory();
               return;
            }

            if (var1.isLeftClick() && var1.isShiftClick()) {
               this.config.set(var11, null);
               this.close();
               ConfigEditorMenu.getHandler().updateFiles(this.editObject, this.editor, var11, var11);
               return;
            }

            if (!(this.config.get(var11) instanceof ConfigurationSection) && (var5 == null || !var5.menu)) {
               this.editingKey = !var2 ? var11 : this.editingKey;
               Object var6 = this.config.get(this.editingKey);
               KeyType var7 = this.getKeyType(var11);
               if (var6 instanceof List || var7 != null && var7.equals(KeyType.LIST)) {
                  if (!var2) {
                     this.editingKey = var11;
                     this.openListEditorGui(var11);
                     return;
                  }

                  if (this.editingLine < 0) {
                     var6 = null;
                  } else {
                     var6 = ((List)var6).get(this.editingLine);
                  }
               } else {
                  this.editingKey = var11;
               }

               if (var7 != null && var7.equals(KeyType.BOOLEAN) && var6 != null) {
                  boolean var15 = (Boolean)var6;
                  this.processInput(!var15 + "");
                  return;
               }

               this.editor.closeInventory();
               this.editor.sendMessage(Text.modify("&8-------------------------------------------------------"));
               this.editor.sendMessage(Text.modify(" "));
               this.editor.sendMessage(Text.modify(" "));
               this.editor.sendMessage(Text.modify(" "));
               this.editor.sendMessage(Text.modify(" "));
               String var8 = "Please enter a value for &e'" + var11 + "'&o";
               if (var7 != null) {
                  var8 = var8 + " (" + var7.getFriendlyName() + ")";
               }

               var8 = var8 + "&f or type &e'cancel'&f to cancel. \n&f&nCurrent value:&e ";
               this.editor.sendMessage(Text.modify(var8) + (var6 == null ? "not set" : var6.toString()));
               this.editor.sendMessage(Text.modify(" "));
               if (var5 != null && var5.wikiLink != null) {
                  TextComponent var9 = new TextComponent(Text.modify("&7&lⓘ &eClick here to open the page explaining '&f" + var11 + "&e' setting."));
                  var9.setClickEvent(new ClickEvent(Action.OPEN_URL, var5.wikiLink));
                  this.editor.spigot().sendMessage(var9);
               }

               if (var6 != null) {
                  TextComponent var16 = new TextComponent(Text.modify("&7&lⓘ &aClick here to insert &f" + var6.toString() + "&a to your chat."));
                  var16.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, var6.toString()));
                  this.editor.spigot().sendMessage(var16);
               }

               if (var5 != null && var5.suggestions != null) {
                  this.editor.sendMessage(Text.modify("&7&oYou can use TAB to auto-complete suggestions for this value!"));
               }
            } else {
               if (this.config.get(var11) == null) {
                  this.config.createSection(var11);
               }

               this.currentSection = var11;
               this.open();
            }
         }
      }
   }

   private String getPreviousSection() {
      String[] var1 = this.currentSection.split("\\.");
      if (var1.length == 1) {
         return "";
      } else {
         return var1.length == 2 ? var1[0] : String.join(".", Arrays.copyOfRange(var1, 0, var1.length - 1));
      }
   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent var1) {
      if (var1.getPlayer().equals(this.editor) && this.editingKey != null) {
         SchedulerUtils.runTaskLater(() -> {
            if (this.validateInput(var1.getMessage())) {
               this.processInput(var1.getMessage());
               if (this.enabled) {
                  this.open();
               }
            } else {
               this.editor.sendMessage("Invalid input: " + var1.getMessage());
            }
         });
         var1.setCancelled(true);
      }
   }

   private void processInput(String var1) {
      if (var1.equalsIgnoreCase("cancel")) {
         this.open();
      } else {
         KeyType var2 = this.getKeyType(this.editingKey);
         if (var2 == null) {
            this.editor.sendMessage(Text.modify("&c&l(!) &cInvalid value type: " + var1));
         } else {
            KeyInfo var3 = this.matchInfo(this.editingKey);
            if (var3 != null) {
               if (var3.addKeyToInput) {
                  var1 = this.editingKey + "." + var1;
               } else if (var3.type.equals(KeyType.KEY)) {
                  var1 = this.currentSection + "." + var1;
               }
            }

            Object var4 = var2.process(var1, this.config);
            if (this.editingLine == -2) {
               if (!var4.equals(KeyType.KEY)) {
                  this.config.set(this.editingKey, var4);
               }
            } else {
               List var5 = this.config.getList(this.editingKey);
               if (this.editingLine != -1) {
                  var5.set(this.editingLine, var4);
               } else {
                  var5.add(var4);
               }

               this.config.set(this.editingKey, var5);
               this.editingLine = -2;
            }

            this.close();
            ConfigEditorMenu.getHandler().updateFiles(this.editObject, this.editor, this.editingKey, this.currentSection);
         }
      }

      if (this.matchInfo(this.editingKey) != null && this.matchInfo(this.editingKey).suggestions != null) {
         this.editingKey = null;
      }
   }

   public String getLastKey(String var1) {
      String[] var2 = var1.split("\\.");
      return var2[var2.length - 1];
   }

   private boolean validateInput(String var1) {
      if (var1.equalsIgnoreCase("cancel")) {
         return true;
      } else {
         KeyType var2 = this.getKeyType(this.editingKey);
         return var2 != null && var2.validate(var1);
      }
   }

   public ConfigEditorGui setDefaultDesc(String var1) {
      this.defaultDesc = var1;
      return this;
   }

   public ConfigEditorGui setCurrentSection(String var1) {
      this.currentSection = var1;
      return this;
   }

   public ConfigEditorGui setDefaultMaterial(String var1) {
      this.defaultMaterial = var1;
      return this;
   }

   public void openListEditorGui(String var1) {
      this.list = (List<String>)(this.config.contains(var1) ? this.config.getStringList(var1) : new ArrayList<>());
      this.listEditor = Bukkit.createInventory(null, ASManager.getInvSize(this.list.size() + 1), Text.modify("&eEdit list: ") + var1);

      for (String var3 : this.list) {
         ItemStack var4 = new ItemStack(Material.PAPER);
         ItemMeta var5 = var4.getItemMeta();
         var5.setDisplayName(var3);
         var5.setLore(Arrays.asList(Text.modify(" &7➤ &nLeft Click&7 here to edit this line."), Text.modify(" &7✕ &nRight Click&7 here to remove this line.")));
         var4.setItemMeta(var5);
         var4 = NBTapi.addNBTTag("randomizer", UUID.randomUUID().toString(), var4);
         this.listEditor.addItem(new ItemStack[]{var4});
      }

      ItemStack var6 = new ItemStack(Material.GREEN_WOOL);
      ItemMeta var7 = var6.getItemMeta();
      var7.setDisplayName("Add new line");
      var6.setItemMeta(var7);
      this.listEditor.setItem(this.list.size(), var6);
      ItemStack var9 = new ItemStack(Material.ARROW);
      ItemMeta var10 = var9.getItemMeta();
      var10.setDisplayName("Go back");
      var9.setItemMeta(var10);
      this.listEditor.setItem(this.listEditor.getSize() - 1, var9);
      this.editor.openInventory(this.listEditor);
   }

   public void addItemForPath(String var1) {
      if (this.matchInfo("item") == null) {
         this.keyInfos
            .add(
               new KeyInfo(var1, Material.CRAFTING_TABLE, "Configure the item", null, "https://wiki.advancedplugins.net/configuration/config-items")
                  .setType(KeyType.KEY)
            );
         this.keyInfos
            .add(
               new KeyInfo(var1 + ".type", Material.STICK, "Set item's material", null, "https://wiki.advancedplugins.net/configuration/config-items")
                  .setType(KeyType.MATERIAL)
                  .addSuggestions(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()))
            );
         this.keyInfos
            .add(
               new KeyInfo(var1 + ".amount", Material.CHEST, "Set item's amount", null, "https://wiki.advancedplugins.net/configuration/config-items")
                  .setType(KeyType.INTEGER)
                  .addSuggestions(Arrays.asList("1", "10", "16", "64"))
            );
         this.keyInfos
            .add(
               new KeyInfo(var1 + ".name", Material.PAPER, "Set name", null, "https://wiki.advancedplugins.net/configuration/config-items")
                  .setType(KeyType.STRING)
            );
         this.keyInfos
            .add(
               new KeyInfo(var1 + ".lore", Material.BOOK, "Set item's lore", null, "https://wiki.advancedplugins.net/configuration/config-items")
                  .setType(KeyType.LIST)
            );
         this.keyInfos
            .add(
               new KeyInfo(
                     var1 + ".force-glow",
                     Material.GLOWSTONE,
                     "Toggle item's glowing status",
                     Arrays.asList("TRUE", "FALSE"),
                     "https://wiki.advancedplugins.net/configuration/config-items"
                  )
                  .setType(KeyType.BOOLEAN)
            );
         this.keyInfos
            .add(
               new KeyInfo(
                     var1 + ".custom-model-data",
                     Material.COMMAND_BLOCK,
                     "Change item's custom model data",
                     null,
                     "https://wiki.advancedplugins.net/configuration/config-items"
                  )
                  .setType(KeyType.INTEGER)
            );
         this.keyInfos
            .add(
               new KeyInfo(
                     var1 + ".enchantments",
                     Material.ENCHANTING_TABLE,
                     "Change item's enchantments",
                     null,
                     "https://wiki.advancedplugins.net/configuration/config-items"
                  )
                  .setType(KeyType.LIST)
            );
         this.keyInfos
            .add(
               new KeyInfo(
                     var1 + ".custom-enchantments",
                     Material.ENCHANTING_TABLE,
                     "Change item's custom enchantments",
                     null,
                     "https://wiki.advancedplugins.net/configuration/config-items"
                  )
                  .setType(KeyType.LIST)
            );
      }
   }

   public LinkedList<KeyInfo> getKeyInfos() {
      return this.keyInfos;
   }

   public ConfigurationSection getConfig() {
      return this.config;
   }
}
