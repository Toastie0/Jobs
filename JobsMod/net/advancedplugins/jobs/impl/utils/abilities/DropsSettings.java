package net.advancedplugins.jobs.impl.utils.abilities;

import org.bukkit.inventory.ItemStack;

public class DropsSettings {
   private boolean smelt = false;
   private boolean addToInventory = false;
   private boolean silkTouch = false;
   private boolean defaultDrops = true;
   private int dropsMultiplier = 1;
   private boolean breakBlocks = false;
   private boolean dropExp = true;
   private boolean durabilityDamage = true;
   private ItemStack tool = null;
   private boolean treeFellerEvent = false;
   private boolean terraformEvent = false;
   private int dropExpAmount = 0;

   public boolean isSmelt() {
      return this.smelt;
   }

   public boolean isAddToInventory() {
      return this.addToInventory;
   }

   public boolean isSilkTouch() {
      return this.silkTouch;
   }

   public boolean isDefaultDrops() {
      return this.defaultDrops;
   }

   public int getDropsMultiplier() {
      return this.dropsMultiplier;
   }

   public boolean isBreakBlocks() {
      return this.breakBlocks;
   }

   public boolean isDropExp() {
      return this.dropExp;
   }

   public boolean isDurabilityDamage() {
      return this.durabilityDamage;
   }

   public ItemStack getTool() {
      return this.tool;
   }

   public boolean isTreeFellerEvent() {
      return this.treeFellerEvent;
   }

   public boolean isTerraformEvent() {
      return this.terraformEvent;
   }

   public int getDropExpAmount() {
      return this.dropExpAmount;
   }

   public void setSmelt(boolean var1) {
      this.smelt = var1;
   }

   public void setAddToInventory(boolean var1) {
      this.addToInventory = var1;
   }

   public void setSilkTouch(boolean var1) {
      this.silkTouch = var1;
   }

   public void setDefaultDrops(boolean var1) {
      this.defaultDrops = var1;
   }

   public void setDropsMultiplier(int var1) {
      this.dropsMultiplier = var1;
   }

   public void setBreakBlocks(boolean var1) {
      this.breakBlocks = var1;
   }

   public void setDropExp(boolean var1) {
      this.dropExp = var1;
   }

   public void setDurabilityDamage(boolean var1) {
      this.durabilityDamage = var1;
   }

   public void setTool(ItemStack var1) {
      this.tool = var1;
   }

   public void setTreeFellerEvent(boolean var1) {
      this.treeFellerEvent = var1;
   }

   public void setTerraformEvent(boolean var1) {
      this.terraformEvent = var1;
   }

   public void setDropExpAmount(int var1) {
      this.dropExpAmount = var1;
   }
}
