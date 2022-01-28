package com.hrznstudio.albedo.tileentity;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;

public class SwitchableRedstoneDummy extends LightDummyTile{
    public SwitchableRedstoneDummy(ILightProviderBlock block) {

        this.color = block.getLightColor();
    }
}
