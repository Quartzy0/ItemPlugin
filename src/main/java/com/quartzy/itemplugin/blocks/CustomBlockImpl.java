package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.items.Rarity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * An implementation of the CustomBlock interface to allow users to add their own blocks from a config file.
 * It has the option to execute the command once a player clicks on it.
 */
public final class CustomBlockImpl extends CustomBlock{
    private final String blockItem, name, description, id;
    private final Material material;
    private final Rarity rarity;
    private final ActionType actionType;
    private final String commandToExecute;
    private final boolean asPlayer;
    
    public CustomBlockImpl(String blockItem, String name, String description, String id, Material material, Rarity rarity, ActionType actionType, String commandToExecute, boolean asPlayer){
        this.blockItem = blockItem;
        this.name = name;
        this.description = description;
        this.id = id;
        this.material = material;
        this.rarity = rarity;
        this.actionType = actionType;
        this.commandToExecute = commandToExecute;
        this.asPlayer = asPlayer;
    }
    
    
    /**
     * Create block with no action
     */
    public CustomBlockImpl(String blockItem, String name, String description, String id, Material material, Rarity rarity){
        this(blockItem, name, description, id, material, rarity, null, null, false);
    }
    
    @Override
    public String getBlockItem(){
        return this.blockItem;
    }
    
    @Override
    public String getName(){
        return this.name;
    }
    
    @Override
    public String getDescription(){
        return this.description;
    }
    
    @Override
    public String getId(){
        return this.id;
    }
    
    @Override
    public Material getBlockMaterial(){
        return this.material;
    }
    
    @Override
    public Rarity getBlockRarity(){
        return this.rarity;
    }
    
    @Override
    public ActionType getActionType(){
        return this.actionType;
    }
    
    @Override
    public void onAction(Player player, Location location){
        if(this.commandToExecute==null)return;
        if(asPlayer){
            player.performCommand(this.commandToExecute);
        }else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.commandToExecute);
        }
    }
}
