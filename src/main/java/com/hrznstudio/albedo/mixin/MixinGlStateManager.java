package com.hrznstudio.albedo.mixin;

import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hrznstudio.albedo.util.RenderUtil.enableLightingUniforms;

@Mixin({GlStateManager.class})
public class MixinGlStateManager {
    @Inject(at = {@At("RETURN")}, method = {"enableLighting"})
    private static void enableLighting(CallbackInfo ci) {
        enableLightingUniforms();
    }

    @Inject(at = {@At("RETURN")}, method = {"disableLighting"})
    private static void disableLighting(CallbackInfo ci) {
        enableLightingUniforms();
    }
}
