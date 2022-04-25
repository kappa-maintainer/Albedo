package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.event.GatherLightsEvent;

public interface IBlockLightProvider {
    //May use in future non-te block lighting mixin.
    //Blocks should register themselves to event bus in constructor.
    //The unregistering will be handled by a setBlockState mixin.
    void handleLightGather(GatherLightsEvent event);
}
