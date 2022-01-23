package com.hrznstudio.albedo;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;

@Config(modid = "albedo", name = "Albedo")
public class ConfigManager {
    @RangeInt(min = 0, max = 1000)
    @Comment({"The maximum number of lights allowed to render in a scene. Lights are sorted nearest-first, so further-away lights will be culled after nearer lights."})
    @LangKey("albedo.config.maxLights")
    public static int maxLights = 40;

    @RangeInt(min = 16, max = 256)
    @Comment({"The maximum distance lights can be before being culled."})
    @LangKey("albedo.config.maxDistance")
    public static int maxDistance = 64;

    @Comment({"Disables albedo lighting."})
    @LangKey("albedo.config.disableLights")
    public static boolean disableLights = false;

    @Comment({"Holiday Events"})
    @LangKey("albedo.config.holidy")
    public static boolean holiday = true;

    public static boolean isLightingEnabled() {
        return !disableLights;
    }
}