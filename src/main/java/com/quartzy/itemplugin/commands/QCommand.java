package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.MinecraftKey;

import java.util.List;

public abstract class QCommand{
    abstract void register(QLiteralArgumentBuilder<CommandListenerWrapper> argumentBuilder);
    
    abstract String getName();
    
    abstract String[] getAliases();
    
    protected final QLiteralArgumentBuilder<CommandListenerWrapper> argument(String s) {
        return QLiteralArgumentBuilder.literal(s);
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype) {
        return RequiredArgumentBuilder.argument(s, argumenttype);
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype, QSuggestionProvider.CreateSuggestions suggestions) {
        return RequiredArgumentBuilder.argument(s, argumenttype).suggests(((SuggestionProvider) QSuggestionProvider.functional(suggestions)));
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype, MinecraftKey... keys) {
        return RequiredArgumentBuilder.argument(s, argumenttype).suggests(((SuggestionProvider) QSuggestionProvider.keyed(keys)));
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype, String... suggestions) {
        return RequiredArgumentBuilder.argument(s, argumenttype).suggests(((SuggestionProvider) QSuggestionProvider.string(suggestions)));
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype, List<String> suggestions) {
        return argument(s, argumenttype, suggestions.toArray(new String[0]));
    }
    
    protected final <T> RequiredArgumentBuilder<CommandListenerWrapper, T> argument(String s, ArgumentType<T> argumenttype, Integer... suggestions) {
        return RequiredArgumentBuilder.argument(s, argumenttype).suggests(((SuggestionProvider) QSuggestionProvider.integer(suggestions)));
    }
}
