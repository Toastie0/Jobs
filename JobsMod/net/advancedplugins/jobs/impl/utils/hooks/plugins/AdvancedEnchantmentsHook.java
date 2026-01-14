package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import java.util.HashMap;
import java.util.List;
import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.enchanthandler.enchantments.AdvancedEnchantment;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.inventory.ItemStack;

public class AdvancedEnchantmentsHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return "AdvancedEnchantments";
   }

   public HashMap<String, Integer> getEnchantmentsOnItem(ItemStack var1) {
      return AEAPI.getEnchantmentsOnItem(var1);
   }

   public List<String> getAllEnchantments() {
      return AEAPI.getAllEnchantments();
   }

   public AdvancedEnchantment getInstance(String var1) {
      return AEAPI.getEnchantmentInstance(var1);
   }
}
