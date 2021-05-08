package com.quartzy.itemplugin.abilities;

import com.quartzy.itemplugin.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class Ability{
    public abstract String getDisplayName();
    public abstract String getId();
    public abstract String getDescription();
    public abstract ActionType getAction();
    
    public abstract void onAction(ItemStack itemUsed, Player player);
}
