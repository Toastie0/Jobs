package net.advancedplugins.simplespigot.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.advancedplugins.jobs.impl.utils.tuple.ImmutablePair;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import org.bukkit.entity.Player;

public abstract class PageableMenu<T> extends Menu {
   protected int page = 1;
   private List<T> elements;
   private List<Integer> elementSlots;
   private final Map<Integer, Set<Integer>> cachedPageIndexes = Maps.newHashMap();

   public PageableMenu(Player var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public abstract MenuItem pageableItem(T var1);

   public abstract ImmutablePair<Collection<T>, Collection<Integer>> elementalValues();

   @Override
   public void close() {
      this.player.closeInventory();
      this.cachedPageIndexes.clear();
   }

   public int getPage() {
      return this.page;
   }

   public void drawPageableItems() {
      this.drawPageableItems(() -> {});
   }

   public void drawPageableItems(Runnable var1) {
      if (this.elements == null || this.elementSlots == null) {
         this.elements = Lists.newArrayList(this.elementalValues().getKey());
         this.elementSlots = Lists.newArrayList(this.elementalValues().getValue());
      }

      this.cachedPageIndexes.computeIfAbsent(this.page, var1x -> {
         int var2 = this.elementSlots.size();
         LinkedHashSet var3x = Sets.newLinkedHashSetWithExpectedSize(var2);

         for (int var4x = 0; var4x < var2; var4x++) {
            int var5x = (this.page - 1) * var2 + var4x;
            if (var5x < this.elements.size()) {
               var3x.add(var5x);
            }
         }

         return var3x;
      });

      for (int var3 : this.elementSlots) {
         this.flush(var3);
      }

      var1.run();
      int var5 = 0;

      for (int var4 : this.cachedPageIndexes.get(this.page)) {
         this.item(MenuItem.builderOf(this.pageableItem(this.elements.get(var4))).rawSlot(this.elementSlots.get(var5)).build());
         var5++;
      }
   }

   public void nextPage(Runnable var1) {
      if (this.elements.size() >= this.elementSlots.size() * this.page + 1) {
         this.page++;
         var1.run();
      }
   }

   public void previousPage(Runnable var1) {
      this.page--;
      var1.run();
   }

   public void refreshPageableItems() {
      this.elements = Lists.newArrayList(this.elementalValues().getKey());
      this.cachedPageIndexes.clear();
      this.redraw();
   }
}
