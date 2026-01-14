package net.advancedplugins.jobs.impl.utils.data.cache;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ICached;

public record ForeignMapper<T extends ICached<Integer>>(ForeignCollection<T> foreign, ConcurrentMap<String, T> cache, Function<T, String> keyExtractor) {
}
