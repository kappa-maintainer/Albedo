package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ILightProvider {
    @SideOnly(Side.CLIENT)
    default void gatherLights(GatherLightsEvent event) {}


}
