package com.quartzy.itemplugin.blocks;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class BlockWorldManager{
    private World world;
    
    private HashMap<BlockPos, String> blockTypes = new HashMap<>();
    
    public BlockWorldManager(World world){
        this.world = world;
    }
    
    private File getDataFile(){
        File item_plugin = world.getWorldFolder().toPath().resolve("item_plugin").toFile();
        if(!item_plugin.exists())item_plugin.mkdirs();
        File file = item_plugin.toPath().resolve("blocks.qdat").toFile();
        if(!file.exists()){
            try{
                file.createNewFile();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return file;
    }
    
    public String getBlock(int x, int y, int z){
        return blockTypes.get(new BlockPos(x, y, z));
    }
    
    public String getBlock(Location location){
        return blockTypes.get(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
    
    public void setBlock(int x, int y, int z, String data){
        blockTypes.put(new BlockPos(x, y, z), data);
    }
    
    public void setBlock(Location location, String data){
        blockTypes.put(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), data);
    }
    
    public void readBlockData(){
        try{
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(getDataFile()));
            blockTypes.clear();
            try{
                while(true){
                    long l = dataInputStream.readLong();
                    String data = dataInputStream.readUTF();
                    BlockPos blockPos = BlockPos.fromLong(l);
                    blockTypes.put(blockPos, data);
                    System.out.println("Block " + data + " at " + blockPos);
                }
            }catch(EOFException e){
                return; //end of file was reached, ignore
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
    
    public void saveBlockData(){
        try{
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(getDataFile(), false));
            for(Map.Entry<BlockPos, String> entry : blockTypes.entrySet()){
                outputStream.writeLong(entry.getKey().toLong());
                outputStream.writeUTF(entry.getValue());
            }
            outputStream.flush();
            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void deleteBlock(Location location){
        blockTypes.remove(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}
