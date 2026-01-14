package net.advancedplugins.jobs.impl.actions.objects.root;

import java.util.function.Predicate;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ActionRoot {
   private final ActionRoot.RootType type;
   private final Object object;

   private ActionRoot(ActionRoot.RootType var1, Object var2) {
      this.type = var1;
      this.object = var2;
   }

   public ActionRoot(String var1) {
      this(var1.equalsIgnoreCase(ActionRegistry.NONE_ROOT) ? ActionRoot.RootType.NONE : ActionRoot.RootType.TEXT, var1);
   }

   public ActionRoot(ItemStack var1) {
      this(ActionRoot.RootType.ITEM_STACK, var1);
   }

   public ActionRoot(Entity var1) {
      this(ActionRoot.RootType.ENTITY, var1);
   }

   public ActionRoot(Block var1) {
      this(ActionRoot.RootType.BLOCK, var1);
   }

   public ActionRoot(Material var1) {
      this(ActionRoot.RootType.MATERIAL, var1);
   }

   public ActionRoot(Predicate<?> var1) {
      this(ActionRoot.RootType.PREDICATE, var1);
   }

   public static ActionRoot from(Object var0) {
      if (var0 instanceof String) {
         return new ActionRoot((String)var0);
      } else if (var0 instanceof ItemStack) {
         return new ActionRoot((ItemStack)var0);
      } else if (var0 instanceof Entity) {
         return new ActionRoot((Entity)var0);
      } else if (var0 instanceof Block) {
         return new ActionRoot((Block)var0);
      } else if (var0 instanceof Material) {
         return new ActionRoot((Material)var0);
      } else {
         return var0 instanceof Predicate ? new ActionRoot((Predicate<?>)var0) : new ActionRoot(var0.toString());
      }
   }

   @Override
   public String toString() {
      if (this.object instanceof String) {
         return (String)this.object;
      } else if (this.object instanceof ItemStack) {
         return ((ItemStack)this.object).getType().name();
      } else if (this.object instanceof Entity) {
         return ((Entity)this.object).getType().name();
      } else if (this.object instanceof Block) {
         return ((Block)this.object).getType().name();
      } else {
         return this.object instanceof Material ? ((Material)this.object).name() : this.object.toString();
      }
   }

   public ActionRoot.RootType getType() {
      return this.type;
   }

   public Object getObject() {
      return this.object;
   }

   static enum RootType {
      NONE,
      TEXT,
      ITEM_STACK,
      ENTITY,
      BLOCK,
      MATERIAL,
      PREDICATE;
   }
}
