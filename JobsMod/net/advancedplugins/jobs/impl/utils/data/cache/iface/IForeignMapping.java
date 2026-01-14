package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.data.cache.ForeignMapper;

public interface IForeignMapping {
   Map<ForeignCollection<? extends ICached<Integer>>, Collection<? extends ICached<Integer>>> getForeignMapping();

   List<ForeignMapper<?>> getForeignMappers();
}
