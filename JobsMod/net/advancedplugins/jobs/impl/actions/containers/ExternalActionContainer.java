package net.advancedplugins.jobs.impl.actions.containers;

import org.bukkit.plugin.java.JavaPlugin;

public class ExternalActionContainer extends ActionContainer {
   protected final String prefix;

   protected ExternalActionContainer(JavaPlugin var1, String var2) {
      super(var1);
      this.prefix = var2.concat("_");
   }

   public String getPrefix() {
      return this.prefix;
   }
}
