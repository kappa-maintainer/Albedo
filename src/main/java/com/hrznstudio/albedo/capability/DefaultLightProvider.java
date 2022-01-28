package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class DefaultLightProvider implements ILightProvider {
    @Override
    public void gatherLights(GatherLightsEvent event, Entity context) {

    }

    @Override
    public void gatherLights(GatherLightsEvent event, TileEntity context) {

    }



}
