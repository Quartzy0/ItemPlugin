package com.quartzy.itemplugin.commands;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.util.RecipeHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RefreshCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if(args.length==0){
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading items...");
            long startItems = System.currentTimeMillis();
            ItemPlugin.getINSTANCE().reloadItems();
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading items (" + (System.currentTimeMillis()-startItems) + " ms)");
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading blocks...");
            long startBlocks = System.currentTimeMillis();
            ItemPlugin.getINSTANCE().reloadBlocks();
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading blocks (" + (System.currentTimeMillis()-startBlocks) + " ms)");
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading recipes...");
            long startRecipes = System.currentTimeMillis();
            RecipeHelper.init();
            sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading recipes (" + (System.currentTimeMillis()-startRecipes) + " ms)");
        }
        if(args.length==1){
            switch(args[0]){
                case "items":
                case "item":
                case "i":
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading items...");
                    long startItems = System.currentTimeMillis();
                    ItemPlugin.getINSTANCE().reloadItems();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading items (" + (System.currentTimeMillis()-startItems) + " ms)");
                    break;
                case "blocks":
                case "block":
                case "b":
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading blocks...");
                    long startBlocks = System.currentTimeMillis();
                    ItemPlugin.getINSTANCE().reloadBlocks();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading blocks (" + (System.currentTimeMillis()-startBlocks) + " ms)");
                    break;
                case "recipe":
                case "recipes":
                case "r":
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading recipes...");
                    long startRecipes = System.currentTimeMillis();
                    RecipeHelper.init();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading recipes (" + (System.currentTimeMillis()-startRecipes) + " ms)");
                    break;
                case "all":
                case "a":
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading items...");
                    long startItems1 = System.currentTimeMillis();
                    ItemPlugin.getINSTANCE().reloadItems();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading items (" + (System.currentTimeMillis()-startItems1) + " ms)");
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading blocks...");
                    long startBlocks1 = System.currentTimeMillis();
                    ItemPlugin.getINSTANCE().reloadBlocks();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading blocks (" + (System.currentTimeMillis()-startBlocks1) + " ms)");
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading recipes...");
                    long startRecipes1 = System.currentTimeMillis();
                    RecipeHelper.init();
                    sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading recipes (" + (System.currentTimeMillis()-startRecipes1) + " ms)");
                default:
                    return false;
            }
        }
        return args.length<=1;
    }
}
