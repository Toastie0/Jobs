package net.toastie.jobs.handlers;

import net.toastie.jobs.controller.JobController;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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
    
    public BlockPlaceHandler(JobController jobController, BlockBreakHandler blockBreakHandler) {
        this.jobController = jobController;
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
        
        // Only process block items
        if (!(stack.getItem() instanceof BlockItem)) {
            return ActionResult.PASS;
        }
        
        BlockItem blockItem = (BlockItem) stack.getItem();
        BlockPos targetPos = hitResult.getBlockPos().offset(hitResult.getSide());
        
        // Check after a short delay (1 tick) to ensure block is placed
        ((ServerWorld) world).getServer().execute(() -> {
            BlockState placedState = world.getBlockState(targetPos);
            if (!placedState.isAir()) {
                handleBlockPlacement(player, placedState, targetPos);
            }
        });
        
        return ActionResult.PASS;
    }
    
    /**
     * Awards progress for placing a block.
     */
    private void handleBlockPlacement(PlayerEntity player, BlockState state, BlockPos pos) {
        // Don't reward for placing crops/seeds - those are for Farmer job
        if (state.getBlock() instanceof CropBlock) {
            LOGGER.info("Skipping crop placement - not a Builder action");
            return;
        }
        
        String material = getMaterialId(state);
        jobController.awardProgress(player.getUuid(), "place", material);
    }
    
    /**
     * Gets the material ID, removing minecraft: prefix for vanilla blocks.
     */
    private String getMaterialId(BlockState state) {
        String id = net.minecraft.registry.Registries.BLOCK.getId(state.getBlock()).toString();
        return id.startsWith("minecraft:") ? id.substring(10) : id;
    }
}
