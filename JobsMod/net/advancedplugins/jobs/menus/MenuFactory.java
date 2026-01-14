package net.advancedplugins.jobs.menus;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.menus.menus.FreeJobsMenu;
import net.advancedplugins.jobs.menus.menus.LeaderboardMenu;
import net.advancedplugins.jobs.menus.menus.PortalMenu;
import net.advancedplugins.jobs.menus.menus.PremiumJobsMenu;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class MenuFactory {
   private final Core plugin;
   private final Map<UUID, Menu> openMenus = Maps.newHashMap();
   private final Map<Collection<String>, Function<Player, Menu>> menus = Maps.newHashMap();

   public MenuFactory(Core var1) {
      this.plugin = var1;
      this.putDefaults();
   }

   public Menu createMenu(String var1, Player var2) {
      for (Entry var4 : this.menus.entrySet()) {
         if (((Collection)var4.getKey()).contains(var1)) {
            return this.initiateMenu(var2, () -> (Menu)((Function)var4.getValue()).apply(var2));
         }
      }

      return null;
   }

   public Map<UUID, Menu> getOpenMenus() {
      return this.openMenus;
   }

   public Menu initiateMenu(Player var1, Supplier<Menu> var2) {
      Menu var3 = (Menu)var2.get();
      this.openMenus.put(var1.getUniqueId(), var3);
      var3.setCloseAction(() -> {
         if (var3.equals(this.openMenus.get(var1.getUniqueId()))) {
            this.openMenus.remove(var1.getUniqueId());
         }
      });
      return var3;
   }

   private void putDefaults() {
      this.menus.put(Sets.newHashSet(new String[]{"portal"}), var1 -> new PortalMenu(this.plugin, this.plugin.getConfig("portal-menu"), var1));
      this.menus.put(Sets.newHashSet(new String[]{"free", "free-jobs"}), var1 -> new FreeJobsMenu(this.plugin, this.plugin.getConfig("free-jobs-menu"), var1));
      this.menus
         .put(
            Sets.newHashSet(new String[]{"premium", "premium-jobs"}),
            var1 -> new PremiumJobsMenu(this.plugin, this.plugin.getConfig("premium-jobs-menu"), var1)
         );
      this.menus
         .put(
            Sets.newHashSet(new String[]{"leaderboard", "leaderboard-menu"}),
            var1 -> new LeaderboardMenu(this.plugin, this.plugin.getConfig("leaderboard-menu"), var1)
         );
   }

   public Map<Collection<String>, Function<Player, Menu>> getMenus() {
      return this.menus;
   }
}
