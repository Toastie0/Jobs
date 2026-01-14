package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import com.j256.ormlite.dao.ForeignCollection;
import java.util.Collection;
import net.advancedplugins.jobs.impl.utils.data.cache.ForeignMapper;

public interface IForeignMappingHandler {
   void dbToJava(IForeignMapping var1);

   void javaToDb(IForeignMapping var1);

   void refreshForeign(ForeignCollection<? extends ICached<Integer>> var1, Collection<? extends ICached<Integer>> var2);

   void refreshForeign(ForeignMapper<?> var1);
}
