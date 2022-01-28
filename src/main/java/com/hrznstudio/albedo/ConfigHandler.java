package com.hrznstudio.albedo;

import com.hrznstudio.albedo.lighting.LightColor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;

import static com.hrznstudio.albedo.ConfigManager.*;

public class ConfigHandler {
    //Store them here to avoid Side error
    public static final LinkedHashMap<Item, LightColor> torchLight = new LinkedHashMap<Item, LightColor>();
    public static final LinkedHashMap<Item, LightColor> constantLight = new LinkedHashMap<Item, LightColor>();
    public static final LinkedHashMap<Block, LightColor> blockLight = new LinkedHashMap<Block, LightColor>();
    public static final LinkedHashMap<String, LightColor> teLight = new LinkedHashMap<String, LightColor>();
    public static void reloadList() {
        String[] e = {""};
        Item temp;
        if(torchList.length > 0) {
            for(String s: torchList) {
                e = s.split("\\|");
                temp = Item.getByNameOrId(e[0]);
                if(temp != null)
                    torchLight.put(temp, new LightColor(new Float(e[1]), new Float(e[2]), new Float(e[3]), new Float(e[4]), new Float(e[5])));
            }

        }

        if(constantList.length > 0) {
            for(String s: constantList) {
                e = s.split("\\|");
                temp = Item.getByNameOrId(e[0]);
                if(temp != null)
                    constantLight.put(temp, new LightColor(new Float(e[1]), new Float(e[2]), new Float(e[3]), new Float(e[4]), new Float(e[5])));
            }
        }
        Block tempblock;
        if(blockList.length > 0) {
            for(String s: blockList) {
                e = s.split("\\|");
                tempblock = Block.getBlockFromName(e[0]);
                if(tempblock != null)
                    blockLight.put(tempblock, new LightColor(new Float(e[1]), new Float(e[2]), new Float(e[3]), new Float(e[4]), new Float(e[5])));
            }
        }
        if(teList.length > 0) {
            for(String s: teList) {
                e = s.split("\\|");
                if(e != null)
                    teLight.put(e[0], new LightColor(new Float(e[1]), new Float(e[2]), new Float(e[3]), new Float(e[4]), new Float(e[5])));
            }
        }


    }
}
