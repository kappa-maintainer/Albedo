package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.lighting.LightColor;

public interface ILightProviderBlock {
    boolean hasProvider();
    void setProvider(boolean value);
    LightColor getLightColor();
    void setLightColor(LightColor color);

}
