package com.hrznstudio.albedo.lighting;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class LightCapabilityHandler {
    @CapabilityInject(ILightProvider.class)
    public static Capability<ILightProvider> LIGHT_PROVIDER_CAPABILITY;

    public static void regCapability() {
        CapabilityManager.INSTANCE.register(ILightProvider.class, new Capability.IStorage<ILightProvider>() {
            @Nullable
            public NBTBase writeNBT(Capability<ILightProvider> capability, ILightProvider instance, EnumFacing side) {
                return null;
            }

            public void readNBT(Capability<ILightProvider> capability, ILightProvider instance, EnumFacing side, NBTBase nbt) {
            }
        }, com.hrznstudio.albedo.lighting.DefaultLightProvider::new);
    }
}
