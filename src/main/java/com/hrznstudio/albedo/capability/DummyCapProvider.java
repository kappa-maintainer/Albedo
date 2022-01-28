package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import com.hrznstudio.albedo.tileentity.LightDummyTile;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyCapProvider implements ICapabilityProvider {

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new DummyLightProvider();
        else
            return null;
    }

    public class DummyLightProvider implements ILightProvider {
        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            if (!(context instanceof LightDummyTile)) return;
            event.add(Light.builder()
                    .pos(context.getPos())
                    .color(((LightDummyTile) context).getColor())
                    .radius(((LightDummyTile) context).getColor().rad)
                    .build());


        }
    }
}
