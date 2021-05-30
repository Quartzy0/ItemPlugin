package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QSuggestionProvider implements SuggestionProvider<CommandListenerWrapper>{
    
    private final List<String> suggestionsS;
    private final List<Integer> suggestionsI;
    private final CreateSuggestions createSuggestions;
    
    protected QSuggestionProvider(List<String> suggestionsS, List<Integer> suggestionsI, CreateSuggestions createSuggestions){
        this.suggestionsS = suggestionsS;
        this.suggestionsI = suggestionsI;
        this.createSuggestions = createSuggestions;
    }
    
    public static QSuggestionProvider functional(CreateSuggestions createSuggestions){
        return new QSuggestionProvider(null, null, createSuggestions);
    }
    
    public static QSuggestionProvider string(String... suggestions){
        List<String> suggestionsS = Arrays.asList(suggestions);
        List<Integer> suggestionsI = Collections.emptyList();
        return new QSuggestionProvider(suggestionsS, suggestionsI, null);
    }
    
    public static QSuggestionProvider integer(Integer... suggestions){
        List<Integer> suggestionsI = Arrays.asList(suggestions);
        List<String> suggestionsS = Collections.emptyList();
        return new QSuggestionProvider(suggestionsS, suggestionsI, null);
    }
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandListenerWrapper> context, SuggestionsBuilder builder) throws CommandSyntaxException{
        int i = context.getInput().lastIndexOf(' ')+1;
        builder = builder.createOffset(i);
        String substring = context.getInput().substring(i);
        if(suggestionsS==null && suggestionsI==null && createSuggestions!=null){
            List<String> strings = createSuggestions.create(context);
            strings = sort(substring, strings);
            for(String string : strings){
                builder.suggest(string);
            }
            return builder.buildFuture();
        }
    
        List<String> sortedSuggestions = sort(substring, suggestionsS);
    
        for(String s : sortedSuggestions){
            builder.suggest(s);
        }
        for(Integer integer : suggestionsI){
            builder.suggest(integer);
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
    public static interface CreateSuggestions{
        List<String> create(CommandContext<CommandListenerWrapper> context);
    }
}
