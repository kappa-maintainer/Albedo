package com.hrznstudio.albedo.mixin;



import com.hrznstudio.albedo.tileentity.LightDummyTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin({Chunk.class})
public abstract class MixinChunk {
    @Shadow
    private World world;

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasTileEntity(Lnet/minecraft/block/state/IBlockState;)Z", ordinal = 0)}, method = {"setBlockState"}, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void removeOldTE(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir, int i, int j, int k, int l, int i1, IBlockState iblockstate, Block block, Block block1, int k1, ExtendedBlockStorage extendedblockstorage, boolean flag, TileEntity var14) {
        if(((ILightProviderBlock)block1).hasProvider()) {
            TileEntity te = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
            if (te != null && te.shouldRefresh(this.world, pos, iblockstate, state))
                this.world.removeTileEntity(pos);
        }
    }

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasTileEntity(Lnet/minecraft/block/state/IBlockState;)Z", ordinal = 2)}, method = {"setBlockState"}, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addNewTE(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir, int i, int j, int k, int l, int i1, IBlockState iblockstate, Block block, Block block1, int k1, ExtendedBlockStorage extendedblockstorage, boolean flag, int var14) {
        if(((ILightProviderBlock)block).hasProvider()) {
            TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

            if (tileentity1 == null)
            {
                tileentity1 = new LightDummyTile();
                tileentity1.setPos(pos);
                this.world.setTileEntity(pos, tileentity1);
            }

        }
    }

    @Shadow
    private TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType check) {
        return null;
    }




}
