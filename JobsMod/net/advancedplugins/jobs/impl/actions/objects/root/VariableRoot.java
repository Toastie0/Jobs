package net.advancedplugins.jobs.impl.actions.objects.root;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class VariableRoot {
   private final String rootString;
   private final List<BlockFace> faces;
   private final boolean itemsAdderEnabled;
   private final boolean mmoItemsEnabled;
   private VariableRoot.RootType type;
   private String textRoot;
   private Material materialRoot;
   private ItemStack itemStackRoot;
   private JsonObject properties;
   private double progressMultiplier;

   public VariableRoot(String var1) {
      this.rootString = var1;
      this.faces = new ArrayList<>();
      this.itemsAdderEnabled = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
      this.mmoItemsEnabled = Bukkit.getPluginManager().isPluginEnabled("MMOItems");
      this.progressMultiplier = 1.0;
      this.createRoot();
   }

   private void createRoot() {
      String var1 = this.rootString;
      this.properties = null;
      if (var1.contains("{") && var1.contains("}")) {
         int var2 = var1.indexOf("{");
         this.properties = (JsonObject)new Gson().fromJson(var1.substring(var2, this.findJsonEnd(var1, var2 + 1) + 1), JsonObject.class);
         var1 = var1.substring(0, var2);
      }

      if (this.properties != null && this.properties.has("progress")) {
         this.progressMultiplier = this.properties.get("progress").getAsDouble();
      }

      if (this.isMaterial(var1)) {
         this.type = VariableRoot.RootType.MATERIAL;
         this.materialRoot = Material.valueOf(var1.toUpperCase());
         if (this.properties != null) {
            if (this.materialRoot.isItem() && this.properties.has("nbt")) {
               this.type = VariableRoot.RootType.ITEM_STACK;
               this.itemStackRoot = new ItemStack(this.materialRoot);
               this.itemStackRoot = Bukkit.getUnsafe().modifyItemStack(this.itemStackRoot, this.properties.get("nbt").getAsString());
               return;
            }

            if (this.properties.has("faces")) {
               this.properties.getAsJsonArray("faces").forEach(var1x -> this.faces.add(BlockFace.valueOf(var1x.getAsString().toUpperCase())));
               return;
            }
         }
      } else if (this.itemsAdderEnabled && var1.startsWith("itemsadder:")) {
         this.type = VariableRoot.RootType.ITEMS_ADDER;
         this.textRoot = var1.replace("itemsadder:", "");
      } else if (this.mmoItemsEnabled && var1.startsWith("mmoitems:")) {
         this.type = VariableRoot.RootType.MMO_ITEMS;
         this.textRoot = var1.replace("mmoitems:", "");
      } else if (var1.startsWith("__")) {
         this.type = VariableRoot.RootType.CONTAINS;
         this.textRoot = var1.substring(2);
      } else if (var1.equalsIgnoreCase("none")) {
         this.type = VariableRoot.RootType.NONE;
      } else {
         this.type = VariableRoot.RootType.TEXT;
         this.textRoot = var1;
      }
   }

   public double isValid(ActionRoot var1) {
      if (this.type != VariableRoot.RootType.NONE && var1.getType() != ActionRoot.RootType.NONE) {
         switch (var1.getType()) {
            case TEXT:
               String var2 = var1.getObject().toString();
               switch (this.type) {
                  case TEXT:
                  case ITEMS_ADDER:
                  case MMO_ITEMS:
                     return this.getMultiplier(var2.equalsIgnoreCase(this.textRoot));
                  case CONTAINS:
                     return this.getMultiplier(var2.contains(this.textRoot));
                  case MATERIAL:
                     return this.getMultiplier(var2.equalsIgnoreCase(this.materialRoot.name()));
                  case ITEM_STACK:
                     return this.getMultiplier(var2.equalsIgnoreCase(this.itemStackRoot.getType().name()));
                  default:
                     return 0.0;
               }
            case MATERIAL:
               Material var3 = (Material)var1.getObject();
               switch (this.type) {
                  case CONTAINS:
                     return this.getMultiplier(var3.name().contains(this.textRoot.toUpperCase()));
                  case MATERIAL:
                     return this.getMultiplier(var3 == this.materialRoot);
                  case ITEM_STACK:
                     return this.getMultiplier(var3 == this.itemStackRoot.getType());
                  case ITEMS_ADDER:
                     Material var10 = null;
                     if (CustomStack.isInRegistry(this.textRoot)) {
                        var10 = CustomStack.getInstance(this.textRoot).getItemStack().getType();
                     }

                     if (CustomBlock.isInRegistry(this.textRoot)) {
                        var10 = CustomBlock.getInstance(this.textRoot).getBlock().getType();
                     }

                     return this.getMultiplier(var3 == var10);
                  default:
                     return 0.0;
               }
            case ITEM_STACK:
               ItemStack var4 = (ItemStack)var1.getObject();
               switch (this.type) {
                  case CONTAINS:
                     return this.getMultiplier(var4.getType().name().contains(this.textRoot.toUpperCase()));
                  case MATERIAL:
                     return this.getMultiplier(var4.getType() == this.materialRoot);
                  case ITEM_STACK:
                     return this.getMultiplier(var4.isSimilar(this.itemStackRoot));
                  case ITEMS_ADDER:
                     if (!CustomStack.isInRegistry(this.textRoot)) {
                        return 0.0;
                     }

                     ItemStack var11 = CustomStack.getInstance(this.textRoot).getItemStack().clone();
                     if (this.properties != null && this.properties.has("nbt")) {
                        var11 = Bukkit.getUnsafe().modifyItemStack(var11, this.properties.get("nbt").getAsString());
                     }

                     return this.getMultiplier(var4.isSimilar(var11));
                  case MMO_ITEMS:
                     NBTItem var12 = NBTItem.get(var4);
                     return this.getMultiplier(var12.hasType() && var12.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(this.textRoot));
                  default:
                     return 0.0;
               }
            case BLOCK:
               Block var5 = (Block)var1.getObject();
               BlockData var6 = var5.getBlockData();
               if (!this.faces.isEmpty() && var6 instanceof MultipleFacing var13) {
                  if (this.faces.size() != var13.getFaces().size()) {
                     return 0.0;
                  }

                  for (BlockFace var9 : this.faces) {
                     if (!var13.hasFace(var9)) {
                        return 0.0;
                     }
                  }
               }

               if (this.properties != null
                  && this.properties.has("age")
                  && var6 instanceof Ageable var14
                  && var14.getAge() != this.properties.get("age").getAsInt()) {
                  return 0.0;
               }

               switch (this.type) {
                  case CONTAINS:
                     return this.getMultiplier(var5.getType().name().contains(this.textRoot.toUpperCase()));
                  case MATERIAL:
                     return this.getMultiplier(var5.getType() == this.materialRoot);
                  case ITEM_STACK:
                  default:
                     return 0.0;
                  case ITEMS_ADDER:
                     CustomBlock var15 = CustomBlock.byAlreadyPlaced(var5);
                     return this.getMultiplier(var15 != null && var15.getNamespacedID().equalsIgnoreCase(this.textRoot));
               }
            case ENTITY:
               Entity var7 = (Entity)var1.getObject();
               switch (this.type) {
                  case TEXT:
                     return this.getMultiplier(var7.getType().name().equalsIgnoreCase(this.textRoot.toUpperCase()));
                  case CONTAINS:
                     return this.getMultiplier(var7.getType().name().contains(this.textRoot.toUpperCase()));
                  case MATERIAL:
                     return this.getMultiplier(var7.getType().name().equalsIgnoreCase(this.materialRoot.name()));
                  case ITEM_STACK:
                  default:
                     return 0.0;
                  case ITEMS_ADDER:
                     CustomEntity var16 = CustomEntity.byAlreadySpawned(var7);
                     return this.getMultiplier(var16 != null && var16.getNamespacedID().equalsIgnoreCase(this.textRoot));
               }
            case PREDICATE:
               Predicate var8 = (Predicate)var1.getObject();
               switch (this.type) {
                  case TEXT:
                     return this.getMultiplier(var8.test(this.textRoot));
                  case CONTAINS:
                     return this.getMultiplier(var8.test("__" + this.textRoot));
                  case MATERIAL:
                     return this.getMultiplier(var8.test(this.materialRoot));
                  case ITEM_STACK:
                     return this.getMultiplier(var8.test(this.itemStackRoot));
                  case ITEMS_ADDER:
                     return this.getMultiplier(var8.test("itemsadder:" + this.textRoot));
                  case MMO_ITEMS:
                     return this.getMultiplier(var8.test("mmoitems:" + this.textRoot));
               }
         }

         return 0.0;
      } else {
         return this.getMultiplier(true);
      }
   }

   private double getMultiplier(boolean var1) {
      return var1 ? this.progressMultiplier : 0.0;
   }

   private int findJsonEnd(String var1, int var2) {
      if (var2 > var1.length()) {
         return -1;
      } else {
         int var3 = var1.indexOf("}", var2);
         int var4 = var1.indexOf("{", var2);
         if (var4 > -1 && var4 < var3) {
            int var5 = this.findJsonEnd(var1, var4 + 1);
            return this.findJsonEnd(var1, var5 + 2);
         } else {
            return var3;
         }
      }
   }

   private boolean isMaterial(String var1) {
      return Arrays.stream(Material.values()).map(Enum::name).anyMatch(var1x -> var1x.equalsIgnoreCase(var1));
   }

   public String getRootString() {
      return this.rootString;
   }

   public List<BlockFace> getFaces() {
      return this.faces;
   }

   public boolean isItemsAdderEnabled() {
      return this.itemsAdderEnabled;
   }

   public boolean isMmoItemsEnabled() {
      return this.mmoItemsEnabled;
   }

   public VariableRoot.RootType getType() {
      return this.type;
   }

   public String getTextRoot() {
      return this.textRoot;
   }

   public Material getMaterialRoot() {
      return this.materialRoot;
   }

   public ItemStack getItemStackRoot() {
      return this.itemStackRoot;
   }

   public JsonObject getProperties() {
      return this.properties;
   }

   public double getProgressMultiplier() {
      return this.progressMultiplier;
   }

   static enum RootType {
      NONE,
      TEXT,
      CONTAINS,
      MATERIAL,
      ITEM_STACK,
      ITEMS_ADDER,
      MMO_ITEMS;
   }
}
