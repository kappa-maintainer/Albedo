package com.hrznstudio.albedo.mixin.tileentity;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin({TileEntityBeacon.class})
public class MixinTileEntityBeacon implements ILightProvider {
    @Override
    public void gatherLights(GatherLightsEvent event) {
        if (((TileEntityBeacon)(Object) this).getLevels() > 0) {
            float r, g, b, h;
            List<TileEntityBeacon.BeamSegment> segs = (((TileEntityBeacon)(Object) this).getBeamSegments());
            if (segs.size() == 0) return;
            int count = 0;
            h = 1;
            for (TileEntityBeacon.BeamSegment s : segs) {
                count++;
                if (count == 1) {
                    h += s.getHeight();
                    continue;
                }
                float[] c = s.getColors();
                r = c[0];
                g = c[1];
                b = c[2];
                event.add(new Light.Builder().pos(((TileEntityBeacon) (Object) this).getPos().add(0, h, 0)).color(r, g, b, 1F).radius(20).build());
                h += s.getHeight();
            }
        }
    }
}
