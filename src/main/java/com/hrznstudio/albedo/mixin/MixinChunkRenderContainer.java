package com.hrznstudio.albedo.mixin;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hrznstudio.albedo.util.RenderUtil.renderChunkUniforms;


@Mixin({ChunkRenderContainer.class})
public abstract class MixinChunkRenderContainer {

    @Inject(at = {@At("RETURN")}, method = {"preRenderChunk"})
    public void preRenderChunk(RenderChunk renderChunkIn, CallbackInfo ci) {
        renderChunkUniforms(renderChunkIn);
    }
}
