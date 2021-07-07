package com.quartzy.itemplugin.commands;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.util.RecipeHelper;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Master command containing many useful sub command
 */
public class ItemPluginCommand extends QCommand{
    @Override
    void register(QLiteralArgumentBuilder<CommandListenerWrapper> argumentBuilder){
        //Reload command
        argumentBuilder.then(argument("reload")
                .requires(commandListenerWrapper -> commandListenerWrapper.hasPermission(3, "itemplugin.command.ipl.reload"))
                .executes(commandContext -> {
                    reloadItems(commandContext.getSource().getBukkitSender());
                    reloadBlocks(commandContext.getSource().getBukkitSender());
                    reloadRecipes(commandContext.getSource().getBukkitSender());
                    return 1;
                })
                .then(argument("items").executes(commandContext -> reloadItems(commandContext.getSource().getBukkitSender())))
                .then(argument("blocks").executes(commandContext -> reloadBlocks(commandContext.getSource().getBukkitSender())))
                .then(argument("recipes").executes(commandContext -> reloadRecipes(commandContext.getSource().getBukkitSender())))
                .then(argument("all").executes(commandContext -> {
                    reloadItems(commandContext.getSource().getBukkitSender());
                    reloadBlocks(commandContext.getSource().getBukkitSender());
                    reloadRecipes(commandContext.getSource().getBukkitSender());
                    return 1;
                }))
        );
        //Resource pack
        argumentBuilder.then(argument("resourcepack")
                .requires(commandListenerWrapper -> commandListenerWrapper.hasPermission(3, "itemplugin.command.ipl.resourcepack"))
                .then(argument("resend").executes(commandContext -> {
                    if(ItemPlugin.getResourcePack()==null){
                        commandContext.getSource().sendFailureMessage(new ChatMessage("Resource pack is not enabled"));
                        return 1;
                    }
                    if(commandContext.getSource().getBukkitSender() instanceof Player){
                        ItemPlugin.getResourcePack().sendToPlayer((Player) commandContext.getSource().getBukkitSender());
                    }else{
                        commandContext.getSource().sendFailureMessage(new ChatMessage("You must be a player to use this command or specify a player"));
                    }
                    return 1;
                }).then(argument("target", ArgumentEntity.a()).executes(commandContext -> {
                    if(ItemPlugin.getResourcePack()==null){
                        commandContext.getSource().sendFailureMessage(new ChatMessage("Resource pack is not enabled"));
                        return 1;
                    }
                    EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
                    List<? extends Entity> entities = player.getEntities(commandContext.getSource());
                    for(Entity entity : entities){
                        if(entity instanceof EntityPlayer){
                            CraftEntity entity1 = CraftPlayer.getEntity(((CraftServer) Bukkit.getServer()), entity);
                            if(entity1 instanceof CraftPlayer){
                                ItemPlugin.getResourcePack().sendToPlayer((Player) entity1);
                            }else{
                                commandContext.getSource().sendFailureMessage(new ChatMessage("Entity must be a player"));
                            }
                        }else{
                            commandContext.getSource().sendFailureMessage(new ChatMessage("Entity must be a player"));
                        }
                    }
                    return 1;
                }))).then(argument("regenerate").executes(commandContext -> {
                    if(ItemPlugin.getResourcePack()==null){
                        commandContext.getSource().sendFailureMessage(new ChatMessage("Resource pack is not enabled"));
                        return 1;
                    }
                    ItemPlugin.getResourcePack().regenerate();
                    return 1;
                }))
        );
    }
    
    public int reloadItems(CommandSender sender){
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading items...");
        long startItems = System.currentTimeMillis();
        ItemPlugin.getINSTANCE().reloadItems();
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading items (" + (System.currentTimeMillis()-startItems) + " ms)");
        return 1;
    }
    
    public int reloadBlocks(CommandSender sender){
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading blocks...");
        long startBlocks = System.currentTimeMillis();
        ItemPlugin.getINSTANCE().reloadBlocks();
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading blocks (" + (System.currentTimeMillis()-startBlocks) + " ms)");
        return 1;
    }
    
    public int reloadRecipes(CommandSender sender){
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Reloading recipes...");
        long startRecipes = System.currentTimeMillis();
        RecipeHelper.init();
        sender.sendMessage(ChatColor.AQUA + "[ItemPlugin] Done reloading recipes (" + (System.currentTimeMillis()-startRecipes) + " ms)");
        return 1;
    }
    
    @Override
    String getName(){
        return "itemplugin";
    }
    
    @Override
    String[] getAliases(){
        return new String[]{"ipl"};
    }
}
