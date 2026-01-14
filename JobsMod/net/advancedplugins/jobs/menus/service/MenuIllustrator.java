package net.advancedplugins.jobs.menus.service;

import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.actions.Action;
import net.advancedplugins.jobs.actions.CommandAction;
import net.advancedplugins.jobs.actions.ConsoleCommandAction;
import net.advancedplugins.jobs.actions.DynamicAction;
import net.advancedplugins.jobs.actions.MenuAction;
import net.advancedplugins.jobs.actions.MessageAction;
import net.advancedplugins.jobs.actions.SoundAction;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.menus.MenuFactory;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.Menu;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import net.advancedplugins.simplespigot.menu.service.MenuService;
import org.bukkit.entity.Player;

public class MenuIllustrator {
   public void draw(
      Menu var1, Config var2, MenuFactory var3, Player var4, Cache<String, Map<Integer, List<Action>>> var5, Map<String, Runnable> var6, Replace var7
   ) {
      Function var8 = var3x -> {
         String var4x = var1.getClass().getSimpleName();
         Map var5x = (Map)var5.getIfPresent(var4x);
         if (var5x != null && var5x.containsKey(var3x)) {
            return (List)var5x.get(var3x);
         } else {
            ArrayList var6x = Lists.newArrayList();
            if (var2.has(String.format("menu.%s.actions", var3x))) {
               for (String var8x : var2.stringList(String.format("menu.%s.actions", var3x))) {
                  var6x.add(Action.parse(var8x));
               }
            } else {
               for (String var13 : var2.getConfiguration().getConfigurationSection("menu").getKeys(false)) {
                  if (var2.has("menu." + var13 + ".slots") && MenuService.parseSlotsForActions(var1, var2.string("menu." + var13 + ".slots")).contains(var3x)) {
                     for (String var10x : var2.stringList(String.format("menu.%s.actions", var13))) {
                        var6x.add(Action.parse(var10x));
                     }
                  }
               }
            }

            try {
               ((Map)var5.get(var4x, Maps::newHashMap)).put(var3x, var6x);
            } catch (Exception var11) {
               var11.printStackTrace();
            }

            return var6x;
         }
      };
      if (var2.has("menu")) {
         for (String var10 : var2.keys("menu", false)) {
            for (int var12 : MenuService.parseSlots(var1, var2, "menu.", var10)) {
               if (var12 > var1.getRows() * 9 - 1) {
                  if (var1.getMenuState().isRaw()) {
                     Core.getInstance()
                        .getLogger()
                        .severe(
                           String.format(
                              "The specified slot %d in the menu %s is greater\n then the amount of slots in the menu (%d). Skipping the slot...",
                              var12,
                              var1.getTitle(),
                              var1.getRows() * 9 - 1
                           )
                        );
                  }
               } else {
                  MenuItem.builder().rawSlot(var12).item(var2, String.format("menu.%s.item", var10), var7).onClick((var6x, var7x) -> {
                     Replacer var8x = new Replacer().set("player", var4.getName());

                     for (Action var10x : (List)var8.apply(var12)) {
                        if (var10x instanceof MenuAction) {
                           ((MenuAction)var10x).accept(var3, var1, var4);
                        } else if (var10x instanceof MessageAction) {
                           ((MessageAction)var10x).accept(var4, null);
                        } else if (var10x instanceof SoundAction) {
                           ((SoundAction)var10x).accept(var4);
                        } else if (var10x instanceof CommandAction) {
                           ((CommandAction)var10x).accept(var4, var8x);
                        } else if (var10x instanceof ConsoleCommandAction) {
                           ((ConsoleCommandAction)var10x).accept(var8x);
                        } else if (var10x instanceof DynamicAction) {
                           for (Entry var12x : var6.entrySet()) {
                              ((DynamicAction)var10x).accept((String)var12x.getKey(), (Runnable)var12x.getValue());
                           }
                        }
                     }
                  }).buildTo(var1);
               }
            }
         }
      }
   }
}
