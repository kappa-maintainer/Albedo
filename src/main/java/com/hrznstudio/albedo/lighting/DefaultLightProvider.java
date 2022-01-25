package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class DefaultLightProvider implements ILightProvider{
    @Override
    public void gatherLights(GatherLightsEvent event, Entity context) {

    }

    @Override
    public void gatherLights(GatherLightsEvent event, TileEntity context) {

    }

    @Override
    public void gatherLights(GatherLightsEvent event, BlockPos context) {

    }



}
