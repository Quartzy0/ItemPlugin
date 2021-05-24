package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.inv.InventoryHandler;
import com.quartzy.itemplugin.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;

public class InventoryListener implements Listener{
    
    @EventHandler
    public void inventoryItemClicked(InventoryClickEvent event){
        InventoryManager inventoryManager = ItemPlugin.getINSTANCE().getInventoryManager();
        HashMap<Player, InventoryHandler> inventories = inventoryManager.getInventories();
    
        InventoryHandler inventoryHandler = inventories.get((Player) event.getWhoClicked());
        if(inventoryHandler==null)return;
        boolean toCancel = inventoryHandler.shouldCancelClick(event.getRawSlot(), event.getAction(), event.getClick());
        Bukkit.getScheduler().runTaskLater(ItemPlugin.getINSTANCE(), new Runnable(){
            @Override
            public void run(){
                inventoryHandler.itemClickedPost(event.getRawSlot(), event.getAction(), event.getClick());
            }
        }, 0L);
        event.setCancelled(toCancel);
    }
    
    @EventHandler
    public void inventoryDrag(InventoryDragEvent event){
        InventoryManager inventoryManager = ItemPlugin.getINSTANCE().getInventoryManager();
        HashMap<Player, InventoryHandler> inventories = inventoryManager.getInventories();
    
        InventoryHandler inventoryHandler = inventories.get((Player) event.getWhoClicked());
        if(inventoryHandler==null)return;
    
        boolean toCancel = inventoryHandler.shouldCancelDrag(event.getNewItems(), event.getType());
        Bukkit.getScheduler().runTaskLater(ItemPlugin.getINSTANCE(), new Runnable(){
            @Override
            public void run(){
                inventoryHandler.inventoryDragPost(event.getNewItems(), event.getType());
            }
        }, 0L);
        event.setCancelled(toCancel);
    }
    
    @EventHandler
    public void inventoryClosed(InventoryCloseEvent event){
        InventoryManager inventoryManager = ItemPlugin.getINSTANCE().getInventoryManager();
        InventoryHandler inventoryHandler = inventoryManager.getInventories().get((Player) event.getPlayer());
        
        if(inventoryHandler==null)return;
    
        inventoryHandler.inventoryClose();
        inventoryManager.getInventories().remove((Player) event.getPlayer());
    }
}
