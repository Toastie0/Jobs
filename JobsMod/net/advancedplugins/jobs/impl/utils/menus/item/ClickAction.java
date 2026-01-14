package net.advancedplugins.jobs.impl.utils.menus.item;

import net.advancedplugins.jobs.impl.utils.menus.AdvancedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ClickAction {
   void onClick(Player var1, AdvancedMenu var2, AdvancedMenuItem var3, int var4, ClickType var5);
}
