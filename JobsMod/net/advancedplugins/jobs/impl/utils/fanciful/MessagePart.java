package net.advancedplugins.jobs.impl.utils.fanciful;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public final class MessagePart implements JsonRepresentedObject, ConfigurationSerializable, Cloneable {
   public static final BiMap<ChatColor, String> stylesToNames;
   public ChatColor color = ChatColor.WHITE;
   public ArrayList<ChatColor> styles = new ArrayList<>();
   public String clickActionName = null;
   public String clickActionData = null;
   public String hoverActionName = null;
   public JsonRepresentedObject hoverActionData = null;
   public TextualComponent text = null;
   public String insertionData = null;
   public ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>();

   public MessagePart(TextualComponent var1) {
      this.text = var1;
   }

   public MessagePart() {
      this.text = null;
   }

   public static MessagePart deserialize(Map<String, Object> var0) {
      MessagePart var1 = new MessagePart((TextualComponent)var0.get("text"));
      var1.styles = (ArrayList<ChatColor>)var0.get("styles");
      var1.color = ChatColor.getByChar(var0.get("color").toString());
      var1.hoverActionName = (String)var0.get("hoverActionName");
      var1.hoverActionData = (JsonRepresentedObject)var0.get("hoverActionData");
      var1.clickActionName = (String)var0.get("clickActionName");
      var1.clickActionData = (String)var0.get("clickActionData");
      var1.insertionData = (String)var0.get("insertion");
      var1.translationReplacements = (ArrayList<JsonRepresentedObject>)var0.get("translationReplacements");
      return var1;
   }

   public boolean hasText() {
      return this.text != null;
   }

   public MessagePart clone() {
      MessagePart var1 = (MessagePart)super.clone();
      var1.styles = (ArrayList<ChatColor>)this.styles.clone();
      if (this.hoverActionData instanceof JsonString) {
         var1.hoverActionData = new JsonString(((JsonString)this.hoverActionData).getValue());
      } else if (this.hoverActionData instanceof FancyMessage) {
         var1.hoverActionData = ((FancyMessage)this.hoverActionData).clone();
      }

      var1.translationReplacements = (ArrayList<JsonRepresentedObject>)this.translationReplacements.clone();
      return var1;
   }

   @Override
   public void writeJson(JsonWriter var1) {
      try {
         var1.beginObject();
         this.text.writeJson(var1);
         var1.name("color").value(this.color.name().toLowerCase(Locale.ROOT));

         for (ChatColor var3 : this.styles) {
            var1.name((String)stylesToNames.get(var3)).value(true);
         }

         if (this.clickActionName != null && this.clickActionData != null) {
            var1.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject();
         }

         if (this.hoverActionName != null && this.hoverActionData != null) {
            var1.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value");
            this.hoverActionData.writeJson(var1);
            var1.endObject();
         }

         if (this.insertionData != null) {
            var1.name("insertion").value(this.insertionData);
         }

         if (!this.translationReplacements.isEmpty() && TextualComponent.isTranslatableText(this.text)) {
            var1.name("with").beginArray();

            for (JsonRepresentedObject var6 : this.translationReplacements) {
               var6.writeJson(var1);
            }

            var1.endArray();
         }

         var1.endObject();
      } catch (IOException var4) {
         Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", (Throwable)var4);
      }
   }

   public Map<String, Object> serialize() {
      HashMap var1 = new HashMap();
      var1.put("text", this.text);
      var1.put("styles", this.styles);
      var1.put("color", this.color.getChar());
      var1.put("hoverActionName", this.hoverActionName);
      var1.put("hoverActionData", this.hoverActionData);
      var1.put("clickActionName", this.clickActionName);
      var1.put("clickActionData", this.clickActionData);
      var1.put("insertion", this.insertionData);
      var1.put("translationReplacements", this.translationReplacements);
      return var1;
   }

   static {
      Builder var0 = ImmutableBiMap.builder();

      for (ChatColor var4 : ChatColor.values()) {
         if (var4.isFormat()) {
            var0.put(var4, switch (var4) {
               case MAGIC -> "obfuscated";
               case UNDERLINE -> "underlined";
               default -> var4.name().toLowerCase(Locale.ROOT);
            });
         }
      }

      stylesToNames = var0.build();
      ConfigurationSerialization.registerClass(MessagePart.class);
   }
}
