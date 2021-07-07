package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlock;
import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveItemCommand extends QCommand{
    
    @Override
    public void register(QLiteralArgumentBuilder<CommandListenerWrapper> literal){
        literal.requires((commandListenerWrapper) -> (
                commandListenerWrapper.hasPermission(2, "minecraft.command.give")
        )).then(argument("target", ArgumentEntity.d())
                .then(argument("item", ArgumentItemStack.a()).executes(commandContext -> {
                    ArgumentPredicateItemStack itemStr = commandContext.getArgument("item", ArgumentPredicateItemStack.class);
                    EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
                    int amount = 1;
                    return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
                }).then(argument("count", IntegerArgumentType.integer(1)).executes(commandContext -> {
                    ArgumentPredicateItemStack itemStr = commandContext.getArgument("item", ArgumentPredicateItemStack.class);
                    EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
                    int amount = commandContext.getArgument("count", Integer.class);
                    return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
                })))
                .then(argument("itemCustom", ArgumentBetterItemStack.a(), context -> ItemPlugin.getItemManager().getAllItems()).executes(commandContext -> {
            ArgumentPredicateItemStack itemStr = commandContext.getArgument("itemCustom", ArgumentPredicateItemStack.class);
            EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
            int amount = 1;
            return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
        }).then(argument("count", IntegerArgumentType.integer(1)).executes(commandContext -> {
            ArgumentPredicateItemStack itemStr = commandContext.getArgument("itemCustom", ArgumentPredicateItemStack.class);
            EntitySelector player = commandContext.getArgument("target", EntitySelector.class);
            int amount = commandContext.getArgument("count", Integer.class);
            return execute(itemStr, player.getEntities(commandContext.getSource()), amount);
        }))));
    }
    
    public int execute(ArgumentPredicateItemStack itemStr, List<? extends Entity> selector, int amount) throws CommandSyntaxException{
        ItemStack item = CraftItemStack.asBukkitCopy(itemStr.a(amount, false));
        if(ItemPlugin.isItemsAvailable()){
            ItemPlugin.getItemManager().validateItem(item);
        }
        for(Entity entity : selector){
            if(entity.getBukkitEntity() instanceof CraftPlayer){
                ((CraftPlayer) entity.getBukkitEntity()).getInventory().addItem(item);
            }
        }
        return 1;
    }
    
    @Override
    public String getName(){
        return "give";
    }
    
    @Override
    String[] getAliases(){
        return new String[]{"gi"};
    }
}
