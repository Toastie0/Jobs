package net.advancedplugins.jobs.impl.utils.fanciful;

import com.google.gson.stream.JsonWriter;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class JsonString implements JsonRepresentedObject, ConfigurationSerializable {
   private final String _value;

   public JsonString(CharSequence var1) {
      this._value = var1 == null ? null : var1.toString();
   }

   public static JsonString deserialize(Map<String, Object> var0) {
      return new JsonString(var0.get("stringValue").toString());
   }

   @Override
   public void writeJson(JsonWriter var1) {
      var1.value(this.getValue());
   }

   public String getValue() {
      return this._value;
   }

   public Map<String, Object> serialize() {
      HashMap var1 = new HashMap();
      var1.put("stringValue", this._value);
      return var1;
   }

   @Override
   public String toString() {
      return this._value;
   }
}
