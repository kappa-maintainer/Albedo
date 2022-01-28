package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreeperLightCapProvider implements ICapabilityProvider {
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new CreeperLightProvider();
        else
            return null;
    }

    public class CreeperLightProvider implements ILightProvider{

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {
            if(((EntityCreeper) context).getPowered()) {
                event.add(new Light.Builder()
                        .pos(((EntityCreeper) context).getEntityBoundingBox().getCenter())
                        .color( 0.8f, 0.8f, 0.8f, 1.5F)
                        .radius(3)
                        .build());;
            }
            if(((EntityCreeper) context).getCreeperState() ==1) {
                float flash = 1F - (((EntityCreeper) context).getCreeperFlashIntensity(0) / 1.17F + 0.1F);
                NBTTagCompound nbt = new NBTTagCompound();
                ((EntityCreeper) context).writeEntityToNBT(nbt);
                float explosionRadius = nbt.getByte("ExplosionRadius");
                float lightRadius = explosionRadius - (explosionRadius * flash);

                event.add(new Light.Builder()
                        .pos(context.getEntityBoundingBox().getCenter())
                        .color(1.5f, 0.8f, 0f, 1.5F)
                        .radius(lightRadius *2)
                        .build());
            }
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
