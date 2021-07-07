package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockManager{
    
    private HashMap<String, BlockWorldManager> blockManagers = new HashMap<>();
    private HashMap<MinecraftKey, CustomBlock> blocks = new HashMap<>();
    
    public CustomBlock getBlockFromItemStack(ItemStack itemStack){
        MinecraftKey itemId = ItemManager.getItemId(itemStack);
        return blocks.get(itemId);
    }
    
    public void addBlock(CustomBlock block){
        blocks.put(block.getId(), block);
    }
    
    public void addBlockTextured(CustomBlock block, boolean down, boolean east, boolean north, boolean south, boolean up, boolean west, boolean useMultipart, String model, String override_model){
        blocks.put(block.getId(), block);
        if(ItemPlugin.getResourcePack()!=null){
            ItemPlugin.getResourcePack().addBlockTexture(block.getId(), down, east, north, south, up, west, useMultipart, model, override_model);
        }
    }
    
    public void addBlockTextured(CustomBlock block, boolean down, boolean east, boolean north, boolean south, boolean up, boolean west, String model, String override_model){
        blocks.put(block.getId(), block);
        if(ItemPlugin.getResourcePack()!=null){
            ItemPlugin.getResourcePack().addBlockTexture(block.getId(), down, east, north, south, up, west, model, override_model);
        }
    }
    
    public void addBlockTextured(CustomBlock block, String model, String override_model){
        blocks.put(block.getId(), block);
        if(ItemPlugin.getResourcePack()!=null){
            ItemPlugin.getResourcePack().addBlockTexture(block.getId(), model, override_model);
        }
    }
    
    public CustomBlock getBlockById(MinecraftKey id){
        return blocks.get(id);
    }
    
    public CustomBlock getBlockById(String id){
        return blocks.get(new MinecraftKey(id));
    }
    
    public void setBlock(Location location, MinecraftKey data){
        World world = location.getWorld();
        if(world==null)return;
        if(!blockManagers.containsKey(world.getName())){
            loadData(world);
        }
        blockManagers.get(world.getName()).setBlock(location, data);
    }
    
    public void deleteBlock(Location location){
        World world = location.getWorld();
        if(world==null)return;
        if(!blockManagers.containsKey(world.getName()))return;
    
        blockManagers.get(world.getName()).deleteBlock(location);
    }
    
    public MinecraftKey getBlock(Location location){
        World world = location.getWorld();
        if(world==null)return null;
        BlockWorldManager blockWorldManager = blockManagers.get(world.getName());
        if(blockWorldManager==null)return null;
        return blockWorldManager.getBlock(location);
    }
    
    public void saveData(){
        for(BlockWorldManager value : blockManagers.values()){
            value.saveBlockData();
        }
    }
    
    public void loadData(World world){
        if(world==null)return;
        BlockWorldManager blockWorldManager = blockManagers.get(world.getName());
        if(blockWorldManager==null){
            blockWorldManager = new BlockWorldManager(world);
            blockManagers.put(world.getName(), blockWorldManager);
        }
        blockWorldManager.readBlockData();
    }
    
    public void clearBlocks(){
        blocks.clear();
    }
}
