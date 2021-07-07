package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.items.Rarity;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestCustomBlock extends CustomBlock{
    @Override
    public MinecraftKey getBlockItem(){
        return new MinecraftKey("itemplugin:test_block");
    }
    
    @Override
    public String getName(){
        return "Test block";
    }
    
    @Override
    public String getDescription(){
        return "A block that tests things out";
    }
    
    @Override
    public MinecraftKey getId(){
        return new MinecraftKey("itemplugin:test_block");
    }
    
    @Override
    public Material getBlockMaterial(){
        return Material.BONE_BLOCK;
    }
    
    @Override
    public Rarity getBlockRarity(){
        return Rarity.SUPER_SUPREME;
    }
    
    @Override
    public ActionType getActionType(){
        return ActionType.RIGHT_CLICK;
    }
    
    @Override
    public void onAction(Player player, Location location){
        player.sendMessage("Yooooo, nibba");
    }
}
