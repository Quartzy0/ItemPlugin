package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ActionType;
import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.inv.InventoryHandlerWorkbench;
import com.quartzy.itemplugin.items.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockWorkbench extends CustomBlock{
    
    @Override
    public String getBlockItem(){
        return "WORKBENCH_ITEM";
    }
    
    @Override
    public String getName(){
        return "Workbench";
    }
    
    @Override
    public String getDescription(){
        return "Craft in a better way";
    }
    
    @Override
    public String getId(){
        return "WORKBENCH";
    }
    
    @Override
    public Material getBlockMaterial(){
        return Material.CRAFTING_TABLE;
    }
    
    @Override
    public Rarity getBlockRarity(){
        return Rarity.UNCOMMON;
    }
    
    @Override
    public ActionType getActionType(){
        return ActionType.RIGHT_CLICK;
    }
    
    @Override
    public void onAction(Player player, Location location){
        ItemPlugin.getINSTANCE().getInventoryManager().openInventory(new InventoryHandlerWorkbench(), player);
    }
}
