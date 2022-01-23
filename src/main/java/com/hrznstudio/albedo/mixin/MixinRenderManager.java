package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.event.RenderEntityEvent;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderManager.class})
public class MixinRenderManager {
    @Inject(at = {@At("HEAD")}, method = {"renderEntity"})
    public void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci) {
        RenderEntityEvent.postNewEvent(entityIn);
    }
}
