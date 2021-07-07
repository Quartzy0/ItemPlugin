package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.v1_16_R3.ICompletionProvider;
import net.minecraft.server.v1_16_R3.MinecraftKey;

import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class QCompletionProvider{
    
    static CompletableFuture<Suggestions> generateSuggestions(Iterable<MinecraftKey> var0, SuggestionsBuilder var1) {
        String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
        a(var0, var2, (var0x) -> var0x, (var1x) -> var1.suggest(var1x.toString()));
        return var1.buildFuture();
    }
    
    static <T> void a(Iterable<T> var0, String var1, Function<T, MinecraftKey> var2, Consumer<T> var3) {
        boolean var4 = var1.indexOf(':') > -1;
    
        for(T var6 : var0){
            MinecraftKey var7 = var2.apply(var6);
            if(var4){
                String var8 = var7.toString();
                if(ICompletionProvider.a(var1, var8)){
                    var3.accept(var6);
                }
            } else if(ICompletionProvider.a(var1, var7.getNamespace()) || ICompletionProvider.a(var1, var7.getKey())){
                var3.accept(var6);
            }
        }
    }
}
