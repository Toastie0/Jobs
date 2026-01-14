package net.advancedplugins.jobs.impl.utils.data.cache.iface;

public interface ISavableCache<K, V extends ICached<K>> extends ISavable<K, V>, ICache<K, V> {
}
