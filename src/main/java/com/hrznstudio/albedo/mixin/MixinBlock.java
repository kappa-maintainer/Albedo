package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.lighting.LightColor;
import com.hrznstudio.albedo.tileentity.LightDummyTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public abstract class MixinBlock implements ILightProviderBlock {
    @Shadow public abstract String toString();

    private boolean isLightProvider = false;
    private LightColor color;
    public boolean hasProvider() {
        return isLightProvider;
    }
    public void setProvider(boolean value) {
        isLightProvider = value;
    }


    @Override
    public LightColor getLightColor() {
        return color;
    }

    @Override
    public void setLightColor(LightColor color) {
        this.color = color;
    }

    @Inject(at = {@At("RETURN")}, method = "breakBlock")
    private void breakBlock(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci){
        if(((ILightProviderBlock) state.getBlock()).hasProvider()) {
            worldIn.removeTileEntity(pos);
        }
    }

    @Inject(at = {@At("RETURN")}, method = "hasTileEntity(Lnet/minecraft/block/state/IBlockState;)Z", cancellable = true, remap = false)
    private void onHasTE(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(((ILightProviderBlock) state.getBlock()).hasProvider()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = {@At("HEAD")}, method = "createTileEntity", cancellable = true, remap = false)
    private void onCreateTE(World world, IBlockState state, CallbackInfoReturnable<TileEntity> cir) {
        if(((ILightProviderBlock) state.getBlock()).hasProvider()) {
            cir.setReturnValue(new LightDummyTile((ILightProviderBlock) state.getBlock()));
        }
    }
}
