package com.hrznstudio.albedo.mixin.entity;

import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityCreeper.class})
public class MixinEntityCreeper implements ILightProvider {
    @Override
    public void gatherLights(GatherLightsEvent event) {
        if(((EntityCreeper) (Object) this).getPowered()) {
            event.add(new Light.Builder()
                    .pos(((EntityCreeper) (Object) this).getEntityBoundingBox().getCenter())
                    .color( 0.8f, 0.8f, 0.8f, 1.5F)
                    .radius(3)
                    .build());
        }
        if(((EntityCreeper) (Object) this).getCreeperState() ==1) {
            float flash = 1F - (((EntityCreeper) (Object) this).getCreeperFlashIntensity(0) / 1.17F + 0.1F);
            NBTTagCompound nbt = new NBTTagCompound();
            ((EntityCreeper) (Object) this).writeEntityToNBT(nbt);
            float explosionRadius = nbt.getByte("ExplosionRadius");
            float lightRadius = explosionRadius - (explosionRadius * flash);

            event.add(new Light.Builder()
                    .pos(((EntityCreeper)(Object) this).getEntityBoundingBox().getCenter())
                    .color(1.5f, 0.8f, 0f, 1.5F)
                    .radius(lightRadius *2)
                    .build());
        }
        if(((EntityCreeper)(Object) this).isBurning() && ((EntityCreeper)(Object) this).canRenderOnFire()) {
            event.add(new Light.Builder()
                    .pos(((EntityCreeper)(Object) this).getEntityBoundingBox().getCenter())
                    .color(0.8f, 0.4f, 0f, 2F)
                    .radius((float)(((EntityCreeper)(Object) this).getRenderBoundingBox().getAverageEdgeLength() * 10))
                    .build());
        }
    }
}
