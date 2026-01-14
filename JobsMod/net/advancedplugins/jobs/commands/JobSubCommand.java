package net.advancedplugins.jobs.commands;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.commands.SubCommand;
import net.advancedplugins.jobs.locale.LocaleHandler;
import org.bukkit.command.CommandSender;

public abstract class JobSubCommand<T extends CommandSender> extends SubCommand<T> {
   protected final Core plugin;
   protected final LocaleHandler locale;

   public JobSubCommand(Core var1, String var2, boolean var3) {
      super(var1, var2, var3);
      this.plugin = var1;
      this.locale = var1.getLocale();
   }

   public JobSubCommand(Core var1) {
      this(var1, "", true);
   }

   public JobSubCommand(Core var1, String var2) {
      this(var1, var2, true);
   }

   public JobSubCommand(Core var1, boolean var2) {
      this(var1, "", var2);
   }
}
