package com.hrznstudio.albedo.capability;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeaconLightCapProvider implements ICapabilityProvider {

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == LightCapabilityHandler.LIGHT_PROVIDER_CAPABILITY)
            return (T) new BeaconLightProvider();
        else
            return null;
    }

    public class BeaconLightProvider implements ILightProvider {

        @Override
        public void gatherLights(GatherLightsEvent event, Entity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {
            if (((TileEntityBeacon) context).getLevels() > 0) {
                float r, g, b, h;
                List<TileEntityBeacon.BeamSegment> segs = ((TileEntityBeacon) context).getBeamSegments();
                if (segs.size() == 0) return;
                int count = 0;
                h = 1;
                segments:for (TileEntityBeacon.BeamSegment s : segs) {
                    TileEntityBeacon.BeamSegment se = ((TileEntityBeacon) context).getBeamSegments().get(count);
                    count++;
                    if(count == 1){
                        h += se.getHeight();
                        continue segments;
                    }
                    float[] c = se.getColors();
                    c = c == null ? EnumDyeColor.WHITE.getColorComponentValues(): c;
                    r = c[0];
                    g = c[1];
                    b = c[2];
                    event.add(new Light.Builder().pos(context.getPos().add(0, h, 0)).color( r, g, b, 1F).radius(20).build());
                    h += se.getHeight();
                }
            }
        }
    }
}
