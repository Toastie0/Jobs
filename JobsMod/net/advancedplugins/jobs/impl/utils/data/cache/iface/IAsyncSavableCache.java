package net.advancedplugins.jobs.impl.utils.data.cache.iface;

public interface IAsyncSavableCache<K, V extends ICached<K>> extends IAsyncSavable<K, V>, IAsyncCache<K, V> {
}
