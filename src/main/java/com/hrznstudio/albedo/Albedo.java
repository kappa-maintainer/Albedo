package com.hrznstudio.albedo;

import com.hrznstudio.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "albedo", version = "1.2.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.12.2]")
public class Albedo {



    public static Logger LOGGER;

    public Albedo() {
        MinecraftForge.EVENT_BUS.register(this);
    }


    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ConfigHandler.reloadList();
    }

    @EventHandler
    public void loadComplete(FMLPostInitializationEvent event) {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ShaderUtil());
        MinecraftForge.EVENT_BUS.register(new EventManager());

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