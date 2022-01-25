package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ILightProvider {
    @SideOnly(Side.CLIENT)
    void gatherLights(GatherLightsEvent event, Entity context);
    @SideOnly(Side.CLIENT)
    void gatherLights(GatherLightsEvent event, TileEntity context);
    @SideOnly(Side.CLIENT)
    void gatherLights(GatherLightsEvent event, BlockPos context);

}
