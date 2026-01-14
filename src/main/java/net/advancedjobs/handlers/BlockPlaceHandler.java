package net.advancedjobs.handlers;

import net.advancedjobs.controller.JobController;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles block placement events for Builder job.
 * Awards progress when players place blocks.
 */
public class BlockPlaceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockPlaceHandler.class);
    
    private final JobController jobController;
    private final BlockBreakHandler blockBreakHandler;
    
    public BlockPlaceHandler(JobController jobController, BlockBreakHandler blockBreakHandler) {
        this.jobController = jobController;
        this.blockBreakHandler = blockBreakHandler;
    }
    
    /**
     * Registers the block place event listener.
     */
    public void register() {
        UseBlockCallback.EVENT.register(this::onUseBlock);
        LOGGER.info("Registered BlockPlaceHandler");
    }
    
    /**
     * Called when a player uses (right-clicks) with an item.
     */
    private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        // Server-side only
        if (world.isClient()) {
            return ActionResult.PASS;
        }
        
        ItemStack stack = player.getStackInHand(hand);
        
        // Check if placing a block
        if (stack.getItem() instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) stack.getItem();
            BlockPos targetPos = hitResult.getBlockPos().offset(hitResult.getSide());
            
            // Award progress after successful placement
            world.getServer().execute(() -> {
                BlockState placedState = world.getBlockState(targetPos);
                if (!placedState.isAir()) {
                    handleBlockPlacement(player, placedState, targetPos);
                }
            });
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * Awards progress for placing a block and tracks it for anti-exploit.
     */
    private void handleBlockPlacement(PlayerEntity player, BlockState state, BlockPos pos) {
        // Get the block identifier (with mod namespace for compatibility)
        String material = net.minecraft.registry.Registries.BLOCK.getId(state.getBlock()).toString();
        // Remove namespace prefix for vanilla blocks to maintain backward compatibility
        if (material.startsWith("minecraft:")) {
            material = material.replace("minecraft:", "");
        }
        // For modded blocks, keep the full identifier (e.g., "modname:custom_block")
        
        // Track this block to prevent exploit (breaking own blocks for rewards)
        blockBreakHandler.trackBlockPlacement(pos, player.getUuid());
        
        LOGGER.debug("Player {} placed {} at {}", player.getName().getString(), material, pos);
        jobController.awardProgress(player.getUuid(), "place", material);
    }
}
