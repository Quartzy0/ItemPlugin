package com.quartzy.itemplugin.inv;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryManager{
    @Getter
    private HashMap<Player, InventoryHandler> inventories = new HashMap<>();
    
    public void openInventory(InventoryHandler inventoryHandler, Player player){
        inventories.put(player, inventoryHandler);
        Inventory inventory = inventoryHandler._inventoryOpen(player);
        player.openInventory(inventory);
    }
}
