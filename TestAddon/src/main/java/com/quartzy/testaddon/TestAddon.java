package com.quartzy.testaddon;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.ItemPluginHandler;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlockImpl;
import com.quartzy.itemplugin.items.CustomItem;
import com.quartzy.itemplugin.items.ItemManager;
import com.quartzy.itemplugin.items.Rarity;
import net.minecraft.server.v1_16_R3.MinecraftKey;
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
        System.out.println("Called items");
        itemManager.addItem(new CustomItem(Material.OAK_BOAT, "Le epic boat", Rarity.SUPER_SUPREME, "Le boat de epico", null, new MinecraftKey("testplugin:boat_special")));
        itemManager.addItem(new CustomItem(Material.SLIME_BALL, "LE epic slime", Rarity.MYTHIC, "no", null, new MinecraftKey("testplugin:slime_special")));
    }
    
    @Override
    public void addBlocks(BlockManager blockManager){
        System.out.println("Called block");
        blockManager.addBlock(new CustomBlockImpl(new MinecraftKey("testplugin:slime_special"), "Special slime block", "no", new MinecraftKey("testplugin:slime_block_special"), Material.SLIME_BLOCK, Rarity.COMMON, ActionType.RIGHT_CLICK, "say hi", false));
    }
    
    @Override
    public void addRecipes(){
        System.out.println("Called recipes");
    }
}
