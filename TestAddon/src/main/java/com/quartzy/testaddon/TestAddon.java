package com.quartzy.testaddon;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.ItemPluginHandler;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlockImpl;
import com.quartzy.itemplugin.items.CustomItem;
import com.quartzy.itemplugin.items.ItemManager;
import com.quartzy.itemplugin.items.Rarity;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestAddon extends JavaPlugin implements ItemPluginHandler{
    
    @Override
    public void onEnable(){
        // Plugin startup logic
        ItemPlugin.addHandler(this);
    }
    
    @Override
    public void onDisable(){
        // Plugin shutdown logic
    }
    
    @Override
    public void addItems(ItemManager itemManager){
        itemManager.addItem(new CustomItem(Material.OAK_BOAT, "Le epic boat", Rarity.SUPER_SUPREME, "Le boat de epico", null, "BOAT_SPECIAL"));
        itemManager.addItem(new CustomItem(Material.SLIME_BALL, "LE epic slime", Rarity.MYTHIC, "no", null, "SLIME_SPECIAL"));
    }
    
    @Override
    public void addBlocks(BlockManager blockManager){
        blockManager.addBlock(new CustomBlockImpl("SLIME_SPECIAL", "Special slime block", "no", "SLIME_SPECIAL_BLOCK", Material.SLIME_BLOCK, Rarity.COMMON, ActionType.RIGHT_CLICK, "say hi", false));
    }
    
    @Override
    public void addRecipes(){
    
    }
}
