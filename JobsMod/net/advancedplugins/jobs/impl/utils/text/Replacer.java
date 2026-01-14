package net.advancedplugins.jobs.impl.utils.text;

import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Replacer {
   private final Map<String, Object> variables = Maps.newHashMap();
   private final Map<String, Supplier<Object>> retrievableVariables = Maps.newHashMap();
   private OfflinePlayer player;
   private boolean usePlaceholderApi;

   public static String to(String var0, UnaryOperator<Replacer> var1) {
      return var1.apply(new Replacer()).applyTo(var0);
   }

   public static Replacer of(Map<String, String> var0) {
      Replacer var1 = new Replacer();

      for (Entry var3 : var0.entrySet()) {
         var1.set((String)var3.getKey(), var3.getValue());
      }

      return var1;
   }

   public Replacer set(String var1, Object var2) {
      if (var2 instanceof Iterable) {
         var2 = String.join("<new>", (Iterable<? extends CharSequence>)var2);
      }

      if (var2 instanceof BigDecimal) {
         var2 = ((BigDecimal)var2).stripTrailingZeros().toPlainString();
      }

      this.variables.put("%" + var1 + "%", var2);
      return this;
   }

   public Replacer set(String var1, Supplier<Object> var2) {
      this.retrievableVariables.put("%" + var1 + "%", var2);
      return this;
   }

   public Replacer tryAddPapi(OfflinePlayer var1) {
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         this.player = var1;
         this.usePlaceholderApi = true;
      }

      return this;
   }

   public HashMap<String, String> getPlaceholders() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.variables.entrySet()) {
         var1.put((String)var3.getKey(), String.valueOf(var3.getValue()));
      }

      for (Entry var5 : this.retrievableVariables.entrySet()) {
         var1.put((String)var5.getKey(), Objects.toString(((Supplier)var5.getValue()).get()));
      }

      return var1;
   }

   public String applyTo(String var1) {
      String var2 = var1;

      for (Entry var4 : this.variables.entrySet()) {
         var2 = var2.replace((CharSequence)var4.getKey(), Objects.toString(var4.getValue()));
      }

      for (Entry var6 : this.retrievableVariables.entrySet()) {
         if (var2.contains((CharSequence)var6.getKey())) {
            var2 = var2.replace((CharSequence)var6.getKey(), Objects.toString(((Supplier)var6.getValue()).get()));
         }
      }

      return this.usePlaceholderApi ? PlaceholderAPI.setPlaceholders(this.player, var2) : var2;
   }

   public OfflinePlayer getPlayer() {
      return this.player;
   }

   public boolean isUsePlaceholderApi() {
      return this.usePlaceholderApi;
   }
}
