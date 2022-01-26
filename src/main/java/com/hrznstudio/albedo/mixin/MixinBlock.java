package com.hrznstudio.albedo.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Block.class})
public class MixinBlock implements ILightProviderBlock{
    private boolean isLightProvider = false;

    public boolean hasProvider() {
        return isLightProvider;
    }
    public void setProvider(boolean value) {
        isLightProvider = value;
    }
}
