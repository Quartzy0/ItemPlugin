package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.v1_16_R3.CommandDispatcher;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;

import java.util.HashMap;

public class QCommandHandler{
    private HashMap<String, QCommand> commands = new HashMap<>();
    
    public void addCommand(QCommand command){
        this.commands.put(command.getName(), command);
    }
    
    public void init(CommandDispatcher dispatcher){
        for(QCommand value : commands.values()){
            if(dispatcher.a().getRoot().getChild(value.getName())!=null){
                dispatcher.a().getRoot().removeCommand(value.getName());
            }
            QLiteralArgumentBuilder<CommandListenerWrapper> argumentBuilder = QLiteralArgumentBuilder.literal(value.getName());
            value.register(argumentBuilder);
            LiteralCommandNode<CommandListenerWrapper> register = dispatcher.a().register(argumentBuilder);
            for(String alias : value.getAliases()){
                QLiteralArgumentBuilder<CommandListenerWrapper> argumentBuilderAlias = QLiteralArgumentBuilder.literal(alias);
                argumentBuilderAlias.requires(argumentBuilder.getRequirement()).redirect(register);
                dispatcher.a().register(argumentBuilderAlias);
            }
        }
    }
}
