package net.advancedplugins.jobs.impl.utils.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class KeyInfo {
   public Material displayMaterial;
   public String description;
   public List<String> suggestions;
   public String wikiLink = null;
   public HashSet<String> parent = new HashSet<>();
   public boolean menu = false;
   public KeyType type;
   public String name = null;
   public FileConfiguration configuration;
   public boolean addKeyToInput = false;
   public String path;

   public KeyInfo(String var1, Material var2, String var3, List<String> var4) {
      this.path = var1;
      this.displayMaterial = var2;
      this.description = var3;
      this.suggestions = var4;
   }

   public KeyInfo(String var1, Material var2, String var3, List<String> var4, String var5) {
      this.path = var1;
      this.displayMaterial = var2;
      this.description = var3;
      this.suggestions = var4;
      this.wikiLink = var5;
   }

   public KeyInfo addParentPath(String var1) {
      this.parent.add(var1);
      return this;
   }

   public KeyInfo setType(KeyType var1) {
      this.type = var1;
      return this;
   }

   public KeyInfo setName(String var1) {
      this.name = var1;
      return this;
   }

   public KeyInfo addSuggestions(List<String> var1) {
      if (this.suggestions == null) {
         this.suggestions = new ArrayList<>();
      }

      this.suggestions.addAll(var1);
      return this;
   }

   public KeyInfo asDefault() {
      this.parent.clear();
      return this;
   }

   public KeyInfo asMenu() {
      this.menu = true;
      return this;
   }

   public KeyInfo addKeyToInput() {
      this.addKeyToInput = true;
      return this;
   }
}
