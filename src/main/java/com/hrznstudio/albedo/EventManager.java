package com.hrznstudio.albedo;

import com.hrznstudio.albedo.event.*;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import com.hrznstudio.albedo.lighting.LightColor;
import com.hrznstudio.albedo.lighting.LightManager;
import com.hrznstudio.albedo.util.ShaderManager;
import com.hrznstudio.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    public static final Map<BlockPos, List<Light>> EXISTING = Collections.synchronizedMap(new LinkedHashMap<>());
    public static boolean isGui = false;
    int ticks = 0;
    boolean postedLights = false;
    boolean precedesEntities = true;
    String section = "";



    @SubscribeEvent
    public void onProfilerChange(ProfilerStartEvent event) {
        section = event.getSection();
        if (ConfigManager.isLightingEnabled()) {
            switch (section) {
                case "terrain":
                    isGui = false;
                    precedesEntities = true;
                    ShaderUtil.fastLightProgram.useShader();
                    ShaderUtil.fastLightProgram.setUniform("ticks", ticks + Minecraft.getMinecraft().getRenderPartialTicks());
                    ShaderUtil.fastLightProgram.setUniform("sampler", 0);
                    ShaderUtil.fastLightProgram.setUniform("lightmap", 1);
                    ShaderUtil.fastLightProgram.setUniform("playerPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                    if (!postedLights) {
                        EXISTING.forEach((pos, lights) -> LightManager.lights.addAll(lights));
                        LightManager.update(Minecraft.getMinecraft().world);
                        ShaderManager.stopShader();
                        MinecraftForge.EVENT_BUS.post(new LightUniformEvent());
                        ShaderUtil.fastLightProgram.useShader();
                        LightManager.uploadLights();
                        ShaderUtil.entityLightProgram.useShader();
                        ShaderUtil.entityLightProgram.setUniform("ticks", ticks + Minecraft.getMinecraft().getRenderPartialTicks());
                        ShaderUtil.entityLightProgram.setUniform("sampler", 0);
                        ShaderUtil.entityLightProgram.setUniform("lightmap", 1);
                        LightManager.uploadLights();
                        ShaderUtil.entityLightProgram.setUniform("playerPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                        ShaderUtil.entityLightProgram.setUniform("lightingEnabled", GL11.glIsEnabled(GL11.GL_LIGHTING));
                        ShaderUtil.fastLightProgram.useShader();
                        postedLights = true;
                        LightManager.clear();
                    }
                    break;

                case "sky":
                case "outline":
                case "aboveClouds":
                case "destroyProgress":
                case "weather":
                    ShaderManager.stopShader();
                    break;
                case "litParticles":
                    ShaderUtil.fastLightProgram.useShader();
                    ShaderUtil.fastLightProgram.setUniform("sampler", 0);
                    ShaderUtil.fastLightProgram.setUniform("lightmap", 1);
                    ShaderUtil.fastLightProgram.setUniform("playerPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                    ShaderUtil.fastLightProgram.setUniform("chunkX", 0);
                    ShaderUtil.fastLightProgram.setUniform("chunkY", 0);
                    ShaderUtil.fastLightProgram.setUniform("chunkZ", 0);
                    break;
                case "particles":
                    ShaderUtil.entityLightProgram.useShader();
                    ShaderUtil.fastLightProgram.setUniform("playerPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                    break;
                case "entities":
                    if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                        ShaderUtil.entityLightProgram.useShader();
                        ShaderUtil.entityLightProgram.setUniform("lightingEnabled", true);
                        ShaderUtil.entityLightProgram.setUniform("fogIntensity", Minecraft.getMinecraft().world.provider instanceof WorldProviderHell ? 0.015625f : 1.0f);

                    }
                    break;
                case "blockEntities":
                    if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                        ShaderUtil.entityLightProgram.useShader();
                        ShaderUtil.entityLightProgram.setUniform("lightingEnabled", true);
                    }
                    break;
                case "translucent":
                    ShaderUtil.fastLightProgram.useShader();
                    ShaderUtil.fastLightProgram.setUniform("sampler", 0);
                    ShaderUtil.fastLightProgram.setUniform("lightmap", 1);
                    ShaderUtil.fastLightProgram.setUniform("playerPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                    break;
                case "hand":
                    ShaderUtil.entityLightProgram.useShader();
                    ShaderUtil.entityLightProgram.setUniform("entityPos", (float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posY, (float) Minecraft.getMinecraft().player.posZ);
                    ShaderUtil.entityLightProgram.setUniform("colorFacor", 1F, 1F, 1F, 0F);
                    precedesEntities = true;
                    break;
                case "gui":
                    isGui = true;
                    ShaderManager.stopShader();
                    break;
            }
        }
    }


    @SubscribeEvent
    public void onRenderEntity(RenderEntityEvent event) {
        Entity e = event.getEntity();
        if (ConfigManager.isLightingEnabled()) {
            if (e instanceof EntityLightningBolt) {
                ShaderManager.stopShader();
            } else if (section.equalsIgnoreCase("entities") || section.equalsIgnoreCase("blockEntities")) {
                ShaderUtil.entityLightProgram.useShader();
            }
            if (ShaderManager.isCurrentShader(ShaderUtil.entityLightProgram)) {
                ShaderUtil.entityLightProgram.setUniform("entityPos", (float) e.posX, (float) e.posY + e.height / 2.0f, (float) e.posZ);
                ShaderUtil.entityLightProgram.setUniform("colorFacor", 1F, 1F, 1F, 0F);
                if (event.getEntity() instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) e;
                    if (elb.hurtTime > 0 || elb.deathTime > 0)
                        ShaderUtil.entityLightProgram.setUniform("colorFacor", 1F, 0F, 0F, 0.35F);
                }

            }
        }
    }

    @SubscribeEvent
    public void onRenderTileEntity(RenderTileEntityEvent event) {
        if (ConfigManager.isLightingEnabled()) {
            if (event.getEntity() instanceof TileEntityEndPortal || event.getEntity() instanceof TileEntityEndGateway) {
                ShaderManager.stopShader();
            } else if (section.equalsIgnoreCase("entities") || section.equalsIgnoreCase("blockEntities")) {
                ShaderUtil.entityLightProgram.useShader();
            }
            if (ShaderManager.isCurrentShader(ShaderUtil.entityLightProgram)) {
                ShaderUtil.entityLightProgram.setUniform("entityPos", (float) event.getEntity().getPos().getX(), (float) event.getEntity().getPos().getY(), (float) event.getEntity().getPos().getZ());
                //ShaderUtil.entityLightProgram.setUniform("colorMult", 1f, 1f, 1f, 0f);
            }
        }
    }

    @SubscribeEvent
    public void onRenderChunk(RenderChunkUniformsEvent event) {
        if (ConfigManager.isLightingEnabled()) {
            if (ShaderManager.isCurrentShader(ShaderUtil.fastLightProgram)) {
                BlockPos pos = event.getChunk().getPosition();
                ShaderUtil.fastLightProgram.setUniform("chunkX", pos.getX());
                ShaderUtil.fastLightProgram.setUniform("chunkY", pos.getY());
                ShaderUtil.fastLightProgram.setUniform("chunkZ", pos.getZ());
            }
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        postedLights = false;
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            GlStateManager.disableLighting();
            ShaderManager.stopShader();
        }
    }
    @SubscribeEvent
    public void renderBurning(RenderEntityEvent evnt){
        Entity entity = evnt.getEntity();
        if(entity != null){
            if(entity.isBurning() && entity.canRenderOnFire()){
                LightManager.addLight(new Light.Builder()
                        .pos(entity.getEntityBoundingBox().getCenter())
                        .color(0.8f, 0.4f, 0f, 2F)
                        .radius((float)(entity.getRenderBoundingBox().getAverageEdgeLength() * 10))
                        .build());
            }
        }
    }

    @SubscribeEvent
    public void renderItemEntityLight(RenderEntityEvent evnt){
        Entity entity = evnt.getEntity();
        LightColor color;
        if(entity instanceof EntityItem){
            color = ConfigHandler.constantLight.get(((EntityItem) entity).getItem().getItem());
            if(color != null) {
                LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());
            }
            if(!entity.isInWater()) {
                color = ConfigHandler.torchLight.get(((EntityItem) entity).getItem().getItem());
                if(color != null)
                    LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());

            }
        }
    }
    @SubscribeEvent
    public void renderLivingEntityLight(RenderEntityEvent evnt){
        Entity entity = evnt.getEntity();
        LightColor color;
        if(entity instanceof EntityLivingBase){
            color = ConfigHandler.constantLight.get(((EntityLivingBase) entity).getHeldItemMainhand().getItem());
            if(color != null) {
                LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());

                LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());
            }
            if (!entity.isInWater()) {
                color = ConfigHandler.torchLight.get(((EntityLivingBase) entity).getHeldItemMainhand().getItem());
                if(color != null) {
                    LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());

                    LightManager.addLight(new Light.Builder().color(color).pos(entity).noDir().build());

                }
            }
        }
    }

}