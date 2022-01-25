package com.hrznstudio.albedo.lighting;

import com.hrznstudio.albedo.Albedo;
import com.hrznstudio.albedo.ConfigManager;
import com.hrznstudio.albedo.event.GatherLightsEvent;
import com.hrznstudio.albedo.util.ShaderManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Comparator;

public class LightManager {
    public static Vec3d cameraPos;
    public static ICamera camera;
    public static ArrayList<Light> lights = new ArrayList<Light>();
    public static DistComparator distComparator = new DistComparator();

    public static void uploadLights() {
        ShaderManager shader = ShaderManager.getCurrentShader();
        shader.setUniform("lightCount", lights.size());
        for (int i = 0; i < Math.min(ConfigManager.maxLights, lights.size()); i++) {
            if (i < lights.size()) {
                Light l = lights.get(i);
                shader.setUniform("lights[" + i + "].position", l.x, l.y, l.z);
                shader.setUniform("lights[" + i + "].color", l.r, l.g, l.b, l.a);
                shader.setUniform("lights[" + i + "].heading", l.rx, l.ry, l.rz);
                shader.setUniform("lights[" + i + "].angle", l.angle);
            } else {
                //shader.setUniform("lights[" + i + "].position", 0, 0, 0);
                //shader.setUniform("lights[" + i + "].color", 0, 0, 0, 0);
                //shader.setUniform("lights[" + i + "].heading", 0, 0, 0);
                //shader.setUniform("lights[" + i + "].angle", 0);
            }
        }
    }

    private static Vec3d interpolate(Entity entity, float partialTicks) {
        return new Vec3d(
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks
        );
    }

    public static void update(World world) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity cameraEntity = mc.getRenderViewEntity();
        if (cameraEntity != null) {
            cameraPos = interpolate(cameraEntity, mc.getRenderPartialTicks());
            camera = new Frustum();
            camera.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
        } else {
            if (cameraPos == null) {
                cameraPos = new Vec3d(0, 0, 0);
            }
            camera = null;
            return;
        }

        GatherLightsEvent event = new GatherLightsEvent(lights, ConfigManager.maxDistance, cameraPos, camera);
        MinecraftForge.EVENT_BUS.post(event);

        int maxDist = ConfigManager.maxDistance;

        for (Entity e : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                cameraPos.x - maxDist,
                cameraPos.y - maxDist,
                cameraPos.z - maxDist,
                cameraPos.x + maxDist,
                cameraPos.y + maxDist,
                cameraPos.z + maxDist
        ))) {
            if (e instanceof EntityItem) {
                if(((EntityItem) e).getItem().hasCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH)) {
                    ((EntityItem) e).getItem().getCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH).gatherLights(event, e);
                }
            }
            for (ItemStack itemStack : e.getHeldEquipment()) {
                if(itemStack.hasCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH)) {
                    itemStack.getCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH).gatherLights(event, e);
                }
            }
            for (ItemStack itemStack : e.getArmorInventoryList()) {
                if(itemStack.hasCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH)) {
                    itemStack.getCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, EnumFacing.NORTH).gatherLights(event, e);
                }
            }
        }

        for (TileEntity t : world.loadedTileEntityList) {
            if(t.hasCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, null)) {
                t.getCapability(Albedo.LIGHT_PROVIDER_CAPABILITY, null).gatherLights(event, t);
            }
        }

        lights.sort(distComparator);
    }

    public static void clear() {
        lights.clear();
    }

    public static class DistComparator implements Comparator<Light> {
        @Override
        public int compare(Light a, Light b) {
            double dist1 = cameraPos.squareDistanceTo(a.x, a.y, a.z);
            double dist2 = cameraPos.squareDistanceTo(b.x, b.y, b.z);
            return Double.compare(dist1, dist2);
        }
    }
}