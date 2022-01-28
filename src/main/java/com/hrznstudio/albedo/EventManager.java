package com.hrznstudio.albedo;

import com.hrznstudio.albedo.capability.*;
import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.event.*;
import com.hrznstudio.albedo.lighting.*;
import com.hrznstudio.albedo.tileentity.LightDummyTile;
import com.hrznstudio.albedo.tileentity.MetaSensitiveDummy;
import com.hrznstudio.albedo.tileentity.SwitchableRedstoneDummy;
import com.hrznstudio.albedo.util.ShaderManager;
import com.hrznstudio.albedo.util.ShaderUtil;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static com.hrznstudio.albedo.ConfigManager.*;

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

    public static void handleNonTEBlock() {
        for(Block b: Block.REGISTRY) {
            if(redstoneLights) {
                if(b instanceof BlockRedstoneTorch) {
                    ((ILightProviderBlock)b).setProvider(true);
                    ((ILightProviderBlock)b).setLightColor(new LightColor(1.0F,0.0F,0.0F,0.6F,3.0F));
                    continue;
                }
                if(b instanceof BlockRedstoneLight) {
                    ((ILightProviderBlock)b).setProvider(true);
                    ((ILightProviderBlock)b).setLightColor(new LightColor(0.82F,0.82F,0.0F,0.8F,5.0F));
                    continue;
                }
                if(b instanceof BlockRedstoneOre) {
                    ((ILightProviderBlock)b).setProvider(true);
                    ((ILightProviderBlock)b).setLightColor(new LightColor(1.0F,0.0F,0.0F,0.2F,1.5F));
                    continue;
                }
                if(b instanceof BlockRedstoneWire) {
                    ((ILightProviderBlock)b).setProvider(true);
                    ((ILightProviderBlock)b).setLightColor(new LightColor(1.0F,0.0F,0.0F,0.0F,2.0F));
                    continue;
                }
            }
            LightColor color = ConfigHandler.torchLight.get(Item.getItemFromBlock(b));
            if(color != null) {
                ((ILightProviderBlock)b).setProvider(true);
                ((ILightProviderBlock)b).setLightColor(color);
            }
            color = ConfigHandler.constantLight.get(Item.getItemFromBlock(b));
            if(color != null) {
                ((ILightProviderBlock)b).setProvider(true);
                ((ILightProviderBlock)b).setLightColor(color);
            }
            color = ConfigHandler.blockLight.get(b);
            if(color != null) {
                ((ILightProviderBlock)b).setProvider(true);
                ((ILightProviderBlock)b).setLightColor(color);
            }

        }
    }

    @SubscribeEvent
    public void attachTileEntityCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity te = event.getObject();
        ResourceLocation res = TileEntity.getKey(te.getClass());
        if(res != null) {
            LightColor color = ConfigHandler.teLight.get(res.toString());
            if (color != null) {
                event.addCapability(new ResourceLocation("albedo", "constant_light_provider"), new ConstantCapProvider(color));
                return;
            }
        }
        //te list

        //Add TE for Non-te blocks
        if(te instanceof LightDummyTile) {
            if(te instanceof SwitchableRedstoneDummy) {
                event.addCapability(new ResourceLocation("albedo", "switch_light_provider"), new SwitchableLightCapProvider(null));
                return;
            }
            if(te instanceof MetaSensitiveDummy) {
                event.addCapability(new ResourceLocation("albedo", "meta_light_provider"), new MetaSensCapProvider(null));
                return;
            }
            if(torchList.length > 0 || constantList.length > 0 || blockList.length > 0) {
                event.addCapability(new ResourceLocation("albedo", "dummy_light_provider"), new DummyCapProvider());
                return;
            }
        }
        if(te instanceof TileEntityFurnace) {
            event.addCapability(new ResourceLocation("albedo", "furnace_light_provider"), new FurnaceLightCapProvider());
        }
        if(te instanceof TileEntityBeacon) {
            event.addCapability(new ResourceLocation("albedo", "beacon_light_provider"), new BeaconLightCapProvider());
        }
    }

    @SubscribeEvent
    public void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack itemstack = event.getObject();
        Item item = itemstack.getItem();
        LightColor color = ConfigHandler.torchLight.get(item);
        if (color != null) {
            event.addCapability(new ResourceLocation("albedo", "torch_light_provider"), new TorchCapProvider(color));
        }
        color = ConfigHandler.constantLight.get(item);
        if (color != null) {
            event.addCapability(new ResourceLocation("albedo", "constant_light_provider"), new ConstantCapProvider(color));
        }

    }

    @SubscribeEvent
    public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity e = event.getObject();
        if(e instanceof EntityCreeper) {
            event.addCapability(new ResourceLocation("albedo", " creeper_light_provider"), new CreeperLightCapProvider());
        }
        event.addCapability(new ResourceLocation("albedo", "burning_light_provider"), new BurningLightCapProvider());
        /*if(e instanceof EntityLivingBase) {
            event.addCapability(new ResourceLocation("albedo", "light_provider"), new BurningLightCapProvider());
            return;
        }
        if(e instanceof EntityItem) {
            event.addCapability(new ResourceLocation("albedo", "light_provider"), new BurningLightCapProvider());
            return;
        }*/
    }

}