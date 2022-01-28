package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.lighting.Light;
import com.hrznstudio.albedo.lighting.LightColor;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetaSensCapProvider implements ICapabilityProvider {
    LightColor color = null;

    public MetaSensCapProvider(LightColor c) {
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
            return (T) new MetaSensCapProvider.SwitchableLightProvider();
        else
            return null;
    }

    public class SwitchableLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            IBlockState brl = Minecraft.getMinecraft().world.getBlockState(context.getPos());
            LightColor c = ((ILightProviderBlock) (brl.getBlock())).getLightColor();
            event.add(Light.builder().pos(context.getPos().add(0.5f, 0.5f, 0.5f)).color(c).alpha(brl.getValue(BlockRedstoneWire.POWER) / 15.0F * 0.6F).radius(c.rad).build());





        }
    }
}
