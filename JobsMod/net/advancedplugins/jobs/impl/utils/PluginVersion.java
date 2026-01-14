package net.advancedplugins.jobs.impl.utils;

public class PluginVersion {
   private int major;
   private int minor;
   private int bugfix;

   public int getMajor() {
      return this.major;
   }

   public PluginVersion setMajor(int var1) {
      this.major = var1;
      return this;
   }

   public int getMinor() {
      return this.minor;
   }

   public PluginVersion setMinor(int var1) {
      this.minor = var1;
      return this;
   }

   public int getBugfix() {
      return this.bugfix;
   }

   public PluginVersion setBugfix(int var1) {
      this.bugfix = var1;
      return this;
   }

   @Override
   public String toString() {
      return "PluginVersion{major=" + this.major + ", minor=" + this.minor + ", bugfix=" + this.bugfix + "}";
   }
}
