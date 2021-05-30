package com.quartzy.itemplugin.abilities;

import com.quartzy.itemplugin.ActionType;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class TestAbility extends Ability{
    private float range;
    
    public TestAbility(float range){
        this.range = range;
    }
    
    @Override
    public String getDisplayName(){
        return "Test ability";
    }
    
    @Override
    public MinecraftKey getId(){
        return new MinecraftKey("itemplugin:ability_test");
    }
    
    @Override
    public String getDescription(){
        return "Tests out the ability functionality";
    }
    
    @Override
    public ActionType getAction(){
        return ActionType.RIGHT_CLICK;
    }
    
    @Override
    public void onAction(ItemStack itemUsed, Player player){
        Vector lookVector = player.getLocation().getDirection().normalize();
        List<Block> targetBlock = player.getLineOfSight(null, (int) Math.ceil(range));
        for(int i = targetBlock.size()-1; i > -1; i--){
            Block block = targetBlock.get(i);
            if(block.getType().isSolid()){
                Location add = player.getLocation().add(lookVector.multiply(i));
                add.add(0, 1, 0);
                player.teleport(add);
                return;
            }
        }
        Location add = player.getLocation().add(lookVector.multiply(5));
        add.add(0, 1, 0);
        player.teleport(add);
    }
}
