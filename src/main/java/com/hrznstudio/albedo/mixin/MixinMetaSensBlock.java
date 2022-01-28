package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.tileentity.MetaSensitiveDummy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockRedstoneWire.class})
public class MixinMetaSensBlock extends Block {


    public MixinMetaSensBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return new MetaSensitiveDummy((ILightProviderBlock) state.getBlock());

    }
}
