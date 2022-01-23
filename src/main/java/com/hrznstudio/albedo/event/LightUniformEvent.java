package com.hrznstudio.albedo.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class LightUniformEvent extends Event {
    public LightUniformEvent() {
        super();
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
