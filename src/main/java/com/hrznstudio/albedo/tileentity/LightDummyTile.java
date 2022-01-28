package com.hrznstudio.albedo.tileentity;

import com.hrznstudio.albedo.lighting.ILightProviderBlock;
import com.hrznstudio.albedo.lighting.LightColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class LightDummyTile extends TileEntity{
    //Deal with block meta some other time
    //ILightProviderBlock lightblocktype;
    public LightColor color = new LightColor(0.0F,0.0F,0.0F,0.0F,0.0F);
    public LightDummyTile(ILightProviderBlock posblock) {
        this.color = posblock.getLightColor();
    }
    public LightColor getColor() {return this.color;}
    //For chunk loading?
    public LightDummyTile() {
        super();
    }



    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setFloat("r", color.r);
        compound.setFloat("g", color.g);
        compound.setFloat("b", color.b);
        compound.setFloat("a", color.a);
        compound.setFloat("rad", color.rad);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.color = new LightColor(
                compound.getFloat("r"),
                compound.getFloat("g"),
                compound.getFloat("b"),
                compound.getFloat("a"),
                compound.getFloat("rad"));
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = this.writeToNBT(new NBTTagCompound());
        return compound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound compound) {
        this.readFromNBT(compound);
    }
}
