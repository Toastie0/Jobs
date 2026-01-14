package net.advancedjobs.objects;

import java.util.List;

/**
 * Configuration for how a job appears in GUI menus
 */
public class GuiItemConfig {
    private final String material;
    private final String name;
    private final List<String> lore;
    private final boolean glow;
    
    public GuiItemConfig(String material, String name, List<String> lore, boolean glow) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.glow = glow;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public boolean isGlow() {
        return glow;
    }
}
