package net.advancedplugins.simplespigot.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.actions.utils.MultiMaterial;
import net.advancedplugins.jobs.impl.utils.SkullCreator;
import net.advancedplugins.jobs.impl.utils.items.ItemFlagFix;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SpigotItem {
   public static SpigotItem.Builder builder() {
      return new SpigotItem.Builder();
   }

   public static ItemStack toItemOrDefault(Config var0, String var1, Replace var2, ItemStack var3) {
      return toItemOrDefault(var0, var1, var2, var3, false);
   }

   public static ItemStack toItemOrDefault(Config var0, String var1, Replace var2, ItemStack var3, boolean var4) {
      if (!var0.has(var1)) {
         return var3;
      } else {
         UnaryOperator var5 = var1x -> String.format("%s.%s", var1, var1x);
         if (var2 == null) {
            new Replacer();
         } else {
            Replacer var10000 = var2.apply(new Replacer());
         }

         String var7 = Text.parsePapi(Text.modify(var0.string(var5.apply("material")), var2), null);
         if (var7 == null) {
            return null;
         } else {
            String[] var8 = var7.split(":");
            boolean var9 = var8[0].equalsIgnoreCase("head") && var8.length == 2;
            ItemStack var10 = var9 ? null : MultiMaterial.parseItem(var7);
            if (var10 == null && !var9) {
               return null;
            } else {
               SpigotItem.Builder var11 = builder().itemStack(var10);
               var11.papi(false);
               validatePath(var0, var5.apply("name"), var4x -> var11.name(Text.modify(var0.string(var5.apply("name")), var2, false)));
               validatePath(var0, var5.apply("lore"), var3x -> var11.lore(Text.modify(var0.list(var3x), var2, false)));
               if (!var4) {
                  validatePath(var0, var5.apply("amount"), var2x -> var11.amount(var0.integer(var2x)));
               }

               validatePath(var0, var5.apply("rgb"), var2x -> var11.color(var0.string(var2x)));
               validatePath(var0, var5.apply("customModelData"), var2x -> var11.customModelData(var0.integer(var2x)));
               if (var0.bool(var5.apply("glow"))) {
                  var11.glow();
               }

               var0.stringList(var5.apply("item-flags")).forEach(var11::flag);
               var0.stringList(var5.apply("enchants")).forEach(var11::enchant);
               if (var9) {
                  var11.head(var8[1]);
               }

               return var11.build();
            }
         }
      }
   }

   public static ItemStack toItem(Config var0, String var1, Replace var2) {
      return toItemOrDefault(var0, var1, var2, null);
   }

   public static ItemStack toItem(Config var0, String var1) {
      return toItem(var0, var1, null);
   }

   private static void validatePath(Config var0, String var1, Consumer<String> var2) {
      if (var0.get(var1) != null) {
         var2.accept(var1);
      }
   }

   public static class Builder {
      private ItemStack itemStack = new ItemStack(Material.DIRT);
      private Material material;
      private byte data;
      private int amount = 1;
      private String name;
      private List<String> lore = Lists.newArrayList();
      private Set<ItemFlag> itemFlags = Sets.newHashSet();
      private Map<Enchantment, Integer> enchants = Maps.newHashMap();
      private boolean doesGlow = false;
      private Color color;
      private int customModelData = 0;
      private String base64Head = null;
      private String ownerHead = null;
      private boolean isHead = false;
      private boolean addPapi = true;

      public SpigotItem.Builder itemStack(ItemStack var1) {
         this.itemStack = var1;
         return this;
      }

      public SpigotItem.Builder item(Material var1, int var2) {
         this.material = var1;
         this.data = (byte)var2;
         return this;
      }

      public SpigotItem.Builder item(Material var1) {
         this.material = var1;
         return this;
      }

      public SpigotItem.Builder customModelData(int var1) {
         this.customModelData = var1;
         return this;
      }

      public SpigotItem.Builder head(String var1) {
         String var2 = "PLAYER_HEAD";
         this.itemStack = new ItemStack(Material.valueOf(var2), 1, (short)3);
         this.data = 3;
         this.isHead = true;
         this.base64Head = var1.length() > 16 ? var1 : null;
         this.ownerHead = var1.length() < 17 ? var1 : null;
         return this;
      }

      public SpigotItem.Builder amount(int var1) {
         this.amount = var1;
         return this;
      }

      public SpigotItem.Builder name(String var1) {
         this.name = Text.modify(var1, null, this.addPapi);
         return this;
      }

      public SpigotItem.Builder lore(List<String> var1) {
         this.lore = Text.modify(var1, null, this.addPapi);
         return this;
      }

      public SpigotItem.Builder lore(String... var1) {
         this.lore = Text.modify(Arrays.asList(var1), null, this.addPapi);
         return this;
      }

      public SpigotItem.Builder flag(ItemFlag var1) {
         this.itemFlags.add(var1);
         return this;
      }

      public SpigotItem.Builder flag(String var1) {
         try {
            ItemFlag var2 = ItemFlag.valueOf(var1.toUpperCase());
            this.itemFlags.add(var2);
         } catch (Exception var3) {
         }

         return this;
      }

      public SpigotItem.Builder enchant(Enchantment var1, int var2) {
         this.enchants.put(var1, var2);
         return this;
      }

      public SpigotItem.Builder enchant(String var1) {
         String[] var2 = var1.split(":");
         if (var2.length == 2) {
            Enchantment var3 = Enchantment.getByName(var2[0]);
            int var4 = Integer.parseInt(var2[1]);
            if (var3 == null) {
               return this;
            }

            this.enchants.put(var3, var4);
         }

         return this;
      }

      public SpigotItem.Builder glow() {
         this.doesGlow = true;
         return this;
      }

      public SpigotItem.Builder papi(boolean var1) {
         this.addPapi = var1;
         return this;
      }

      public SpigotItem.Builder color(String var1) {
         if (!var1.contains(",")) {
            return this;
         } else {
            String[] var2 = var1.replace(" ", "").split(",");
            if (var2.length != 3) {
               return this;
            } else {
               this.color = Color.fromRGB(Integer.parseInt(var2[0]), Integer.parseInt(var2[1]), Integer.parseInt(var2[2]));
               return this;
            }
         }
      }

      public ItemStack build() {
         if (this.material != null) {
            this.itemStack.setType(this.material);
            this.itemStack.setAmount(this.amount);
            this.itemStack.setDurability(this.data);
         }

         if (this.amount > 0) {
            this.itemStack.setAmount(this.amount);
         }

         if (this.itemStack.getItemMeta() == null) {
            return this.itemStack;
         } else {
            ItemMeta var1 = this.itemStack.getItemMeta();
            this.itemStack.setItemMeta(this.createMeta(var1));
            if (this.isHead) {
               SkullMeta var2 = (SkullMeta)this.itemStack.getItemMeta();
               if (this.base64Head != null) {
                  this.itemStack = SkullCreator.itemWithBase64(this.itemStack, this.base64Head);
               } else if (!this.ownerHead.isEmpty() && !this.ownerHead.equalsIgnoreCase("-")) {
                  var2.setOwner(this.ownerHead);
                  this.itemStack.setItemMeta(var2);
               }
            }

            return this.itemStack;
         }
      }

      public ItemMeta createMeta(ItemMeta var1) {
         if (this.name != null) {
            var1.setDisplayName(this.name);
         }

         if (this.customModelData != 0) {
            var1.setCustomModelData(this.customModelData);
         }

         if (!this.lore.isEmpty() && (!this.lore.get(0).isEmpty() || this.lore.size() >= 2)) {
            var1.setLore(this.lore);
         }

         if (!this.itemFlags.isEmpty()) {
            ItemFlagFix.fix(var1);

            for (ItemFlag var3 : this.itemFlags) {
               var1.addItemFlags(new ItemFlag[]{var3});
            }
         }

         if (!this.enchants.isEmpty()) {
            for (Entry var5 : this.enchants.entrySet()) {
               var1.addEnchant((Enchantment)var5.getKey(), (Integer)var5.getValue(), true);
            }
         }

         if (this.doesGlow) {
            var1.addEnchant(Enchantment.DURABILITY, 0, true);
            ItemFlagFix.fix(var1);
            var1.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         if (this.color != null && var1 instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)var1).setColor(this.color);
         }

         return var1;
      }
   }
}
