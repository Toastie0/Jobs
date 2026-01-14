package net.advancedplugins.jobs.impl.utils.fanciful;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.stream.JsonWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public abstract class TextualComponent implements Cloneable {
   public static TextualComponent deserialize(Map<String, Object> var0) {
      if (var0.containsKey("key") && var0.size() == 2 && var0.containsKey("value")) {
         return TextualComponent.ArbitraryTextTypeComponent.deserialize(var0);
      } else {
         return var0.size() >= 2 && var0.containsKey("key") && !var0.containsKey("value") ? TextualComponent.ComplexTextTypeComponent.deserialize(var0) : null;
      }
   }

   public static boolean isTextKey(String var0) {
      return var0.equals("translate") || var0.equals("text") || var0.equals("score") || var0.equals("selector");
   }

   public static boolean isTranslatableText(TextualComponent var0) {
      return var0 instanceof TextualComponent.ComplexTextTypeComponent && var0.getKey().equals("translate");
   }

   public static TextualComponent rawText(String var0) {
      return new TextualComponent.ArbitraryTextTypeComponent("text", var0);
   }

   public static TextualComponent localizedText(String var0) {
      return new TextualComponent.ArbitraryTextTypeComponent("translate", var0);
   }

   private static void throwUnsupportedSnapshot() {
      throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
   }

   public static TextualComponent objectiveScore(String var0) {
      return objectiveScore("*", var0);
   }

   private static TextualComponent objectiveScore(String var0, String var1) {
      throwUnsupportedSnapshot();
      return new TextualComponent.ComplexTextTypeComponent("score", ImmutableMap.builder().put("name", var0).put("objective", var1).build());
   }

   public static TextualComponent selector(String var0) {
      throwUnsupportedSnapshot();
      return new TextualComponent.ArbitraryTextTypeComponent("selector", var0);
   }

   @Override
   public String toString() {
      return this.getReadableString();
   }

   public abstract String getKey();

   protected abstract String getReadableString();

   public abstract TextualComponent clone();

   public abstract void writeJson(JsonWriter var1);

   static {
      ConfigurationSerialization.registerClass(TextualComponent.ArbitraryTextTypeComponent.class);
      ConfigurationSerialization.registerClass(TextualComponent.ComplexTextTypeComponent.class);
   }

   private static final class ArbitraryTextTypeComponent extends TextualComponent implements ConfigurationSerializable {
      private String _key;
      private String _value;

      ArbitraryTextTypeComponent(String var1, String var2) {
         this.setKey(var1);
         this.setValue(var2);
      }

      public static TextualComponent.ArbitraryTextTypeComponent deserialize(Map<String, Object> var0) {
         return new TextualComponent.ArbitraryTextTypeComponent(var0.get("key").toString(), var0.get("value").toString());
      }

      @Override
      public String getKey() {
         return this._key;
      }

      void setKey(String var1) {
         Preconditions.checkArgument(var1 != null && !var1.isEmpty(), "The key must be specified.");
         this._key = var1;
      }

      String getValue() {
         return this._value;
      }

      void setValue(String var1) {
         Preconditions.checkArgument(var1 != null, "The value must be specified.");
         this._value = var1;
      }

      @Override
      public TextualComponent clone() {
         return new TextualComponent.ArbitraryTextTypeComponent(this.getKey(), this.getValue());
      }

      @Override
      public void writeJson(JsonWriter var1) {
         var1.name(this.getKey()).value(this.getValue());
      }

      public Map<String, Object> serialize() {
         return new HashMap<String, Object>() {
            {
               this.put("key", ArbitraryTextTypeComponent.this.getKey());
               this.put("value", ArbitraryTextTypeComponent.this.getValue());
            }
         };
      }

      @Override
      public String getReadableString() {
         return this.getValue();
      }
   }

   private static final class ComplexTextTypeComponent extends TextualComponent implements ConfigurationSerializable {
      private String _key;
      private Map<String, String> _value;

      ComplexTextTypeComponent(String var1, Map<String, String> var2) {
         this.setKey(var1);
         this.setValue(var2);
      }

      public static TextualComponent.ComplexTextTypeComponent deserialize(Map<String, Object> var0) {
         String var1 = null;
         HashMap var2 = new HashMap();

         for (Entry var4 : var0.entrySet()) {
            if (((String)var4.getKey()).equals("key")) {
               var1 = (String)var4.getValue();
            } else if (((String)var4.getKey()).startsWith("value.")) {
               var2.put(((String)var4.getKey()).substring(6), var4.getValue().toString());
            }
         }

         return new TextualComponent.ComplexTextTypeComponent(var1, var2);
      }

      @Override
      public String getKey() {
         return this._key;
      }

      void setKey(String var1) {
         Preconditions.checkArgument(var1 != null && !var1.isEmpty(), "The key must be specified.");
         this._key = var1;
      }

      Map<String, String> getValue() {
         return this._value;
      }

      void setValue(Map<String, String> var1) {
         Preconditions.checkArgument(var1 != null, "The value must be specified.");
         this._value = var1;
      }

      @Override
      public TextualComponent clone() {
         return new TextualComponent.ComplexTextTypeComponent(this.getKey(), this.getValue());
      }

      @Override
      public void writeJson(JsonWriter var1) {
         var1.name(this.getKey());
         var1.beginObject();

         for (Entry var3 : this._value.entrySet()) {
            var1.name((String)var3.getKey()).value((String)var3.getValue());
         }

         var1.endObject();
      }

      public Map<String, Object> serialize() {
         return new HashMap<String, Object>() {
            {
               this.put("key", ComplexTextTypeComponent.this.getKey());

               for (Entry var3 : ComplexTextTypeComponent.this.getValue().entrySet()) {
                  this.put("value." + (String)var3.getKey(), var3.getValue());
               }
            }
         };
      }

      @Override
      public String getReadableString() {
         return this.getKey();
      }
   }
}
