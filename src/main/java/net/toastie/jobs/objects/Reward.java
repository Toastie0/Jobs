package net.toastie.jobs.objects;

import java.util.List;
import java.util.Map;

/**
 * Represents a reward that can be given to players
 */
public class Reward {
    private final String id;
    private final String type; // "command" or "item"
    private final String name;
    private final Map<String, String> variables;
    private final List<String> commands; // For command type
    private final Map<String, ItemReward> items; // For item type
    
    public Reward(String id, String type, String name, Map<String, String> variables,
                  List<String> commands, Map<String, ItemReward> items) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.variables = variables;
        this.commands = commands;
        this.items = items;
    }
    
    public String getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public Map<String, String> getVariables() { return variables; }
    public List<String> getCommands() { return commands; }
    public Map<String, ItemReward> getItems() { return items; }
    
    public static class ItemReward {
        private final String material;
        private final String amount;
        private final String name;
        private final List<String> lore;
        private final boolean glow;
        
        public ItemReward(String material, String amount, String name, List<String> lore, boolean glow) {
            this.material = material;
            this.amount = amount;
            this.name = name;
            this.lore = lore;
            this.glow = glow;
        }
        
        public String getMaterial() { return material; }
        public String getAmount() { return amount; }
        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public boolean isGlow() { return glow; }
    }
}
