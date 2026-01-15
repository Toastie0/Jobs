package net.toastie.jobs.util;

import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Scans the block registry for ores, crops, and placeable blocks.
 * Generates default progress values for auto-detection.
 */
public class BlockRegistryScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockRegistryScanner.class);
    
    /**
     * Scans for all ore blocks in the registry.
     */
    public static Map<String, Double> scanOres(double defaultProgress) {
        Map<String, Double> ores = new HashMap<>();
        
        for (Identifier id : Registries.BLOCK.getIds()) {
            String blockId = id.toString();
            Block block = Registries.BLOCK.get(id);
            
            // Detect ores by name pattern
            if (isOreBlock(blockId, block)) {
                String material = blockId.startsWith("minecraft:") 
                    ? blockId.replace("minecraft:", "") 
                    : blockId;
                ores.put(material, defaultProgress);
            }
        }
        
        LOGGER.info("Auto-detected {} ore types", ores.size());
        return ores;
    }
    
    /**
     * Scans for all crop blocks in the registry.
     */
    public static Map<String, Double> scanCrops(double defaultProgress) {
        Map<String, Double> crops = new HashMap<>();
        
        for (Identifier id : Registries.BLOCK.getIds()) {
            String blockId = id.toString();
            Block block = Registries.BLOCK.get(id);
            
            // Detect crops by block type
            if (isCropBlock(block)) {
                String material = blockId.startsWith("minecraft:") 
                    ? blockId.replace("minecraft:", "") 
                    : blockId;
                crops.put(material, defaultProgress);
            }
        }
        
        LOGGER.info("Auto-detected {} crop types", crops.size());
        return crops;
    }
    
    /**
     * Scans for all placeable blocks in the registry.
     * Filters out non-building blocks like air, liquids, technical blocks.
     */
    public static Map<String, Double> scanPlaceableBlocks(double defaultProgress) {
        Map<String, Double> blocks = new HashMap<>();
        
        for (Identifier id : Registries.BLOCK.getIds()) {
            String blockId = id.toString();
            Block block = Registries.BLOCK.get(id);
            
            // Only include actual building blocks
            if (isPlaceableBlock(block) && !isCropBlock(block) && !isOreBlock(blockId, block)) {
                String material = blockId.startsWith("minecraft:") 
                    ? blockId.replace("minecraft:", "") 
                    : blockId;
                blocks.put(material, defaultProgress);
            }
        }
        
        LOGGER.info("Auto-detected {} placeable block types", blocks.size());
        return blocks;
    }
    
    /**
     * Checks if a block is an ore based on name pattern.
     */
    private static boolean isOreBlock(String blockId, Block block) {
        String lowerCaseId = blockId.toLowerCase();
        return lowerCaseId.contains("_ore") || lowerCaseId.contains("ancient_debris");
    }
    
    /**
     * Checks if a block is a crop.
     */
    private static boolean isCropBlock(Block block) {
        return block instanceof CropBlock 
            || block instanceof NetherWartBlock 
            || block instanceof SweetBerryBushBlock
            || block instanceof CocoaBlock;
    }
    
    /**
     * Checks if a block is placeable and suitable for building.
     */
    private static boolean isPlaceableBlock(Block block) {
        // Exclude technical/non-building blocks
        if (block instanceof AirBlock 
            || block instanceof FluidBlock
            || block instanceof FireBlock
            || block instanceof AbstractSignBlock
            || block instanceof BedBlock
            || block instanceof DoorBlock
            || block instanceof ButtonBlock
            || block instanceof LeverBlock
            || block instanceof RedstoneTorchBlock
            || block instanceof RedstoneWireBlock
            || block instanceof PistonBlock
            || block instanceof PistonHeadBlock) {
            return false;
        }
        
        // Include solid building blocks
        return true;
    }
}
