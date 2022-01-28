package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import com.hrznstudio.albedo.lighting.LightColor;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TorchCapProvider implements ICapabilityProvider {
    LightColor color = null;

    public TorchCapProvider(LightColor c) {
        this.color = c;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return true;
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new TorchLightProvider();
        else
            return null;
    }

    private class TorchLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {
            if(!context.isInWater()) {
                event.add(Light.builder().pos(context).color(color).radius(color.rad).build());
            }

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            event.add(Light.builder().pos(context.getPos().add(0.5f, 0.5f, 0.5f)).color(color).radius(color.rad).build());

        }
    }
}
