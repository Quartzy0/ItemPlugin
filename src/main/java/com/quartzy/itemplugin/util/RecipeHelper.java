package com.quartzy.itemplugin.util;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.items.ItemManager;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class RecipeHelper{
    
    public static void init(){
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        List<Recipe> recipeList = new ArrayList<>();
        while(recipeIterator.hasNext()){
            recipeList.add(recipeIterator.next());
        }
        Bukkit.clearRecipes();
        for(Recipe recipeUnknown : recipeList){
            if(recipeUnknown instanceof ShapedRecipe){
                ShapedRecipe recipeShapedBukkit = (ShapedRecipe) recipeUnknown;
                Map<Character, RecipeChoice> ingredientMap = recipeShapedBukkit.getChoiceMap();
                HashMap<Character, MaterialChoice> newMap = new HashMap<>();
                for(Map.Entry<Character, RecipeChoice> entry : ingredientMap.entrySet()){
                    if(entry.getValue() instanceof RecipeChoice.MaterialChoice){
                        newMap.put(entry.getKey(), new MaterialChoice(((RecipeChoice.MaterialChoice) entry.getValue())));
                    }else if(entry.getValue() instanceof MaterialChoice){
                        newMap.put(entry.getKey(), ((MaterialChoice) entry.getValue()));
                    }
                }
                shapedKey(recipeShapedBukkit.getResult(), newMap, CraftNamespacedKey.toMinecraft(recipeShapedBukkit.getKey()), recipeShapedBukkit.getShape());
            }else if(recipeUnknown instanceof ShapelessRecipe){
                ShapelessRecipe recipeShapelessBukkit = (ShapelessRecipe) recipeUnknown;
                List<RecipeChoice> choiceList = recipeShapelessBukkit.getChoiceList();
                List<MaterialChoice> newChoice = new ArrayList<>();
                for(RecipeChoice recipeChoice : choiceList){
                    if(recipeChoice instanceof RecipeChoice.MaterialChoice){
                        newChoice.add(new MaterialChoice(((RecipeChoice.MaterialChoice) recipeChoice)));
                    }else if(recipeChoice instanceof MaterialChoice){
                        newChoice.add(((MaterialChoice) recipeChoice));
                    }
                }
                
                shapelessKey(newChoice, recipeShapelessBukkit.getResult(), CraftNamespacedKey.toMinecraft(recipeShapelessBukkit.getKey()));
            }else{
                Bukkit.addRecipe(recipeUnknown);
            }
        }
    }
    
    private static void shapelessKey(List<MaterialChoice> ingredients, org.bukkit.inventory.ItemStack result, MinecraftKey key){
        NonNullList<RecipeItemStack> recipeItemStacks = NonNullList.a();
        for(MaterialChoice ingredient : ingredients){
            recipeItemStacks.add(new QRecipeItemStack(ingredient).toRecipeItemStack());
        }
    
        QShapelessRecipe recipe = new QShapelessRecipe(key, CraftItemStack.asNMSCopy(result), recipeItemStacks);
    
        MinecraftServer.getServer().getCraftingManager().addRecipe(recipe);
    }
    
    private static void shapedKey(org.bukkit.inventory.ItemStack result, HashMap<Character, MaterialChoice> charMap, MinecraftKey key, String... shape){
        NonNullList<RecipeItemStack> items = NonNullList.a();
        int w = 0, h = shape.length;
        for(int i = 0; i < shape.length; i++){
            w = Math.max(w, shape[i].length());
            for(int i1 = 0; i1 < shape[i].length(); i1++){
                items.add(new QRecipeItemStack(charMap.get(shape[i].charAt(i1))).toRecipeItemStack());
            }
        }
        QShapedRecipe recipe = new QShapedRecipe(key, w, h, items, CraftItemStack.asNMSCopy(result));
    
        MinecraftServer.getServer().getCraftingManager().addRecipe(recipe);
    }
    
    public static void addShapelessRecipe(List<MaterialChoice> ingredients, org.bukkit.inventory.ItemStack result){
        shapelessKey(ingredients, result, CraftNamespacedKey.toMinecraft(new NamespacedKey(ItemPlugin.getINSTANCE(), "recipe_custom_shapeless_" + ItemManager.getItemId(result))));
    }
    
    public static void addShapedRecipe(org.bukkit.inventory.ItemStack result, HashMap<Character, MaterialChoice> charMap, String... shape){
        shapedKey(result, charMap, CraftNamespacedKey.toMinecraft(new NamespacedKey(ItemPlugin.getINSTANCE(), "recipe_custom_shaped_" + ItemManager.getItemId(result))), shape);
    }
    
    public static class QShapelessRecipe extends ShapelessRecipes{
        
        private NonNullList<QRecipeItemStack> items;
        private net.minecraft.server.v1_16_R3.ItemStack result;
        private String group;
    
        public QShapelessRecipe(MinecraftKey key, net.minecraft.server.v1_16_R3.ItemStack itemstack, NonNullList<RecipeItemStack> items){
            super(key, "", itemstack, items);
            this.items = NonNullList.a();
            for(RecipeItemStack item : items){
                this.items.add(QRecipeItemStack.fromRecipeItemStack(item));
            }
            this.result = itemstack;
            this.group = " ";
        }
    
        @Override
        public ShapelessRecipe toBukkitRecipe(){
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftShapelessRecipe recipe = new CraftShapelessRecipe(result, this);
            recipe.setGroup(this.group);
            Iterator<QRecipeItemStack> var4 = this.items.iterator();
    
            while(var4.hasNext()) {
                QRecipeItemStack list = var4.next();
                recipe.addIngredient(list.recipeChoice);
            }
    
            return recipe;
        }
    
        @Override
        public boolean a(InventoryCrafting inventorycrafting, World world){
            HashMap<QRecipeItemStack, Integer> ingredientCount = new HashMap<>();
            for(QRecipeItemStack item : this.items){
                if(ingredientCount.containsKey(item)){
                    ingredientCount.put(item, ingredientCount.get(item)+1);
                }else{
                    ingredientCount.put(item, 1);
                }
            }
            int i = 0;
    
            for(int j = 0; j < inventorycrafting.getSize(); ++j) {
                net.minecraft.server.v1_16_R3.ItemStack itemstack = inventorycrafting.getItem(j);
                if (itemstack!=null && !itemstack.isEmpty()) {
                    ++i;
                    for(QRecipeItemStack itemStackQ : ingredientCount.keySet()){
                        if(itemStackQ.test(itemstack)){
                            ingredientCount.put(itemStackQ, ingredientCount.get(itemStackQ)-1);
                        }
                    }
                }
            }
            
            boolean flag = true;
            for(Integer value : ingredientCount.values()){
                if(value!=0){
                    flag = false;
                    break;
                }
            }
            
            if (i == this.items.size() && flag) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static class QShapedRecipe extends ShapedRecipes{
        private NonNullList<QRecipeItemStack> items;
        private int width, height;
        private String group;
        private net.minecraft.server.v1_16_R3.ItemStack result;
        private MinecraftKey key;
    
        public QShapedRecipe(MinecraftKey key, int w, int h, NonNullList<RecipeItemStack> items, net.minecraft.server.v1_16_R3.ItemStack itemstack){
            this(key, "", w, h, items, itemstack);
        }
    
        public QShapedRecipe(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemStack> nonnulllist, net.minecraft.server.v1_16_R3.ItemStack itemstack){
            super(minecraftkey, s, i, j, nonnulllist, itemstack);
            this.items = NonNullList.a();
            for(RecipeItemStack item : nonnulllist){
                this.items.add(QRecipeItemStack.fromRecipeItemStack(item));
            }
            this.width = i;
            this.height = j;
            this.group = s;
            this.result = itemstack;
            this.key = minecraftkey;
        }
    
        @Override
        public ShapedRecipe toBukkitRecipe(){
            CraftShapedRecipe recipe;
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            if(this.result==null || this.result.isEmpty()){
                System.out.println(this.key.toString());
            }
            recipe = new CraftShapedRecipe(result, this);
            recipe.setGroup(this.group);
            label40:
            switch(this.height) {
                case 1:
                    switch(this.width) {
                        case 1:
                            recipe.shape("a");
                            break label40;
                        case 2:
                            recipe.shape("ab");
                            break label40;
                        case 3:
                            recipe.shape("abc");
                        default:
                            break label40;
                    }
                case 2:
                    switch(this.width) {
                        case 1:
                            recipe.shape("a", "b");
                            break label40;
                        case 2:
                            recipe.shape("ab", "cd");
                            break label40;
                        case 3:
                            recipe.shape("abc", "def");
                        default:
                            break label40;
                    }
                case 3:
                    switch(this.width) {
                        case 1:
                            recipe.shape("a", "b", "c");
                            break;
                        case 2:
                            recipe.shape("ab", "cd", "ef");
                            break;
                        case 3:
                            recipe.shape("abc", "def", "ghi");
                    }
            }
    
            char c = 'a';
    
            for(Iterator<QRecipeItemStack> var5 = this.items.iterator(); var5.hasNext(); ++c) {
                QRecipeItemStack list = var5.next();
                RecipeChoice choice = list.getRecipeChoice();
                if (choice != null) {
                    recipe.setIngredient(c, choice);
                }
            }
    
            return recipe;
        }
    
        @Override
        public net.minecraft.server.v1_16_R3.ItemStack getResult(){
            return result;
        }
    
        @Override
        public boolean a(InventoryCrafting inventorycrafting, World world){
            for(int i = 0; i <= inventorycrafting.g() - this.width; ++i) {
                for(int j = 0; j <= inventorycrafting.f() - this.height; ++j) {
                    if (this.a(inventorycrafting, i, j, true)) {
                        return true;
                    }
            
                    if (this.a(inventorycrafting, i, j, false)) {
                        return true;
                    }
                }
            }
    
            return false;
        }
    
        private boolean a(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
            for(int k = 0; k < inventorycrafting.g(); ++k) {
                for(int l = 0; l < inventorycrafting.f(); ++l) {
                    int i1 = k - i;
                    int j1 = l - j;
                    QRecipeItemStack recipeitemstack = null;
                    if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                        if (flag) {
                            recipeitemstack = this.items.get(this.width - i1 - 1 + j1 * this.width);
                        } else {
                            recipeitemstack = this.items.get(i1 + j1 * this.width);
                        }
                    }
                    
                    if(recipeitemstack==null)continue;
                    if (!recipeitemstack.test(inventorycrafting.getItem(k + l * inventorycrafting.g()))) {
                        return false;
                    }
                }
            }
        
            return true;
        }
    }
    
    public static class QRecipeItemStack{
        @Getter
        private RecipeChoice recipeChoice;
    
        public QRecipeItemStack(RecipeChoice recipeChoice){
            this.recipeChoice = recipeChoice;
        }
    
        public boolean test(net.minecraft.server.v1_16_R3.ItemStack item){
            if(recipeChoice == null){
                return item == null || item.isEmpty();
            }
            if(recipeChoice instanceof MaterialChoice){
                return recipeChoice.test(CraftItemStack.asBukkitCopy(item));
            }else if(recipeChoice instanceof RecipeChoice.MaterialChoice){
                RecipeChoice.MaterialChoice choice = (RecipeChoice.MaterialChoice) recipeChoice;
                for(org.bukkit.Material choiceChoice : choice.getChoices()){
                    if(ItemManager.getItemId(item).equals(choiceChoice.name()))return true;
                }
                return false;
            }
            return recipeChoice.test(CraftItemStack.asBukkitCopy(item));
        }
        
        public RecipeItemStack toRecipeItemStack(){
            List<RecipeItemStack.StackProvider> providers = new ArrayList<>();
            if(recipeChoice instanceof MaterialChoice){
                ItemManager itemManager = ItemPlugin.getINSTANCE().getItemManager();
                ((MaterialChoice) recipeChoice).choices.stream().forEach(s -> {
                    providers.add(new RecipeItemStack.StackProvider(CraftItemStack.asNMSCopy(itemManager.createItem(s, 1))));
                });
            }
            return new RecipeItemStack(providers.stream());
        }
        
        public static QRecipeItemStack fromRecipeItemStack(RecipeItemStack recipeItemStack){
            recipeItemStack.buildChoices();
            net.minecraft.server.v1_16_R3.ItemStack[] choices = recipeItemStack.choices;
            List<String> matChoices = new ArrayList<>();
            for(int i = 0; i < choices.length; i++){
                matChoices.add(ItemManager.getItemId(choices[i]));
            }
            if(matChoices.isEmpty()){
                return new QRecipeItemStack(null);
            }
            return new QRecipeItemStack(new MaterialChoice(matChoices));
        }
    }
    
    public static class MaterialChoice implements RecipeChoice{
        @Getter
        private List<String> choices;
    
        public MaterialChoice(RecipeChoice.MaterialChoice materialChoice){
            List<org.bukkit.Material> choices = materialChoice.getChoices();
            this.choices = new ArrayList<>();
            for(org.bukkit.Material choice : choices){
                this.choices.add(choice.name());
            }
        }
    
        public MaterialChoice(List<String> choices){
            if(choices ==null || choices.isEmpty()){
                throw new IllegalArgumentException("Material choice list can not be null or empty");
            }
            this.choices = choices;
        }
        
        public MaterialChoice(String... choices){
            if(choices ==null || choices.length==0){
                throw new IllegalArgumentException("Material choice list can not be null or empty");
            }
            this.choices = Arrays.asList(choices);
        }
    
        @Override
        public org.bukkit.inventory.ItemStack getItemStack(){
            ItemManager itemManager = ItemPlugin.getItemManager();
            return itemManager.createItem(choices.get(0), 1);
        }
    
        @Override
        public RecipeChoice clone(){
            try {
                RecipeHelper.MaterialChoice clone = (RecipeHelper.MaterialChoice)super.clone();
                clone.choices = new ArrayList(this.choices);
                return clone;
            } catch (CloneNotSupportedException var2) {
                throw new AssertionError(var2);
            }
        }
    
        @Override
        public boolean test(org.bukkit.inventory.ItemStack itemStack){
            String itemId = ItemManager.getItemId(itemStack);
            return choices.contains(itemId);
        }
    }
}
