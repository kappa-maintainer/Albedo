package com.hrznstudio.albedo.mixin.tileentity;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.tileentity.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TileEntityFurnace.class})
public class MixinTileEntityFurnace implements ILightProvider {

    @Shadow
    public boolean isBurning(){
        return false;
    }

    @Override
    public void gatherLights(GatherLightsEvent event) {
        if(isBurning()) {
            event.add(new Light.Builder().pos(((TileEntityFurnace)(Object)this).getPos()).color(1F, 0.5F, 0F, 1F).radius(7).build());
        }
    }
}
