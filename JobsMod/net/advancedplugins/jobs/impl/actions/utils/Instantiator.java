package net.advancedplugins.jobs.impl.actions.utils;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.plugin.java.JavaPlugin;

@FunctionalInterface
public interface Instantiator<T extends ActionContainer> {
   T init(JavaPlugin var1);
}
