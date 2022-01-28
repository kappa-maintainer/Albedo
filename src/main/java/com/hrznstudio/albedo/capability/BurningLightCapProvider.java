package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BurningLightCapProvider implements ICapabilityProvider {
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new BurningLightProvider();
        else
            return null;
    }

    public class BurningLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {
            if(context.isBurning() && context.canRenderOnFire()) {
                event.add(new Light.Builder()
                        .pos(context.getEntityBoundingBox().getCenter())
                        .color(0.8f, 0.4f, 0f, 2F)
                        .radius((float)(context.getRenderBoundingBox().getAverageEdgeLength() * 10))
                        .build());
            }
        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {

        }
    }
}
