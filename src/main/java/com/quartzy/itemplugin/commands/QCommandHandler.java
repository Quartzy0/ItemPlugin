package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.v1_16_R3.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class QCommandHandler{
    private HashMap<String, QCommand> commands = new HashMap<>();
    
    private static String ensureLength(String name){
        StringBuilder nameBuilder = new StringBuilder(new MinecraftKey(name).toString());
        for(int i = nameBuilder.length(); i < 32; i++){
            nameBuilder.append("-");
        }
        return nameBuilder.toString();
    }
    
    public void addCommand(QCommand command){
        this.commands.put(command.getName(), command);
    }
    
    public void init(CommandDispatcher dispatcher){
        String var0 = ensureLength(ArgumentBetterItemStack.a().getSerializerId());
        System.out.println(var0 + " " + var0.length());
        ArgumentRegistry.a(var0, ArgumentBetterItemStack.class, new QPacketSerializer<>(ArgumentBetterItemStack::a));
    
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
