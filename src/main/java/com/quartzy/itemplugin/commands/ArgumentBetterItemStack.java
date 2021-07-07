package com.quartzy.itemplugin.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.quartzy.itemplugin.ItemPlugin;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class ArgumentBetterItemStack extends ArgumentItemStack implements QArgumentType<ArgumentPredicateItemStack>{
    
    public static ArgumentBetterItemStack a(){
        return new ArgumentBetterItemStack();
    }
    
    @Override
    public ArgumentPredicateItemStack parse(StringReader var0) throws CommandSyntaxException{
        QArgumentParserItemStack var1 = (new QArgumentParserItemStack(var0, false)).h();
        return new ArgumentPredicateItemStack(var1.b(), var1.c());
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var0, SuggestionsBuilder var1){
        StringReader var2 = new StringReader(var1.getInput());
        var2.setCursor(var1.getStart());
        QArgumentParserItemStack var3 = new QArgumentParserItemStack(var2, false);
    
        try {
            var3.h();
        } catch (CommandSyntaxException var6) {
        }
    
        return var3.a(var1, TagsItem.a());
    }
    
    @Override
    public String getSerializerId(){
        return "resource_location";
    }
    
    public static void mergeTags(NBTTagCompound tag1, NBTTagCompound tag2){
        for(String key : tag2.getKeys()){
            NBTBase nbtBase = tag2.get(key);
            if(nbtBase instanceof NBTList){
                NBTList list = (NBTList) nbtBase;
                if(tag2.hasKey(key)){
                    NBTBase nbtBase1 = tag1.get(key);
                    if(nbtBase1 instanceof NBTList){
                        NBTList list1 = (NBTList) nbtBase1;
                        if(list1.d_()!=list.d_()){
                            tag1.set(key, list);
                            continue;
                        }
                        for(Object base : list){
                            list1.add(((NBTBase) base).getTypeId(), ((NBTBase) base));
                        }
                    }
                }else{
                    tag1.set(key, list);
                }
            }else if(nbtBase instanceof NBTTagCompound){
                if(tag1.hasKey(key)){
                    NBTBase nbtBase1 = tag1.get(key);
                    if(nbtBase1 instanceof NBTTagCompound){
                        mergeTags(((NBTTagCompound) nbtBase1), ((NBTTagCompound) nbtBase));
                    }else{
                        tag1.set(key, nbtBase);
                    }
                }else{
                    tag1.set(key, nbtBase);
                }
            }else{
                tag1.set(key, nbtBase);
            }
        }
    }
    
    private static class QArgumentParserItemStack extends ArgumentParserItemStack{
        private static final BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> c = (var0, var1) -> var0.buildFuture();
        private final StringReader d;
        private final boolean e;
        private final Map<IBlockState<?>, Comparable<?>> f = Maps.newHashMap();
        private Item g;
        private MinecraftKey actualItem;
    
        private MinecraftKey i = new MinecraftKey("");
        private int j;
        private BiFunction<SuggestionsBuilder, Tags<Item>, CompletableFuture<Suggestions>> k;
        private NBTTagCompound nbt;
    
        public QArgumentParserItemStack(StringReader var0, boolean var1){
            super(var0, var1);
            this.d = var0;
            this.e = var1;
        }
    
        @Nullable
        @Override
        public NBTTagCompound c(){
            return nbt;
        }
    
        private CompletableFuture<Suggestions> d(SuggestionsBuilder var0, Tags<Item> var1) {
            if (this.e) {
                ICompletionProvider.a(var1.b(), var0, String.valueOf('#'));
            }
    
            ItemPlugin instance = ItemPlugin.getINSTANCE();
            if(instance!=null && ItemPlugin.getItemManager()!=null){
                return ICompletionProvider.a(ItemPlugin.getItemManager().getAllItems(), var0);
            }
            return ICompletionProvider.a(IRegistry.ITEM.keySet(), var0);
        }
    
        @Override
        public MinecraftKey d(){
            return this.i;
        }
    
        @Override
        public void f() throws CommandSyntaxException{
            if (!this.e) {
                throw a.create();
            } else {
                this.k = this::c;
                this.d.expect('#');
                this.j = this.d.getCursor();
                MinecraftKey itemId = MinecraftKey.a(this.d);
                if(ItemPlugin.isItemsAvailable()){
                    this.i = ItemPlugin.getItemManager().getMinecraftItem(itemId);
                }else{
                    this.i = itemId;
                }
            }
        }
    
        private CompletableFuture<Suggestions> c(SuggestionsBuilder var0, Tags<Item> var1) {
            return ICompletionProvider.a(var1.b(), var0.createOffset(this.j));
        }
    
        @Override
        public QArgumentParserItemStack h() throws CommandSyntaxException{
            this.k = this::d;
            if (this.d.canRead() && this.d.peek() == '#') {
                this.f();
            } else {
                this.e();
                this.k = this::b;
            }
    
            if (this.d.canRead() && this.d.peek() == '{') {
                this.k = c;
            }
            this.g();
    
            return this;
        }
    
        @Override
        public Item b(){
            return this.g;
        }
    
        private CompletableFuture<Suggestions> b(SuggestionsBuilder var0, Tags<Item> var1) {
            if (var0.getRemaining().isEmpty()) {
                var0.suggest(String.valueOf('{'));
            }
        
            return var0.buildFuture();
        }
    
        @Override
        public CompletableFuture a(SuggestionsBuilder var0, Tags<Item> var1){
            return this.k.apply(var0.createOffset(this.d.getCursor()), var1);
        }
    
        @Override
        public void g() throws CommandSyntaxException{
            if(this.actualItem!=null && ItemPlugin.isItemsAvailable()){
                ItemStack item = ItemPlugin.getItemManager().createItem(this.actualItem, 1);
                net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
                this.nbt = itemStack.getTag();
            }
        }
    
        @Override
        public void e() throws CommandSyntaxException{
            int var0 = this.d.getCursor();
            MinecraftKey var2 = MinecraftKey.a(this.d);
            MinecraftKey var1;
            if(ItemPlugin.getINSTANCE()!=null && ItemPlugin.getItemManager()!=null){
                var1 = ItemPlugin.getItemManager().getMinecraftItem(var2);
            }else{
                var1 = var2;
            }
            this.actualItem = var2;
            MinecraftKey finalVar = var1;
            this.g = IRegistry.ITEM.getOptional(var1).orElseThrow(() -> {
                this.d.setCursor(var0);
                return b.createWithContext(this.d, finalVar.toString());
            });
        }
    }
}
