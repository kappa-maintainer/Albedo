package com.hrznstudio.albedo;

import com.google.common.collect.ImmutableMap;
import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.capability.LightCapabilityHandler;
import com.hrznstudio.albedo.tileentity.LightDummyTile;
import com.hrznstudio.albedo.tileentity.MetaSensitiveDummy;
import com.hrznstudio.albedo.tileentity.SwitchableRedstoneDummy;
import com.hrznstudio.albedo.util.ShaderUtil;
import com.hrznstudio.albedo.util.TriConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(modid = "albedo", version = "1.2.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.12.2]")
public class Albedo {

    public static Map<Block, TriConsumer<BlockPos, IBlockState, GatherLightsEvent>> MAP = new LinkedHashMap<>();

    public static Logger LOGGER;

    public Albedo() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static TriConsumer<BlockPos, IBlockState, GatherLightsEvent> getLightHandler(Block block) {
        return MAP.get(block);
    }

    public static ImmutableMap<Block, TriConsumer<BlockPos, IBlockState, GatherLightsEvent>> getBlockHandlers() {
        return ImmutableMap.copyOf(MAP);
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        LightCapabilityHandler.regCapability();
        GameRegistry.registerTileEntity(LightDummyTile.class, new ResourceLocation("albedo", "light_dummy"));
        GameRegistry.registerTileEntity(SwitchableRedstoneDummy.class, new ResourceLocation("albedo", "light_dummy_switchable"));
        GameRegistry.registerTileEntity(MetaSensitiveDummy.class, new ResourceLocation("albedo", "light_dummy_meta"));

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ConfigHandler.reloadList();
        EventManager.handleNonTEBlock();
    }

    @EventHandler
    public void loadComplete(FMLPostInitializationEvent event) {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ShaderUtil());
        MinecraftForge.EVENT_BUS.register(new EventManager());

    }

    public static void registerBlockHandler(Block block, TriConsumer<BlockPos, IBlockState, GatherLightsEvent> consumer) {
        MAP.put(block, consumer);
    }

    /*public void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventManager());
        MinecraftForge.EVENT_BUS.register(new ConfigManager());
        registerBlockHandler(Blocks.REDSTONE_TORCH, (pos, state, evt) -> {
            if (state.get(BlockRedstoneTorch.LIT)) {
                evt.add(Light.builder()
                        .pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                        .color(1.0f, 0, 0, 1.0f)
                        .radius(6)
                        .build());
            }
        });
    }*/
}