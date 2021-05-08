package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.items.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class CustomBlock{
    
    public abstract String getBlockItem();
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getId();
    public abstract Material getBlockMaterial();
    public abstract Rarity getBlockRarity();
    public abstract ActionType getActionType();
    
    public abstract void onAction(Player player, Location location);
}
