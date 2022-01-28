package com.hrznstudio.albedo.tileentity;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;

public class MetaSensitiveDummy extends LightDummyTile {
    public MetaSensitiveDummy(ILightProviderBlock block) {
        this.color = block.getLightColor();

    }
}
