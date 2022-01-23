package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ILightProvider {
    @SideOnly(Side.CLIENT)
    void gatherLights(GatherLightsEvent event, Entity context);
}
