package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.inv.InventoryHandler;
import com.quartzy.itemplugin.inv.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class InventoryListener implements Listener{
    
    @EventHandler
    public void inventoryItemClicked(InventoryClickEvent event){
        InventoryManager inventoryManager = ItemPlugin.getINSTANCE().getInventoryManager();
        HashMap<Player, InventoryHandler> inventories = inventoryManager.getInventories();
    
        InventoryHandler inventoryHandler = inventories.get((Player) event.getWhoClicked());
        if(inventoryHandler==null)return;
        event.setCancelled(inventoryHandler.itemClicked(event.getRawSlot(), event.getAction(), event.getClick()));
    }
    
    @EventHandler
    public void inventoryClosed(InventoryCloseEvent event){
        InventoryManager inventoryManager = ItemPlugin.getINSTANCE().getInventoryManager();
        inventoryManager.getInventories().remove((Player) event.getPlayer());
    }
}
