package com.quartzy.itemplugin.commands;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlock;
import com.quartzy.itemplugin.items.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if(sender instanceof Player){
            ItemManager itemManager = ItemPlugin.getItemManager();
            ItemStack item = itemManager.createItem(args[0], 1);
            if(item!=null){
                ((Player) sender).getInventory().addItem(item);
            }else{
                BlockManager blockManager = ItemPlugin.getBlockManager();
                CustomBlock blockById = blockManager.getBlockById(args[0]);
                if(blockById!=null){
                    ItemStack item1 = itemManager.createItem(blockById.getBlockItem(), 1);
                    ((Player) sender).getInventory().addItem(item1);
                }
            }
        }
        return true;
    }
}
