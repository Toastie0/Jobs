package net.advancedplugins.jobs.impl.utils.nbt.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AvailableSince {
   MinecraftVersion version();
}
