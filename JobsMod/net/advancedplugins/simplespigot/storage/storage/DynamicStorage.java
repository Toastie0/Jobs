package net.advancedplugins.simplespigot.storage.storage;

import net.advancedplugins.simplespigot.storage.Backend;

public abstract class DynamicStorage<T> extends Storage<T> {
   public DynamicStorage(Backend var1) {
      super(var1);
   }
}
