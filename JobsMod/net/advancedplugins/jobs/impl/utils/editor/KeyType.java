package net.advancedplugins.jobs.impl.utils.editor;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public enum KeyType {
   INTEGER {
      @Override
      public boolean validate(String var1) {
         try {
            Integer.parseInt(var1);
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return Integer.parseInt(var1);
      }

      @Override
      public String getFriendlyName() {
         return "Number";
      }
   },
   BOOLEAN {
      @Override
      public boolean validate(String var1) {
         return "true".equalsIgnoreCase(var1) || "false".equalsIgnoreCase(var1);
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return Boolean.parseBoolean(var1);
      }

      @Override
      public String getFriendlyName() {
         return "true/false";
      }
   },
   LIST {
      @Override
      public boolean validate(String var1) {
         return true;
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return var1;
      }

      @Override
      public String getFriendlyName() {
         return "List";
      }
   },
   DOUBLE {
      @Override
      public boolean validate(String var1) {
         try {
            Double.parseDouble(var1);
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return Double.parseDouble(var1);
      }

      @Override
      public String getFriendlyName() {
         return "Decimal number";
      }
   },
   KEY {
      @Override
      public boolean validate(String var1) {
         return true;
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         var2.createSection(var1);
         return KeyType.KEY;
      }

      @Override
      public String getFriendlyName() {
         return "Config Section";
      }
   },
   ITEM {
      @Override
      public boolean validate(String var1) {
         return true;
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         var2.createSection(var1);
         return KeyType.KEY;
      }

      @Override
      public String getFriendlyName() {
         return "Item";
      }
   },
   STRING {
      @Override
      public boolean validate(String var1) {
         return true;
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return var1;
      }

      @Override
      public String getFriendlyName() {
         return "Text";
      }
   },
   MATERIAL {
      @Override
      public boolean validate(String var1) {
         return Material.matchMaterial(var1) != null;
      }

      @Override
      public Object process(String var1, ConfigurationSection var2) {
         return Material.matchMaterial(var1).name();
      }

      @Override
      public String getFriendlyName() {
         return "Material";
      }
   };

   public abstract String getFriendlyName();

   public abstract boolean validate(String var1);

   public abstract Object process(String var1, ConfigurationSection var2);

   public static KeyType getKeyType(Object var0) {
      if (var0 instanceof Integer) {
         return INTEGER;
      } else if (var0 instanceof Boolean) {
         return BOOLEAN;
      } else if (var0 instanceof List) {
         return LIST;
      } else if (var0 instanceof String) {
         return STRING;
      } else {
         return var0 instanceof ConfigurationSection ? KEY : null;
      }
   }
}
