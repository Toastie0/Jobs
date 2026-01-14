package net.advancedplugins.jobs.impl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectMatcher {
   private static final HashMap<PotionEffectType, List<String>> potionAliases = new HashMap<>();

   public static PotionEffectType matchPotion(String var0) {
      try {
         var0 = var0.toUpperCase(Locale.ROOT);
         if (potionAliases.isEmpty()) {
            init();
         }

         for (Entry var2 : potionAliases.entrySet()) {
            for (String var4 : (List)var2.getValue()) {
               if (var4.equalsIgnoreCase(var0)) {
                  return (PotionEffectType)var2.getKey();
               }
            }
         }

         return PotionEffectType.getByName(var0);
      } catch (Exception var5) {
         var5.printStackTrace();
         return PotionEffectType.getByName(var0);
      }
   }

   private static void init() {
      for (PotionEffectType var3 : PotionEffectType.values()) {
         ArrayList var4 = new ArrayList();
         String var5 = var3.getName().toUpperCase(Locale.ROOT);
         switch (var5) {
            case "CONFUSION":
               var4.add("NAUSEA");
               break;
            case "DAMAGE_RESISTANCE":
               var4.add("RESISTANCE");
               var4.add("RES");
               break;
            case "FAST_DIGGING":
               var4.add("HASTE");
               break;
            case "FIRE_RESISTANCE":
               var4.add("FIRE_RESISTANCE");
               var4.add("FIRE_RES");
               break;
            case "HARM":
               var4.add("HARMNESS");
               break;
            case "INCREASE_DAMAGE":
               var4.add("STRENGTH");
               var4.add("STRENGHT");
               break;
            case "JUMP":
               var4.add("JUMP_BOOST");
               break;
            case "SLOW":
               var4.add("SLOWNESS");
               break;
            case "BLINDNESS":
               var4.add("BLIND");
         }

         if (!var4.isEmpty()) {
            potionAliases.put(var3, var4);
         }
      }
   }
}
