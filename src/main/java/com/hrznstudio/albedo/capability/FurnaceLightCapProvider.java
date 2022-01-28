package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceLightCapProvider implements ICapabilityProvider {
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new FurnaceLightProvider();
        else
            return null;
    }

    public class FurnaceLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            if(((TileEntityFurnace) context).isBurning()) {
                event.add(new Light.Builder().pos(context.getPos()).color(1F, 0.5F, 0F, 1F).radius(7).build());
            }
        }
    }
}
