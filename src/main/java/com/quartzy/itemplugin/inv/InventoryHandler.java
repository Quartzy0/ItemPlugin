package com.quartzy.itemplugin.inv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryHandler{
    private HashMap<Byte, Inventory> inventories = new HashMap<>();
    private byte inventory;
    private Player player;
    
    public abstract Inventory inventoryOpen();
    public abstract void itemClickedPost(int slot, InventoryAction action, ClickType clickType);
    public abstract boolean shouldCancelClick(int slot, InventoryAction action, ClickType clickType);
    
    public abstract boolean shouldCancelDrag(Map<Integer, ItemStack> slots, DragType type);
    public abstract void inventoryDragPost(Map<Integer, ItemStack> slots, DragType type);
    
    public abstract void inventoryClose();
    
    final ItemStack noName(Material mat){
        ItemStack itemStack = new ItemStack(mat);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLocalizedName(" ");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    final ItemStack noName(){
        return noName(Material.BLACK_STAINED_GLASS_PANE);
    }
    
    final boolean isInsideInventory(int slot){
        return inventory().getSize() > slot;
    }
    
    final Inventory _inventoryOpen(Player player){
        this.player = player;
        Inventory value = inventoryOpen();
        inventories.put((byte) 0, value);
        inventory = 0;
        return value;
    }
    
    public final Inventory inventory(){
        return inventories.get(inventory);
    }
    
    protected final byte inventoryId(){
        return inventory;
    }
    
    protected final void addInventory(Inventory inventory, byte id){
        inventories.put(id, inventory);
    }
    
    protected final void openInv(byte id){
        inventory = id;
        player.openInventory(inventory());
    }
    
    public final Player getPlayer(){
        return player;
    }
    
    protected final void fillInventory(Material material, Inventory inv){
        fillInventory(new ItemStack(material, 1), inv);
    }
    
    protected final void fillInventory(ItemStack item, Inventory inv){
        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, item);
        }
    }
    
    protected final Inventory createInventory(int slots){
        return Bukkit.createInventory(null, slots);
    }
    
    protected final Inventory createInventory(int slots, String title){
        return Bukkit.createInventory(null, slots, title);
    }
    
    protected final Inventory createInventory(String title, InventoryType type){
        return Bukkit.createInventory(null, type, title);
    }
}
