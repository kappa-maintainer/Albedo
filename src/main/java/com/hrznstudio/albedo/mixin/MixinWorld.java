package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.tileentity.LightDummyTile;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;


@Mixin({World.class})
public abstract class MixinWorld {

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow
    @Nullable
    public abstract TileEntity getTileEntity(BlockPos pos);

    @Shadow
    public abstract void removeTileEntity(BlockPos pos);

    @Shadow
    public abstract void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn);

    @Shadow
    @Nullable
    protected abstract TileEntity getPendingTileEntityAt(BlockPos pos);

    @Shadow public abstract boolean addTileEntity(TileEntity tile);

    @Inject(at = {@At(value = "HEAD")}, method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"})
    public void beforeSetBlockState(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        //if(Albedo.getLightHandler(oldstate.getBlock()) != null)
        if (this.getPendingTileEntityAt(pos) instanceof LightDummyTile) {

            this.removeTileEntity(pos);


        }

    }

    @Inject(at = {@At(value = "RETURN", ordinal = 3)}, method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"})
    public void afterSetBlockState(BlockPos pos, IBlockState newstate, int flags, CallbackInfoReturnable<Boolean> cir) {
        //if(Albedo.getLightHandler(newstate.getBlock()) != null)
        //if (newstate.getBlock().getRegistryName().equals("minecraft:torch")) {
        if (newstate.getBlock() instanceof BlockTorch) {

            TileEntity dummy = new LightDummyTile();
            dummy.setPos(pos);
            this.setTileEntity(pos, dummy);
        }
    }

}
