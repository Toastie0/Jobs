package net.advancedplugins.jobs.impl.actions.objects.variable;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.actions.objects.root.VariableRoot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class Variable {
   private final List<VariableRoot> roots;
   private final Map<String, List<VariableRoot>> subRoots = Maps.newHashMap();
   private final List<String> blacklists = new ArrayList<>();
   private final Map<Variable.ConditionType, List<String>> conditions;

   public Variable(String var1) {
      this(var1, Maps.newHashMap());
   }

   public Variable(String var1, Map<String, String> var2) {
      this(var1, var2, Maps.newHashMap());
   }

   public Variable(String var1, Map<String, String> var2, Map<Variable.ConditionType, List<String>> var3) {
      if (var1.startsWith("!")) {
         this.blacklists.add("root");
         var1 = var1.substring(1);
      }

      this.roots = getRootsFromString(var1);
      var2.forEach((var1x, var2x) -> {
         if (var2x.startsWith("!")) {
            this.blacklists.add(var1x);
            var2x = var2x.substring(1);
         }

         this.subRoots.put(var1x, getRootsFromString(var2x));
      });
      this.conditions = var3;
      if (this.conditions.isEmpty()) {
         Arrays.stream(Variable.ConditionType.values()).forEach(var1x -> this.conditions.put(var1x, new ArrayList<>()));
      }
   }

   private static List<VariableRoot> getRootsFromString(String var0) {
      return Arrays.stream(var0.split(" OR ")).map(VariableRoot::new).collect(Collectors.toList());
   }

   public static Variable of(ConfigurationSection var0, String var1) {
      HashMap var2 = Maps.newHashMap();
      HashMap var3 = Maps.newHashMap();
      Arrays.stream(Variable.ConditionType.values()).forEach(var1x -> var3.put(var1x, new ArrayList()));
      if (var0.get(var1.concat("variable")) instanceof MemorySection) {
         Object var8 = var0.get(var1.concat("variable.root"));
         String var5 = "none";
         if (var8 instanceof List) {
            var5 = String.join(" OR ", (List)var8);
         } else if (var8 != null) {
            var5 = var8.toString();
         }

         ConfigurationSection var6 = var0.getConfigurationSection(var1.concat("variable"));
         var6.getValues(true).forEach((var1x, var2x) -> {
            if (!var1x.equalsIgnoreCase("root") && !var1x.startsWith("conditions")) {
               if (var2x instanceof List) {
                  var2.put(var1x, String.join(" OR ", (List)var2x));
               } else if (var2x != null) {
                  var2.put(var1x, var2x.toString());
               }
            }
         });
         ConfigurationSection var7 = var6.getConfigurationSection("conditions");
         if (var7 != null) {
            Arrays.stream(Variable.ConditionType.values()).forEach(var2x -> var3.put(var2x, var7.getStringList(var2x.name().toLowerCase())));
         }

         return new Variable(var5, var2, var3);
      } else if (var0.contains(var1.concat("variable"))) {
         Object var4 = var0.get(var1.concat("variable"));
         return var4 instanceof List ? new Variable(String.join(" OR ", (List)var4)) : new Variable(var4.toString());
      } else {
         return new Variable("none");
      }
   }

   public List<VariableRoot> getRoots() {
      return this.roots;
   }

   public Map<String, List<VariableRoot>> getSubRoots() {
      return this.subRoots;
   }

   public List<String> getBlacklists() {
      return this.blacklists;
   }

   public Map<Variable.ConditionType, List<String>> getConditions() {
      return this.conditions;
   }

   public static enum ConditionType {
      OR,
      AND;
   }
}
