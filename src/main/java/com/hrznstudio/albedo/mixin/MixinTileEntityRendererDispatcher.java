package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.event.RenderTileEntityEvent;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntityRendererDispatcher.class})
public class MixinTileEntityRendererDispatcher {
    @Inject(at = {@At("HEAD")}, method = {"render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V"})
    public void render(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, float p_192854_10_, CallbackInfo ci) {
        RenderTileEntityEvent.postNewEvent(tileEntityIn);
    }

}
