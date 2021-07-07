package com.quartzy.itemplugin.commands;

import com.quartzy.itemplugin.ItemPlugin;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourcePackCommand extends QCommand{
    @Override
    void register(QLiteralArgumentBuilder<CommandListenerWrapper> argumentBuilder){
        argumentBuilder.requires(commandListenerWrapper -> commandListenerWrapper.hasPermission(2, "itemplugin.commands.resourcepack"))
            .executes(commandContext -> {
                try{
                    CommandSender bukkitSender = commandContext.getSource().getBukkitSender();
                    if(!(bukkitSender instanceof Player)){
                        bukkitSender.sendMessage(ChatColor.RED + "You must be a player to access this command");
                        return 1;
                    }
                    Player player = (Player) bukkitSender;
                    ItemPlugin.getResourcePack().sendToPlayer(player);
                    Block blockAt = player.getWorld().getBlockAt(player.getLocation());
                    blockAt.setType(Material.BROWN_MUSHROOM_BLOCK, false);
                    BlockData blockData = blockAt.getBlockData();
                    MultipleFacing multipleFacing = (MultipleFacing) blockData;
                    multipleFacing.setFace(BlockFace.UP, false);
                    multipleFacing.setFace(BlockFace.DOWN, false);
                    multipleFacing.setFace(BlockFace.NORTH, false);
                    multipleFacing.setFace(BlockFace.WEST, false);
                    multipleFacing.setFace(BlockFace.EAST, false);
                    multipleFacing.setFace(BlockFace.SOUTH, false);
                    blockAt.setBlockData(multipleFacing);
                }catch(Throwable e){
                    e.printStackTrace();
                }
                return 1;
            });
    }
    
    @Override
    String getName(){
        return "resourcepack";
    }
    
    @Override
    String[] getAliases(){
        return new String[0];
    }
}
