package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.lighting.ISwitchableBlock;
import com.hrznstudio.albedo.tileentity.SwitchableRedstoneDummy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({BlockRedstoneLight.class})
public class MixinRedstoneLightBlock extends Block implements ISwitchableBlock {
    @Shadow @Final private boolean isOn;

    public MixinRedstoneLightBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    public boolean getIsOn() {
        return this.isOn;
    }
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
            return new SwitchableRedstoneDummy((ILightProviderBlock) state.getBlock());

    }

}
