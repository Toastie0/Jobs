package net.advancedjobs.handlers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.advancedjobs.controller.JobController;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles block break events for Miner and other block-breaking jobs.
 * Includes anti-exploit protection to prevent farming player-placed blocks.
 */
public class BlockBreakHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockBreakHandler.class);
    private static final long BLOCK_TRACKING_EXPIRY_MS = 600000; // 10 minutes
    
    private final JobController jobController;
    private final ConcurrentHashMap<BlockPos, PlacedBlockInfo> placedBlocks = new ConcurrentHashMap<>();
    private boolean trackPlacedBlocks = true;
    
    /**
     * Stores information about a placed block including who placed it and when.
     */
    private static class PlacedBlockInfo {
        final UUID playerUuid;
        final long timestamp;
        
        PlacedBlockInfo(UUID playerUuid) {
            this.playerUuid = playerUuid;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public BlockBreakHandler(JobController jobController) {
        this.jobController = jobController;
    }
    
    /**
     * Registers the block break event listener.
     */
    public void register() {
        PlayerBlockBreakEvents.AFTER.register(this::onBlockBreak);
        LOGGER.info("Registered BlockBreakHandler");
    }
    
    /**
     * Called after a player breaks a block.
     */
    private void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        // Server-side only
        if (world.isClient()) {
            return;
        }
        
        // Check if block was placed by a player (anti-exploit)
        if (trackPlacedBlocks && wasPlacedByPlayer(pos)) {
            placedBlocks.remove(pos);
            LOGGER.debug("Ignoring player-placed block break at {}", pos);
            return;
        }
        
        // Get the block material identifier (with mod namespace for compatibility)
        String material = Registries.BLOCK.getId(state.getBlock()).toString();
        // Remove namespace prefix for vanilla blocks to maintain backward compatibility
        if (material.startsWith("minecraft:")) {
            material = material.replace("minecraft:", "");
        }
        // For modded blocks, keep the full identifier (e.g., "modname:ore_block")
        
        LOGGER.debug("Player {} broke block: {}", player.getName().getString(), material);
        
        // Award progress for this block break
        jobController.awardProgress(player.getUuid(), "break", material);
    }
    
    /**
     * Tracks a block placement to prevent exploit.
     */
    public void trackBlockPlacement(BlockPos pos, UUID playerUuid) {
        if (trackPlacedBlocks) {
            placedBlocks.put(pos, new PlacedBlockInfo(playerUuid));
        }
    }
    
    /**
     * Checks if a block was placed by a player.
     */
    private boolean wasPlacedByPlayer(BlockPos pos) {
        return placedBlocks.containsKey(pos);
    }
    
    /**
     * Clears old entries from the placed blocks cache.
     * Removes entries older than BLOCK_TRACKING_EXPIRY_MS or keeps only the newest 10000 entries.
     */
    public void clearOldEntries() {
        long now = System.currentTimeMillis();
        int initialSize = placedBlocks.size();
        
        // Remove entries older than expiry time
        placedBlocks.entrySet().removeIf(entry -> 
            (now - entry.getValue().timestamp) > BLOCK_TRACKING_EXPIRY_MS
        );
        
        // If still over 10000, remove oldest entries
        if (placedBlocks.size() > 10000) {
            placedBlocks.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e1.getValue().timestamp, e2.getValue().timestamp))
                .limit(placedBlocks.size() - 10000)
                .map(Map.Entry::getKey)
                .forEach(placedBlocks::remove);
        }
        
        int removed = initialSize - placedBlocks.size();
        if (removed > 0) {
            LOGGER.debug("Cleared {} old block tracking entries ({} remaining)", removed, placedBlocks.size());
        }
    }
    
    /**
     * Sets whether to track placed blocks.
     */
    public void setTrackPlacedBlocks(boolean track) {
        this.trackPlacedBlocks = track;
    }
}
