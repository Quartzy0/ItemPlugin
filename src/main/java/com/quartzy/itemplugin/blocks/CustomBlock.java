package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.items.Rarity;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class CustomBlock{
    
    public abstract MinecraftKey getBlockItem();
    public abstract String getName();
    public abstract String getDescription();
    public abstract MinecraftKey getId();
    public abstract Material getBlockMaterial();
    public abstract Rarity getBlockRarity();
    public abstract ActionType getActionType();
    
    public abstract void onAction(Player player, Location location);
}
