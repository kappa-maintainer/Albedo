package com.hrznstudio.albedo.mixin;

import com.hrznstudio.albedo.Albedo;
import com.hrznstudio.albedo.event.ProfilerStartEvent;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin({Profiler.class})
public class MixinProfiler {
    @Inject(at = {@At("HEAD")}, method = {"endStartSection"})
    public void endStartSection(String name, CallbackInfo ci) {

        ProfilerStartEvent.postNewEvent(name);

    }

}
