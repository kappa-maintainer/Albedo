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
    public static int maxLights = 100;

    @RangeInt(min = 16, max = 256)
    @Comment({"The maximum distance lights can be before being culled."})
    @LangKey("albedo.config.maxDistance")
    public static int maxDistance = 64;

    @Comment({"Disables albedo lighting."})
    @LangKey("albedo.config.disableLights")
    public static boolean disableLights = false;

    @Comment({"Redstone Stuffs"})
    @LangKey("albedo.config.redstonelights")
    public static boolean redstoneLights = true;

    @Comment({"Items would not light under water, e.g. torch"})
    @LangKey("albedo.config.torchlight")
    @Config.RequiresMcRestart
    public static String[] torchList = {"minecraft:torch|1|1|0.7|0.9|5"
    };

    @Comment({"Items would glow everywhere, e.g. glowstone"})
    @LangKey("albedo.config.constantlight")
    @Config.RequiresMcRestart
    public static String[] constantList = {
            "minecraft:glowstone|0.82|0.82|0|0.8|5",
            "minecraft:lit_pumpkin|0.82|0.82|0|0.8|5",
            "minecraft:sea_lantern|0.35|0.51|0.47|1.2|5",
            "minecraft:magma|0.88|0.39|0.06|0.6|3",
            "minecraft:lava_bucket|0.88|0.39|0.06|1.2|5",
            "minecraft:end_rod|1|1|1|0.9|7"
    };

    @Comment({"Blocks don't have item form or do not need to emit light in hand"})
    @LangKey("albedo.config.blocklight")
    @Config.RequiresMcRestart
    public static String[] blockList = {
            //"minecraft:lava|0.88|0.39|0.06|1.2|5",
            //"minecraft:flowing_lava|0.88|0.39|0.06|1.2|5",
            "minecraft:portal|0.62|0.25|0.95|0.85|5",
            "minecraft:end_portal|1|1|1|0.9|5",
            "minecraft:end_portal_frame|1|1|1|0.9|5",
            "minecraft:fire|1|1|0.7|0.9|6"

    };

    @Comment({"TileEntity"})
    @LangKey("albedo.config.tileentity")
    @Config.RequiresMcRestart
    public static String[] teList = {

    };

    public static boolean isLightingEnabled() {
        return !disableLights;
    }
}