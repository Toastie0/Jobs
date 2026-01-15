package net.toastie.jobs.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.toastie.jobs.controller.JobController;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles crop harvest events for Farmer job.
 * Detects when players harvest crops and awards progress.
 */
public class CropHarvestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CropHarvestHandler.class);
    
    private final JobController jobController;
    private final Map<BlockPos, Long> bonemealUsageTracker = new ConcurrentHashMap<>();
    private static final long BONEMEAL_COOLDOWN = 1000; // 1 second cooldown after bonemeal use
    
    public CropHarvestHandler(JobController jobController) {
        this.jobController = jobController;
    }
    
    /**
     * Registers the crop harvest event listeners.
     */
    public void register() {
        UseBlockCallback.EVENT.register(this::onUseBlock);
        PlayerBlockBreakEvents.AFTER.register(this::onBlockBreak);
        LOGGER.info("Registered CropHarvestHandler");
    }
    
    /**
     * Track bonemeal usage and handle right-click harvesting (berries, etc.)
     */
    private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        // Server-side only
        if (world.isClient()) {
            return ActionResult.PASS;
        }
        
        BlockPos pos = hitResult.getBlockPos();
        ItemStack heldItem = player.getStackInHand(hand);
        
        // Track bonemeal usage - mark this position to prevent progress for a short time
        if (heldItem.getItem() == Items.BONE_MEAL) {
            bonemealUsageTracker.put(pos, System.currentTimeMillis());
            return ActionResult.PASS;
        }
        
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        
        // Only handle right-click harvesting for berry bushes (they don't break on harvest)
        if (block instanceof SweetBerryBushBlock) {
            int age = state.get(SweetBerryBushBlock.AGE);
            if (age >= 3) { // Berry bush is mature at age 3
                if (!wasRecentlyBonemealed(pos)) {
                    handleCropHarvest(player, block, pos);
                }
            }
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * Called when a player breaks a block.
     * Handle actual crop harvesting (breaking mature crops).
     */
    private void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, net.minecraft.block.entity.BlockEntity blockEntity) {
        Block block = state.getBlock();
        
        // Check if this was recently bonemealed
        if (wasRecentlyBonemealed(pos)) {
            return;
        }
        
        // Check if breaking mature crops
        if (block instanceof CropBlock) {
            CropBlock crop = (CropBlock) block;
            if (crop.isMature(state)) {
                handleCropHarvest(player, crop, pos);
            }
        } else if (block instanceof CocoaBlock) {
            int age = state.get(CocoaBlock.AGE);
            if (age >= 2) { // Cocoa is mature at age 2
                handleCropHarvest(player, block, pos);
            }
        } else if (block instanceof NetherWartBlock) {
            int age = state.get(NetherWartBlock.AGE);
            if (age >= 3) { // Nether wart is mature at age 3
                handleCropHarvest(player, block, pos);
            }
        }
    }
    
    /**
     * Check if a position was recently affected by bonemeal.
     */
    private boolean wasRecentlyBonemealed(BlockPos pos) {
        Long lastBonemeal = bonemealUsageTracker.get(pos);
        if (lastBonemeal == null) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - lastBonemeal;
        if (elapsed > BONEMEAL_COOLDOWN) {
            bonemealUsageTracker.remove(pos);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clears expired bonemeal tracking entries.
     * Call this periodically to prevent memory buildup.
     */
    public void clearExpiredBonemealEntries() {
        long now = System.currentTimeMillis();
        bonemealUsageTracker.entrySet().removeIf(entry -> 
            (now - entry.getValue()) > BONEMEAL_COOLDOWN
        );
    }
    
    /**
     * Awards progress for harvesting a crop.
     */
    private void handleCropHarvest(PlayerEntity player, Block block, BlockPos pos) {
        // Get the block identifier (with mod namespace for compatibility)
        String material = Registries.BLOCK.getId(block).toString();
        // Remove namespace prefix for vanilla blocks to maintain backward compatibility
        if (material.startsWith("minecraft:")) {
            material = material.replace("minecraft:", "");
        }
        // For modded crops, keep the full identifier (e.g., "modname:custom_crop")
        
        LOGGER.debug("Player {} harvested {} at {}", player.getName().getString(), material, pos);
        jobController.awardProgress(player.getUuid(), "harvest", material);
    }
}
