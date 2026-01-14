package net.advancedplugins.simplespigot.menu.item;

import java.util.Arrays;
import net.advancedplugins.jobs.impl.utils.items.ItemFlagFix;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.item.SpigotItem;
import net.advancedplugins.simplespigot.menu.Menu;
import net.advancedplugins.simplespigot.menu.item.click.ClickAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuItem {
   private final ItemStack itemStack;
   private final int row;
   private final int slot;
   private final ClickAction clickAction;

   public MenuItem(ItemStack var1, int var2, int var3, ClickAction var4) {
      this.itemStack = var1;
      this.hideAttributes();
      this.row = var2;
      this.slot = var3;
      this.clickAction = var4;
   }

   private void hideAttributes() {
      ItemFlagFix.fix(this.itemStack);
      ItemMeta var1 = this.itemStack.getItemMeta();
      if (var1 != null) {
         Arrays.asList(
               ItemFlag.HIDE_ATTRIBUTES,
               ItemFlag.HIDE_ENCHANTS,
               ItemFlag.HIDE_DESTROYS,
               ItemFlag.HIDE_PLACED_ON,
               ItemFlag.HIDE_DYE,
               ItemFlag.HIDE_POTION_EFFECTS,
               ItemFlag.HIDE_UNBREAKABLE
            )
            .forEach(var1x -> var1.addItemFlags(new ItemFlag[]{var1x}));
         this.itemStack.setItemMeta(var1);
      }
   }

   public static MenuItem of(ItemStack var0) {
      return new MenuItem(var0, 1, 1, (var0x, var1) -> {});
   }

   public static MenuItem.Builder builderOf(ItemStack var0) {
      return new MenuItem.Builder(new MenuItem(var0, 1, 1, (var0x, var1) -> {}));
   }

   public static MenuItem.Builder builder() {
      return new MenuItem.Builder();
   }

   public static MenuItem.Builder builderOf(MenuItem var0) {
      return new MenuItem.Builder(var0);
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public int getRow() {
      return this.row;
   }

   public int getSlot() {
      return this.slot;
   }

   public ClickAction getClickAction() {
      return this.clickAction;
   }

   public static class Builder {
      private ItemStack itemStack;
      private int slot;
      private int row;
      private ClickAction clickAction;

      public Builder() {
      }

      public Builder(MenuItem var1) {
         this.itemStack = var1.getItemStack();
         this.slot = var1.getSlot();
         this.row = var1.getRow();
         this.clickAction = var1.getClickAction();
      }

      public MenuItem.Builder item(SpigotItem.Builder var1) {
         this.itemStack = var1.build();
         return this;
      }

      public MenuItem.Builder item(Config var1, String var2) {
         this.itemStack = SpigotItem.toItem(var1, var2);
         return this;
      }

      public MenuItem.Builder item(Config var1, String var2, Replace var3) {
         this.itemStack = SpigotItem.toItem(var1, var2, var3);
         return this;
      }

      public MenuItem.Builder slot(int var1) {
         this.slot = var1;
         return this;
      }

      public MenuItem.Builder row(int var1) {
         this.row = var1;
         return this;
      }

      public MenuItem.Builder rawSlot(int var1) {
         this.row = var1 / 9 + 1;
         this.slot = -9 * this.row + 10 + var1;
         return this;
      }

      public MenuItem.Builder grid(int var1, int var2) {
         this.slot = var1;
         this.row = var2;
         return this;
      }

      public MenuItem.Builder onClick(ClickAction var1) {
         this.clickAction = var1;
         return this;
      }

      public MenuItem build() {
         return new MenuItem(this.itemStack, this.row, this.slot, this.clickAction);
      }

      public void buildTo(Menu var1) {
         var1.item(this.build());
      }
   }
}
