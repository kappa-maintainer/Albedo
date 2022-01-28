package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwitchableLightCapProvider implements ICapabilityProvider {
    LightColor color = null;

    public SwitchableLightCapProvider(LightColor c) {
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
            return (T) new SwitchableLightProvider();
        else
            return null;
    }

    public class SwitchableLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            ISwitchableBlock brl = (ISwitchableBlock)
                    (Minecraft.getMinecraft().world.getBlockState(context.getPos()).getBlock());
            LightColor c = ((ILightProviderBlock) brl).getLightColor();
            if(brl.getIsOn()){
                event.add(Light.builder().pos(context.getPos().add(0.5f, 0.5f, 0.5f)).color(c).radius(c.rad).build());
            }




        }
    }
}
