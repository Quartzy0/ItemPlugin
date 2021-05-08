package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlock;
import com.quartzy.itemplugin.items.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener{
    
    @EventHandler
    public void blockPlaced(BlockPlaceEvent event){
        ItemStack itemInHand = event.getItemInHand();
        BlockManager blockManager = ItemPlugin.getINSTANCE().getBlockManager();
        CustomBlock blockFromItemStack = blockManager.getBlockFromItemStack(itemInHand);
    
        if(blockFromItemStack==null)return;
    
        blockManager.setBlock(event.getBlockPlaced().getLocation(), blockFromItemStack.getId());
    }
    
    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        BlockManager blockManager = ItemPlugin.getINSTANCE().getBlockManager();
        String block = blockManager.getBlock(event.getBlock().getLocation());
        
        if(block==null)return;
        
        blockManager.deleteBlock(event.getBlock().getLocation());
        
        event.setDropItems(false);
        String blockItem = blockManager.getBlockById(block).getBlockItem();
        ItemManager itemManager = ItemPlugin.getINSTANCE().getItemManager();
        ItemStack item = itemManager.createItem(blockItem, 1);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            BlockManager blockManager = ItemPlugin.getINSTANCE().getBlockManager();
            String blockId = blockManager.getBlock(event.getClickedBlock().getLocation());
            if(blockId==null)return;
            CustomBlock block = blockManager.getBlockById(blockId);
            if(block==null)return;
            if(block.getActionType().isEquivalent(event.getAction())){
                block.onAction(event.getPlayer(), event.getClickedBlock().getLocation());
                event.setCancelled(true);
            }
        }
    }
}
