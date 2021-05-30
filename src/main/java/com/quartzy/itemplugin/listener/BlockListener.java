package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlock;
import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftSound;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockListener implements Listener{
    
    @EventHandler
    public void blockPlaced(BlockPlaceEvent event){
        ItemStack itemInHand = event.getItemInHand();
        BlockManager blockManager = ItemPlugin.getBlockManager();
        CustomBlock blockFromItemStack = blockManager.getBlockFromItemStack(itemInHand);
    
        if(blockFromItemStack==null)return;
        
        event.getBlockPlaced().setType(blockFromItemStack.getBlockMaterial());
        blockManager.setBlock(event.getBlockPlaced().getLocation(), blockFromItemStack.getId());
    }
    
    //Not sure about it yet
    /*@EventHandler
    public void blockPlacedItem(PlayerInteractEvent event){
        if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
            if(event.getClickedBlock()==null)return;
            ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
            System.out.println(item.toString() + "   " + event.getHand());
            if(item.getType()==Material.AIR || item.getAmount()==0)return;
            BlockManager blockManager = ItemPlugin.getBlockManager();
            CustomBlock blockFromItemStack = blockManager.getBlockFromItemStack(item);
            
            if(blockFromItemStack==null)return;
    
            event.setCancelled(true);
            Block blockAt = event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getLocation().add(new Vector(event.getBlockFace().getModX(), event.getBlockFace().getModY(), event.getBlockFace().getModZ())));
            blockAt.setType(blockFromItemStack.getBlockMaterial());
            blockManager.setBlock(blockAt.getLocation(), blockFromItemStack.getId());
            CraftBlock block = (CraftBlock) blockAt;
            SoundEffectType stepSound = block.getNMS().getStepSound();
            SoundEffect e = stepSound.e();
            Location loc = blockAt.getLocation();
            PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(e, SoundCategory.BLOCKS, loc.getX(), loc.getY(), loc.getZ(), 1f, 1f);
            ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        }
    }*/
    
    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        BlockManager blockManager = ItemPlugin.getBlockManager();
        MinecraftKey block = blockManager.getBlock(event.getBlock().getLocation());
        
        if(block==null)return;
        
        blockManager.deleteBlock(event.getBlock().getLocation());
        
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)return;
        
        event.setDropItems(false);
        MinecraftKey blockItem = blockManager.getBlockById(block).getBlockItem();
        ItemManager itemManager = ItemPlugin.getItemManager();
        ItemStack item = itemManager.createItem(blockItem, 1);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getHand() != EquipmentSlot.HAND)return;
            BlockManager blockManager = ItemPlugin.getBlockManager();
            MinecraftKey blockId = blockManager.getBlock(event.getClickedBlock().getLocation());
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
