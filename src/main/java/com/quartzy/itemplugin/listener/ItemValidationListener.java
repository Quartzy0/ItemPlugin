package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.items.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemValidationListener implements Listener{
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickupItem(EntityPickupItemEvent event){
        ItemStack itemStack = event.getItem().getItemStack();
        ItemManager itemManager = ItemPlugin.getINSTANCE().getItemManager();
        event.getItem().setItemStack(itemManager.validateItem(itemStack));
    }
}
