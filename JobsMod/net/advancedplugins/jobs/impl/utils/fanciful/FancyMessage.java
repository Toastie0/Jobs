package net.advancedplugins.jobs.impl.utils.fanciful;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable {
   private static final JsonParser _stringParser = new JsonParser();
   private List<MessagePart> messageParts = new ArrayList<>();
   private String jsonString;
   private boolean dirty;

   public FancyMessage(String var1) {
      this(TextualComponent.rawText(var1));
   }

   private FancyMessage(TextualComponent var1) {
      this.messageParts.add(new MessagePart(var1));
      this.jsonString = null;
      this.dirty = false;
   }

   private FancyMessage() {
      this((TextualComponent)null);
   }

   public static FancyMessage deserialize(Map<String, Object> var0) {
      FancyMessage var1 = new FancyMessage();
      var1.messageParts = (List<MessagePart>)var0.get("messageParts");
      var1.jsonString = var0.containsKey("JSON") ? var0.get("JSON").toString() : null;
      var1.dirty = !var0.containsKey("JSON");
      return var1;
   }

   private static FancyMessage deserialize(String var0) {
      JsonObject var1 = _stringParser.parse(var0).getAsJsonObject();
      JsonArray var2 = var1.getAsJsonArray("extra");
      FancyMessage var3 = new FancyMessage();
      var3.messageParts.clear();

      for (JsonElement var5 : var2) {
         MessagePart var6 = new MessagePart();
         JsonObject var7 = var5.getAsJsonObject();

         for (Entry var9 : var7.entrySet()) {
            if (TextualComponent.isTextKey((String)var9.getKey())) {
               HashMap var15 = new HashMap();
               var15.put("key", var9.getKey());
               if (((JsonElement)var9.getValue()).isJsonPrimitive()) {
                  var15.put("value", ((JsonElement)var9.getValue()).getAsString());
               } else {
                  for (Entry var12 : ((JsonElement)var9.getValue()).getAsJsonObject().entrySet()) {
                     var15.put("value." + (String)var12.getKey(), ((JsonElement)var12.getValue()).getAsString());
                  }
               }

               var6.text = TextualComponent.deserialize(var15);
            } else if (MessagePart.stylesToNames.inverse().containsKey(var9.getKey())) {
               if (((JsonElement)var9.getValue()).getAsBoolean()) {
                  var6.styles.add((ChatColor)MessagePart.stylesToNames.inverse().get(var9.getKey()));
               }
            } else if (((String)var9.getKey()).equals("color")) {
               var6.color = ChatColor.valueOf(((JsonElement)var9.getValue()).getAsString().toUpperCase(Locale.ROOT));
            } else if (((String)var9.getKey()).equals("clickEvent")) {
               JsonObject var14 = ((JsonElement)var9.getValue()).getAsJsonObject();
               var6.clickActionName = var14.get("action").getAsString();
               var6.clickActionData = var14.get("value").getAsString();
            } else if (((String)var9.getKey()).equals("hoverEvent")) {
               JsonObject var13 = ((JsonElement)var9.getValue()).getAsJsonObject();
               var6.hoverActionName = var13.get("action").getAsString();
               if (var13.get("value").isJsonPrimitive()) {
                  var6.hoverActionData = new JsonString(var13.get("value").getAsString());
               } else {
                  var6.hoverActionData = deserialize(var13.get("value").toString());
               }
            } else if (((String)var9.getKey()).equals("insertion")) {
               var6.insertionData = ((JsonElement)var9.getValue()).getAsString();
            } else if (((String)var9.getKey()).equals("with")) {
               for (JsonElement var11 : ((JsonElement)var9.getValue()).getAsJsonArray()) {
                  if (var11.isJsonPrimitive()) {
                     var6.translationReplacements.add(new JsonString(var11.getAsString()));
                  } else {
                     var6.translationReplacements.add(deserialize(var11.toString()));
                  }
               }
            }
         }

         var3.messageParts.add(var6);
      }

      return var3;
   }

   public FancyMessage clone() {
      FancyMessage var1 = (FancyMessage)super.clone();
      var1.messageParts = new ArrayList<>(this.messageParts.size());

      for (int var2 = 0; var2 < this.messageParts.size(); var2++) {
         var1.messageParts.add(var2, this.messageParts.get(var2).clone());
      }

      var1.dirty = false;
      var1.jsonString = null;
      return var1;
   }

   public FancyMessage text(String var1) {
      MessagePart var2 = this.latest();
      var2.text = TextualComponent.rawText(var1);
      this.dirty = true;
      return this;
   }

   public FancyMessage text(TextualComponent var1) {
      MessagePart var2 = this.latest();
      var2.text = var1;
      this.dirty = true;
      return this;
   }

   public FancyMessage color(ChatColor var1) {
      if (!var1.isColor()) {
         throw new IllegalArgumentException(var1.name() + " is not a color");
      } else {
         this.latest().color = var1;
         this.dirty = true;
         return this;
      }
   }

   public FancyMessage style(ChatColor... var1) {
      for (ChatColor var5 : var1) {
         if (!var5.isFormat()) {
            throw new IllegalArgumentException(var5.name() + " is not a style");
         }
      }

      this.latest().styles.addAll(Arrays.asList(var1));
      this.dirty = true;
      return this;
   }

   public FancyMessage file(String var1) {
      this.onClick("open_file", var1);
      return this;
   }

   public FancyMessage link(String var1) {
      this.onClick("open_url", var1);
      return this;
   }

   public FancyMessage suggest(String var1) {
      this.onClick("suggest_command", var1);
      return this;
   }

   public FancyMessage insert(String var1) {
      this.latest().insertionData = var1;
      this.dirty = true;
      return this;
   }

   public FancyMessage command(String var1) {
      this.onClick("run_command", var1);
      return this;
   }

   public FancyMessage achievementTooltip(String var1) {
      this.onHover("show_achievement", new JsonString("achievement." + var1));
      return this;
   }

   private FancyMessage tooltip(String var1) {
      this.onHover("show_text", new JsonString(var1));
      return this;
   }

   public FancyMessage tooltip(Iterable<String> var1) {
      this.tooltip(ArrayWrapper.toArray(var1, String.class));
      return this;
   }

   public FancyMessage tooltip(String... var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2.append(var1[var3]);
         if (var3 != var1.length - 1) {
            var2.append('\n');
         }
      }

      this.tooltip(var2.toString());
      return this;
   }

   private FancyMessage formattedTooltip(FancyMessage var1) {
      for (MessagePart var3 : var1.messageParts) {
         if (var3.clickActionData != null && var3.clickActionName != null) {
            throw new IllegalArgumentException("The tooltip text cannot have click data.");
         }

         if (var3.hoverActionData != null && var3.hoverActionName != null) {
            throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
         }
      }

      this.onHover("show_text", var1);
      return this;
   }

   private FancyMessage formattedTooltip(FancyMessage... var1) {
      if (var1.length < 1) {
         this.onHover(null, null);
         return this;
      } else {
         FancyMessage var2 = new FancyMessage();
         var2.messageParts.clear();

         for (int var3 = 0; var3 < var1.length; var3++) {
            try {
               for (MessagePart var5 : var1[var3]) {
                  if (var5.clickActionData != null && var5.clickActionName != null) {
                     throw new IllegalArgumentException("The tooltip text cannot have click data.");
                  }

                  if (var5.hoverActionData != null && var5.hoverActionName != null) {
                     throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
                  }

                  if (var5.hasText()) {
                     var2.messageParts.add(var5.clone());
                  }
               }

               if (var3 != var1.length - 1) {
                  var2.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
               }
            } catch (CloneNotSupportedException var6) {
               Bukkit.getLogger().log(Level.WARNING, "Failed to clone object", (Throwable)var6);
               return this;
            }
         }

         return this.formattedTooltip(var2.messageParts.isEmpty() ? null : var2);
      }
   }

   public FancyMessage formattedTooltip(Iterable<FancyMessage> var1) {
      return this.formattedTooltip(ArrayWrapper.toArray(var1, FancyMessage.class));
   }

   public FancyMessage translationReplacements(String... var1) {
      for (String var5 : var1) {
         this.latest().translationReplacements.add(new JsonString(var5));
      }

      this.dirty = true;
      return this;
   }

   private FancyMessage translationReplacements(FancyMessage... var1) {
      Collections.addAll(this.latest().translationReplacements, var1);
      this.dirty = true;
      return this;
   }

   public FancyMessage translationReplacements(Iterable<FancyMessage> var1) {
      return this.translationReplacements(ArrayWrapper.toArray(var1, FancyMessage.class));
   }

   public FancyMessage then(String var1) {
      return this.then(TextualComponent.rawText(var1));
   }

   private FancyMessage then(TextualComponent var1) {
      if (!this.latest().hasText()) {
         throw new IllegalStateException("previous message part has no text");
      } else {
         this.messageParts.add(new MessagePart(var1));
         this.dirty = true;
         return this;
      }
   }

   public FancyMessage then() {
      if (!this.latest().hasText()) {
         throw new IllegalStateException("previous message part has no text");
      } else {
         this.messageParts.add(new MessagePart());
         this.dirty = true;
         return this;
      }
   }

   @Override
   public void writeJson(JsonWriter var1) {
      if (this.messageParts.size() == 1) {
         this.latest().writeJson(var1);
      } else {
         var1.beginObject().name("text").value("").name("extra").beginArray();

         for (MessagePart var3 : this) {
            var3.writeJson(var1);
         }

         var1.endArray().endObject();
      }
   }

   private String toJSONString() {
      if (!this.dirty && this.jsonString != null) {
         return this.jsonString;
      } else {
         StringWriter var1 = new StringWriter();
         JsonWriter var2 = new JsonWriter(var1);

         try {
            this.writeJson(var2);
            var2.close();
         } catch (IOException var4) {
            throw new RuntimeException("invalid message");
         }

         this.jsonString = var1.toString();
         this.dirty = false;
         return this.jsonString;
      }
   }

   public void send(Player var1) {
      this.send(var1, this.toJSONString());
   }

   private void send(CommandSender var1, String var2) {
      if (!(var1 instanceof Player var3)) {
         var1.sendMessage(this.toOldMessageFormat());
      } else {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + var3.getName() + " " + var2);
      }
   }

   public void send(CommandSender var1) {
      this.send(var1, this.toJSONString());
   }

   public void send(Iterable<? extends CommandSender> var1) {
      String var2 = this.toJSONString();

      for (CommandSender var4 : var1) {
         this.send(var4, var2);
      }
   }

   private String toOldMessageFormat() {
      StringBuilder var1 = new StringBuilder();

      for (MessagePart var3 : this) {
         var1.append(var3.color == null ? "" : var3.color);

         for (ChatColor var5 : var3.styles) {
            var1.append(var5);
         }

         var1.append(var3.text);
      }

      return var1.toString();
   }

   private MessagePart latest() {
      return this.messageParts.get(this.messageParts.size() - 1);
   }

   private void onClick(String var1, String var2) {
      MessagePart var3 = this.latest();
      var3.clickActionName = var1;
      var3.clickActionData = var2;
      this.dirty = true;
   }

   private void onHover(String var1, JsonRepresentedObject var2) {
      MessagePart var3 = this.latest();
      var3.hoverActionName = var1;
      var3.hoverActionData = var2;
      this.dirty = true;
   }

   public Map<String, Object> serialize() {
      HashMap var1 = new HashMap();
      var1.put("messageParts", this.messageParts);
      return var1;
   }

   @Override
   public Iterator<MessagePart> iterator() {
      return this.messageParts.iterator();
   }

   static {
      ConfigurationSerialization.registerClass(FancyMessage.class);
   }
}
