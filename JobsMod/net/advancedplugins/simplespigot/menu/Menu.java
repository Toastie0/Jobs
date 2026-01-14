package net.advancedplugins.simplespigot.menu;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class Menu implements InventoryHolder {
   protected final Player player;
   private String title;
   private int rows;
   private Inventory inventory;
   private Menu.MenuState menuState;
   private Runnable closeAction;
   private Map<Integer, MenuItem> menuItems = Maps.newHashMap();
   private BukkitTask updater;

   public Menu(Player var1, String var2, int var3) {
      this.player = var1;
      this.menuState = Menu.MenuState.RAW;
      this.title = Text.parsePapi(var2, var1);
      this.rows = var3;
   }

   public abstract void redraw();

   public Inventory getInventory() {
      return this.inventory;
   }

   public void show() {
      if (this.menuState.isRaw()) {
         this.inventory = Bukkit.createInventory(this, this.rows * 9, this.title);
         this.redraw();
         this.menuState = Menu.MenuState.ACTIVE;
      }

      this.player.openInventory(this.inventory);
   }

   public void close() {
      this.player.closeInventory();
   }

   public void preTitle(String var1) {
      this.title = var1;
   }

   public void preRows(int var1) {
      this.rows = var1;
   }

   public MenuItem getMenuItem(int var1) {
      return this.menuItems.get(var1);
   }

   public Runnable getCloseAction() {
      return this.closeAction;
   }

   public void setCloseAction(Runnable var1) {
      this.closeAction = var1;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public int getRows() {
      return this.rows;
   }

   public Menu.MenuState getMenuState() {
      return this.menuState;
   }

   public void addUpdater(Plugin var1, int var2) {
      this.updater = Bukkit.getScheduler().runTaskTimer(var1, () -> {
         if (this.inventory.getHolder() != null && this.inventory.getHolder().equals(this.player.getOpenInventory().getTopInventory().getHolder())) {
            this.redraw();
         } else {
            this.updater.cancel();
         }
      }, 0L, var2);
   }

   public void flush() {
      for (int var2 : this.menuItems.keySet()) {
         this.inventory.setItem(var2, null);
      }

      this.menuItems.clear();
   }

   public void flush(int var1, int var2) {
      this.flush(this.gridToSlot(var1, var2));
   }

   public void flush(int var1) {
      if (this.menuItems.containsKey(var1)) {
         this.menuItems.remove(var1);
         this.inventory.setItem(var1, null);
      }
   }

   public void enduringRun(Runnable var1) {
      if (this.menuState.isRaw()) {
         var1.run();
      }
   }

   public <T> T enduringSupply(Supplier<T> var1) {
      return (T)(this.menuState.isRaw() ? var1.get() : null);
   }

   public void item(MenuItem var1) {
      int var2 = this.gridToSlot(var1.getSlot(), var1.getRow());
      this.menuItems.put(var2, var1);
      this.inventory.setItem(var2, var1.getItemStack());
   }

   public Optional<Integer> firstEmpty() {
      int var1 = this.inventory.firstEmpty();
      return var1 < 0 ? Optional.empty() : Optional.of(var1);
   }

   public void loopEmptyRawSlots(Consumer<Integer> var1) {
      while (this.firstEmpty().isPresent()) {
         var1.accept(this.firstEmpty().get());
      }
   }

   private int gridToSlot(int var1, int var2) {
      return 9 * (var2 - 1) + (var1 - 1);
   }

   public static enum MenuState {
      RAW,
      ACTIVE;

      public boolean isRaw() {
         return this.equals(RAW);
      }

      public boolean isActive() {
         return this.equals(ACTIVE);
      }
   }
}
