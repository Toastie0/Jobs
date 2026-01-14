package net.advancedplugins.simplespigot.config;

import java.util.List;
import java.util.function.Consumer;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.simplespigot.item.SpigotItem;
import org.bukkit.inventory.ItemStack;

public class ConfigLoader {
   public static ConfigLoader.Reader reader(Config var0) {
      return new ConfigLoader.Reader(var0);
   }

   public static class Reader {
      private final Config config;
      private String currentPath = "";

      public Reader(Config var1) {
         this.config = var1;
      }

      public ConfigLoader.Reader readWrap(Consumer<ConfigLoader.Reader> var1) {
         var1.accept(this);
         return this;
      }

      public String getCurrentPath() {
         return this.currentPath;
      }

      public void setCurrentPath(String var1) {
         this.currentPath = var1;
      }

      public boolean has(String var1) {
         return this.config.has(this.currentPath.concat(".").concat(var1));
      }

      public String string(String var1) {
         return this.config.string(this.currentPath.concat(".").concat(var1));
      }

      public String string() {
         return this.config.string(this.currentPath);
      }

      public int integer(String var1) {
         return this.config.integer(this.currentPath.concat(".").concat(var1));
      }

      public int integer() {
         return this.config.integer(this.currentPath);
      }

      public List<String> list(String var1) {
         return this.config.list(this.currentPath.concat(".").concat(var1));
      }

      public List<String> list() {
         return this.config.list(this.currentPath);
      }

      public ItemStack getItem(String var1) {
         return this.getItem(var1, null);
      }

      public ItemStack getItem(String var1, Replace var2) {
         return this.getItem(var1, var2, false);
      }

      public ItemStack getItem(String var1, Replace var2, boolean var3) {
         return SpigotItem.toItemOrDefault(this.config, var1.isEmpty() ? this.currentPath : this.currentPath.concat(".").concat(var1), var2, null, var3);
      }

      public ConfigLoader.Reader keyLoop(String var1, boolean var2, Consumer<String> var3) {
         if (this.currentPath.isEmpty()) {
            this.currentPath = var1;
         } else if (!var1.isEmpty()) {
            this.currentPath = this.currentPath + ".".concat(var1);
         }

         for (String var5 : this.config.keys(var1, var2)) {
            this.currentPath = var1.isEmpty() ? var5 : var1.concat(".").concat(var5);
            var3.accept(var5);
         }

         return this;
      }

      public ConfigLoader.Reader keyLoop(String var1, Consumer<String> var2) {
         return this.keyLoop(var1, false, var2);
      }

      public ConfigLoader.Reader keyLoop(boolean var1, Consumer<String> var2) {
         return this.keyLoop("", var1, var2);
      }

      public ConfigLoader.Reader keyLoop(Consumer<String> var1) {
         return this.keyLoop("", false, var1);
      }
   }
}
