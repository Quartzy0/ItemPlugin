package com.quartzy.itemplugin.blocks;

import com.quartzy.itemplugin.ItemPlugin;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockManager{
    
    private HashMap<String, BlockWorldManager> blockManagers = new HashMap<>();
    private HashMap<String, CustomBlock> blocks = new HashMap<>();
    
    public BlockManager(){
        blocks.put("WORKBENCH", new BlockWorkbench());
    }
    
    public CustomBlock getBlockFromItemStack(ItemStack itemStack){
        if(!ItemPlugin.getINSTANCE().getItemManager().isItemValid(itemStack))return null;
    
        NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        String internalID = tag.getString("internalId");
        for(CustomBlock value : blocks.values()){
            if(value.getBlockItem().equals(internalID)){
                return value;
            }
        }
        return null;
    }
    
    public CustomBlock getBlockById(String id){
        return blocks.get(id);
    }
    
    public void setBlock(Location location, String data){
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
    
    public String getBlock(Location location){
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
}