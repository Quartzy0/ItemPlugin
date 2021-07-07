package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.ICompletionProvider;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class QSuggestionProvider implements SuggestionProvider<CommandListenerWrapper>{
    
    private final List<String> suggestionsS;
    private final List<Integer> suggestionsI;
    private final List<MinecraftKey> suggestionsM;
    private final CreateSuggestions createSuggestions;
    
    protected QSuggestionProvider(List<String> suggestionsS, List<Integer> suggestionsI, List<MinecraftKey> suggestionsM, CreateSuggestions createSuggestions){
        this.suggestionsS = suggestionsS;
        this.suggestionsI = suggestionsI;
        this.suggestionsM = suggestionsM;
        this.createSuggestions = createSuggestions;
    }
    
    public static QSuggestionProvider functional(CreateSuggestions createSuggestions){
        return new QSuggestionProvider(null, null, null, createSuggestions);
    }
    
    public static QSuggestionProvider keyed(MinecraftKey... keys){
        return new QSuggestionProvider(null, null, Arrays.asList(keys), null);
    }
    
    public static QSuggestionProvider string(String... suggestions){
        List<String> suggestionsS = Arrays.asList(suggestions);
        return new QSuggestionProvider(suggestionsS, null, null, null);
    }
    
    public static QSuggestionProvider integer(Integer... suggestions){
        List<Integer> suggestionsI = Arrays.asList(suggestions);
        return new QSuggestionProvider(null, suggestionsI, null, null);
    }
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandListenerWrapper> context, SuggestionsBuilder builder) throws CommandSyntaxException{
        int i = context.getInput().lastIndexOf(' ')+1;
        builder = builder.createOffset(i);
        String substring = context.getInput().substring(i);
        if(suggestionsS==null && suggestionsM==null && suggestionsI==null && createSuggestions!=null){
            List<Object> objs = createSuggestions.create(context);
            if(!objs.isEmpty() && objs.get(0) instanceof MinecraftKey){
                return QCompletionProvider.generateSuggestions((List<MinecraftKey>) (Object) objs, builder);
            }
            List<String> strings = new ArrayList<>(objs.size());
            for(int i1 = 0; i1 < objs.size(); i1++){
                Object o = objs.get(i1);
                if(o==null)continue;
                strings.add(o.toString());
            }
            sort(substring, strings);
            for(String string : strings){
                builder.suggest(string);
            }
            return builder.buildFuture();
        }
        
        if(suggestionsM!=null){
            return ICompletionProvider.a(suggestionsM, builder);
        }
    
        List<String> sortedSuggestions = sort(substring, suggestionsS);
        
        for(String s : sortedSuggestions){
            builder.suggest(s);
        }
        if(suggestionsI!=null){
            for(Integer integer : suggestionsI){
                builder.suggest(integer);
            }
        }
        
        return builder.buildFuture();
    }
    
    //https://www.spigotmc.org/threads/command-tab-completion.442470/#post-3831527
    public List<String> sort(String arg, List<String> in){
        if(in==null || arg==null) return new ArrayList<>();
        if(in.isEmpty())return in;
        final List < String > completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, in, completions);
        Collections.sort(completions);
        return completions;
    }
    
    @FunctionalInterface
    public static interface CreateSuggestions<T>{
        List<T> create(CommandContext<CommandListenerWrapper> context);
    }
}
