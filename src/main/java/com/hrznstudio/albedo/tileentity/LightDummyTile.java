package com.hrznstudio.albedo.tileentity;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class LightDummyTile extends TileEntity implements ILightProvider {
    @Override
    public void gatherLights(GatherLightsEvent event, Entity context) {

    }

    @Override
    public void gatherLights(GatherLightsEvent event, TileEntity context) {
        event.add(Light.builder()
                .pos(
                        context.getPos().getX(),
                        context.getPos().getY(),
                        context.getPos().getZ()
                )
                .color(1.0f, 0, 1.0f)
                .radius(15)
                //.color(1, 1, 1)
                //.direction(10f, 0f, 0f, (float)(Math.PI/8.0))
                //.direction(heading, (float)(Math.PI/3.0))
                .build());
    }

}
