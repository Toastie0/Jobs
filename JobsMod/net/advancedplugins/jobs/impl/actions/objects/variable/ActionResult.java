package net.advancedplugins.jobs.impl.actions.objects.variable;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ActionResult {
   ActionResult none();

   ActionResult root(String var1);

   ActionResult root(Material var1);

   ActionResult root(ItemStack var1);

   ActionResult root(Entity var1);

   ActionResult root(Block var1);

   ActionResult root(Predicate<Object> var1);

   ActionResult subRoot(String var1, String var2);

   ActionResult subRoot(String var1, Material var2);

   ActionResult subRoot(String var1, ItemStack var2);

   ActionResult subRoot(String var1, Entity var2);

   ActionResult subRoot(String var1, Block var2);

   ActionResult subRoot(String var1, Predicate<Object> var2);

   ActionResult subRoot(ItemStack var1);

   double isEligible(Player var1, Variable var2);

   @Override
   String toString();

   String getEffectiveRoot();
}
