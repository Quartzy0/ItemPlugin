package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.v1_16_R3.MinecraftKey;

import java.util.function.Predicate;

public class QLiteralArgumentBuilder<S> extends LiteralArgumentBuilder<S>{
    protected QLiteralArgumentBuilder(String literal){
        super(literal);
    }
    
    public static <S> QLiteralArgumentBuilder<S> literal(String name) {
        return new QLiteralArgumentBuilder<S>(name);
    }
    
    @Override
    protected QLiteralArgumentBuilder<S> getThis(){
        return this;
    }
    
    public <T> QLiteralArgumentBuilder<S> then(String s, ArgumentType<T> argumenttype, SuggestionProvider suggestions){
        then(RequiredArgumentBuilder.argument(s, argumenttype).suggests(suggestions));
        return this.getThis();
    }
    
    public <T> QLiteralArgumentBuilder<S> then(String s, ArgumentType<T> argumenttype, String... suggestions){
        return then(s, argumenttype, QSuggestionProvider.string(suggestions));
    }
    
    public <T> QLiteralArgumentBuilder<S> then(String s, ArgumentType<T> argumenttype, MinecraftKey... suggestions){
        return then(s, argumenttype, QSuggestionProvider.keyed(suggestions));
    }
    
    public <T> QLiteralArgumentBuilder<S> then(String s, ArgumentType<T> argumenttype, Integer... suggestions){
        return then(s, argumenttype, QSuggestionProvider.integer(suggestions));
    }
    
    public <T> QLiteralArgumentBuilder<S> then(String s, ArgumentType<T> argumenttype){
        then(RequiredArgumentBuilder.argument(s, argumenttype));
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> executes(Command<S> command){
        super.executes(command);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> then(ArgumentBuilder<S, ?> argument){
        super.then(argument);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> redirect(CommandNode<S> target){
        super.redirect(target);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> redirect(CommandNode<S> target, SingleRedirectModifier<S> modifier){
        super.redirect(target, modifier);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> fork(CommandNode<S> target, RedirectModifier<S> modifier){
        super.fork(target, modifier);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> forward(CommandNode<S> target, RedirectModifier<S> modifier, boolean fork){
        super.forward(target, modifier, fork);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> then(CommandNode<S> argument){
        super.then(argument);
        return this.getThis();
    }
    
    @Override
    public QLiteralArgumentBuilder<S> requires(Predicate<S> requirement){
        super.requires(requirement);
        return this.getThis();
    }
}
