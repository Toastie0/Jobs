package net.advancedplugins.jobs.menus.menus;

import java.util.ArrayList;
import java.util.Collection;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.utils.tuple.ImmutablePair;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.jobs.menus.service.extensions.PageableConfigMenu;
import net.advancedplugins.jobs.objects.job.CommunityJob;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommunityJobsMenu extends PageableConfigMenu<CommunityJob> implements UserDependent {
   private final JobsResetHandler jobsResetHandler;
   private final JobController jobController;
   private final LocaleHandler lang;
   private final User user;

   public CommunityJobsMenu(Core var1, Config var2, Player var3) {
      super(var1, var2, var3, var1x -> var1x.tryAddPapi(var3));
      this.jobsResetHandler = var1.getJobsResetHandler();
      this.jobController = var1.getJobController();
      this.lang = var1.getLocale();
      this.user = var1.getUserCache().getOrThrow(var3.getUniqueId());
      this.addUpdater(var1, 20);
   }

   @Override
   public void redraw() {
      this.drawPageableItems(
         () -> this.drawConfigItems(var1 -> var1.set("time_left", this.jobsResetHandler.asString()).set("page", this.getPage()).tryAddPapi(this.player))
      );
   }

   public MenuItem pageableItem(CommunityJob var1) {
      return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
   }

   @Override
   public ImmutablePair<Collection<CommunityJob>, Collection<Integer>> elementalValues() {
      return ImmutablePair.of(new ArrayList<>(), new ArrayList<>());
   }

   @Override
   public boolean isUserViable() {
      return this.user != null;
   }
}
