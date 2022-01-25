package com.hrznstudio.albedo;

import com.hrznstudio.albedo.event.*;
import com.hrznstudio.albedo.lighting.ILightProvider;
import com.hrznstudio.albedo.lighting.Light;
import com.hrznstudio.albedo.lighting.LightManager;
import com.hrznstudio.albedo.util.ShaderManager;
import com.hrznstudio.albedo.util.ShaderUtil;
import com.hrznstudio.albedo.util.TriConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EventManager {
    public static final Map<BlockPos, List<Light>> EXISTING = Collections.synchronizedMap(new LinkedHashMap<>());
    public static boolean isGui = false;
    int ticks = 0;
    boolean postedLights = false;
    boolean precedesEntities = true;
    String section = "";
    Thread thread;

    public void startThread() {
        thread = new Thread(() -> {

            while (!thread.isInterrupted()) {
                if (!ConfigManager.isLightingEnabled()) {
                    EXISTING.clear();
                    return;
                }
                if (Minecraft.getMinecraft().player != null) {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (Minecraft.getMinecraft().world != null) {
                        WorldClient reader = Minecraft.getMinecraft().world;
                        BlockPos playerPos = player.getPosition();
                        int maxDistance = ConfigManager.maxDistance;
                        int r = maxDistance / 2;
                        Iterable<BlockPos.MutableBlockPos> posIterable = BlockPos.getAllInBoxMutable(playerPos.add(-r, -r, -r), playerPos.add(r, r, r));
                        for (BlockPos.MutableBlockPos pos : posIterable) {
                            Vec3d cameraPosition = LightManager.cameraPos;
                            ICamera camera = LightManager.camera;
                            IBlockState state = reader.getBlockState(pos);
                            ArrayList<Light> lights = new ArrayList<>();
                            GatherLightsEvent lightsEvent = new GatherLightsEvent(lights, maxDistance, cameraPosition, camera);
                            TriConsumer<BlockPos, IBlockState, GatherLightsEvent> consumer = Albedo.MAP.get(state.getBlock());
                            if (consumer != null)
                                consumer.apply(pos, state, lightsEvent);

                            /*if (Item.getItemFromBlock(state.getBlock()).getDefaultInstance().hasCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, null)) {
                                Item.getItemFromBlock(state.getBlock()).getDefaultInstance().getCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, null).gatherLights(lightsEvent, pos);
                            }*/

                            if (lights.isEmpty()) {
                                EXISTING.remove(pos);
                            } else {
                                EXISTING.put(pos.toImmutable(), lights);
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

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
                        if (thread == null || !thread.isAlive()) {
                            startThread();
                        }
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
    public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack item = event.getObject();
        String regname = event.getObject().getItem().getRegistryName().toString();
        if (regname.equals("minecraft:torch")) {
            event.addCapability(new ResourceLocation("albedo", "light_provider"), new ICapabilityProvider() {

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == Albedo.LIGHT_PROVIDER_CAPABILITY;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return (T) new TorchLightProvider();
                }
            });
            Albedo.registerBlockHandler(Block.getBlockFromItem(item.getItem()), (blockPos, iBlockState, gatherLightsEvent) -> gatherLightsEvent.add(Light.builder()
                    .pos(
                            blockPos.getX(),
                            blockPos.getY(),
                            blockPos.getZ()
                    )
                    .color(1.0f, 0.78431374f, 0)
                    .color(1.0f, 1.0f, 1.0f)
                    .direction(10f, 0f, 0f, (float) (Math.PI / 8.0))
                    .radius(10)
                    .build()
            ));
        } else if (regname.equals("minecraft:redstone_torch")) {
            event.addCapability(new ResourceLocation("albedo", "light_provider"), new ICapabilityProvider() {
                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == Albedo.LIGHT_PROVIDER_CAPABILITY;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return (T) new RedstoneTorchProvider();
                }
            });
            Albedo.registerBlockHandler(Block.getBlockFromItem(item.getItem()), (blockPos, iBlockState, gatherLightsEvent) -> gatherLightsEvent.add(Light.builder()
                    .pos(
                            blockPos.getX(),
                            blockPos.getY(),
                            blockPos.getZ()
                    )
                    .color(1.0f, 0, 0)
                    .radius(5)
                    //.color(1, 1, 1)
                    //.direction(10f, 0f, 0f, (float)(Math.PI/8.0))
                    //.direction(heading, (float)(Math.PI/3.0))
                    .build()
            ));
        }

    }

    public static class TorchLightProvider implements ILightProvider {
        @Override
        public void gatherLights(GatherLightsEvent event, Entity entity) {
            event.add(Light.builder()
                    .pos(
                            (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Minecraft.getMinecraft().getRenderPartialTicks()),
                            (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Minecraft.getMinecraft().getRenderPartialTicks()),
                            (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Minecraft.getMinecraft().getRenderPartialTicks())
                    )
                    .color(1.0f, 0.78431374f, 0)
                    .color(1.0f, 1.0f, 1.0f)
                    .direction(10f, 0f, 0f, (float) (Math.PI / 8.0))
                    .radius(10)
                    .build()
            );
        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, BlockPos context) {
            event.add(Light.builder()
                    .pos(
                            context.getX(),
                            context.getY(),
                            context.getZ()
                    )
                    .color(1.0f, 0.78431374f, 0)
                    .color(1.0f, 1.0f, 1.0f)
                    .direction(10f, 0f, 0f, (float) (Math.PI / 8.0))
                    .radius(10)
                    .build()
            );
        }

    }

    public static class RedstoneTorchProvider implements ILightProvider {
        @Override
        public void gatherLights(GatherLightsEvent event, Entity entity) {
            //float theta = entity.ticksExisted / 10f;
            //Vec3d heading = new Vec3d(10, 0, 0).rotateYaw(theta);
            event.add(Light.builder()
                    .pos(
                            (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Minecraft.getMinecraft().getRenderPartialTicks()),
                            (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Minecraft.getMinecraft().getRenderPartialTicks()),
                            (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Minecraft.getMinecraft().getRenderPartialTicks())
                    )
                    .color(1.0f, 0, 0)
                    .radius(15)
                    //.color(1, 1, 1)
                    //.direction(10f, 0f, 0f, (float)(Math.PI/8.0))
                    //.direction(heading, (float)(Math.PI/3.0))
                    .build()
            );
        }

        @Override
        public void gatherLights(GatherLightsEvent event, TileEntity context) {

        }

        @Override
        public void gatherLights(GatherLightsEvent event, BlockPos context) {
            event.add(Light.builder()
                    .pos(
                            context.getX(),
                            context.getY(),
                            context.getZ()
                    )
                    .color(1.0f, 0, 0)
                    .radius(15)
                    //.color(1, 1, 1)
                    //.direction(10f, 0f, 0f, (float)(Math.PI/8.0))
                    //.direction(heading, (float)(Math.PI/3.0))
                    .build());
        }

    }
}