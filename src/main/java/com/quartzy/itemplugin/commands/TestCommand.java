package com.quartzy.itemplugin.commands;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if(sender instanceof Player){
            BlockManager blockManager = ItemPlugin.getINSTANCE().getBlockManager();
            if(args[0].equals("s")){
                blockManager.saveData();
            }else{
                blockManager.loadData(((Player) sender).getWorld());
            }
        }
        return true;
    }
}
