package net.advancedplugins.jobs.impl.utils.actions;

@FunctionalInterface
public interface ActionConstructor {
   Action construct(String var1, String var2, double var3);
}
