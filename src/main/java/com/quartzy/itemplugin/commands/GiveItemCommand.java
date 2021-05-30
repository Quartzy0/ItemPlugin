package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlock;
import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveItemCommand extends QCommand{
    
    @Override
    public void register(QLiteralArgumentBuilder<CommandListenerWrapper> literal){
        literal.requires((commandListenerWrapper) -> (
                commandListenerWrapper.hasPermission(2, "itemplugin.command.giveitem")
        )).then(argument("target", ArgumentEntity.d()).then(argument("item", ArgumentMinecraftKeyRegistered.a(), context -> ItemPlugin.getItemManager().getAllItemsS()).executes(commandContext -> {
            MinecraftKey itemStr = commandContext.getArgument("item", MinecraftKey.class);
            EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
            int amount = 1;
            return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
        }).then(argument("count", IntegerArgumentType.integer(1)).executes(commandContext -> {
            MinecraftKey itemStr = commandContext.getArgument("item", MinecraftKey.class);
            EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
            int amount = commandContext.getArgument("count", Integer.class);
            return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
        }))));
    }
    
    public int execute(MinecraftKey itemStr, List<? extends Entity> selector, int amount){
        for(Entity entity : selector){
            if(entity.getBukkitEntity() instanceof CraftPlayer){
                execute(itemStr, ((CraftPlayer) entity.getBukkitEntity()), amount);
            }
        }
        return 1;
    }
    
    public int execute(MinecraftKey itemStr, CraftPlayer player, int amount){
        ItemManager itemManager = ItemPlugin.getItemManager();
        ItemStack item = itemManager.createItem(itemStr, amount);
        if(item!=null){
            player.getInventory().addItem(item);
        }else{
            BlockManager blockManager = ItemPlugin.getBlockManager();
            CustomBlock blockById = blockManager.getBlockById(itemStr);
            if(blockById != null){
                ItemStack item1 = itemManager.createItem(blockById.getBlockItem(), amount);
                if(item1 == null) return 1;
                player.getInventory().addItem(item1);
            } else{
                player.sendMessage(ChatColor.RED + "An item with id " + itemStr + " was not found");
            }
        }
        return 1;
    }
    
    @Override
    public String getName(){
        return "givei";
    }
    
    @Override
    String[] getAliases(){
        return new String[]{"gi"};
    }
}
