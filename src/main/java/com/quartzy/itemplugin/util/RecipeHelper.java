package com.quartzy.itemplugin.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RecipeHelper{
    
    private static long a1, b1, p1;
    
    @Getter
    private static HashMap<ShapelessContainer, ShapelessRecipe> shapeless = new HashMap<>();
    @Getter
    private static HashMap<ShapedContainer, ShapedRecipe> shaped = new HashMap<>();
    
    public static void init(){
        shapeless.clear();
        shaped.clear();
        long start = System.currentTimeMillis();
        a1 = ThreadLocalRandom.current().nextLong(1, Integer.MAX_VALUE);
        b1 = ThreadLocalRandom.current().nextLong(1, Integer.MAX_VALUE);
        p1 = Integer.MAX_VALUE + ThreadLocalRandom.current().nextLong(1, Integer.MAX_VALUE+20000L);
        if(p1 % 2 == 0) p1++;
        double ceil = Math.ceil(Math.sqrt(p1));
        for(long i = 3; i < ceil; i+=2){
            if(p1 % i == 0){
                ++p1;
                i=1;
                ceil = Math.ceil(Math.sqrt(p1));
            }
        }
        System.out.println("Time taken: " + (System.currentTimeMillis()-start) + "ms");
        
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while(recipeIterator.hasNext()){
            Recipe recipe = recipeIterator.next();
            if(recipe instanceof ShapelessRecipe){
                shapeless.put(new ShapelessContainer(getShapelessIngredients((ShapelessRecipe) recipe)), (ShapelessRecipe) recipe);
            }else if(recipe instanceof ShapedRecipe){
                shaped.put(new ShapedContainer(getShapedIngredients((ShapedRecipe) recipe)), (ShapedRecipe) recipe);
            }
        }
    }
    
    public static List<String> getShapelessIngredients(ShapelessRecipe recipe){
        List<String> neededStuff = new ArrayList<>();
        List<ItemStack> ingredientList = recipe.getIngredientList();
        for(ItemStack itemStack : ingredientList){
            neededStuff.add(itemStack.getType().name());
        }
        return neededStuff;
    }
    
    public static List<String> getShapedIngredients(ShapedRecipe recipe){
        List<String> neededStuff = new ArrayList<>();
        Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
        String[] shape = recipe.getShape();
        for(String s : shape){
            for(int i = 0; i < 3; i++){
                if(i>s.length()-1){
                    neededStuff.add(null);
                }else{
                    ItemStack itemStack = ingredientMap.get(s.charAt(i));
                    if(itemStack==null){
                        neededStuff.add(null);
                        continue;
                    }
                    neededStuff.add(itemStack.getType().name());
                }
            }
        }
        return neededStuff;
    }
    
    public static final class ShapedContainer{
        public final List<String> recipe;
        
        public ShapedContainer(List<String> recipe){
            this.recipe = new ArrayList<>();
            boolean begin = false;
            for(int i = 0; i < recipe.size(); i++){
                if(recipe.get(i)!=null || begin){
                    begin = true;
                    this.recipe.add(recipe.get(i));
                }
            }
            for(int i = this.recipe.size() - 1; i >= 0; i--){
                if(this.recipe.get(i)!=null)break;
                this.recipe.remove(i);
            }
        }
        
        @Override
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            ShapedContainer that = (ShapedContainer) o;
    
    
            boolean equals = this.recipe.equals(that.recipe);
            if(equals){
                System.out.println(this.recipe);
                System.out.println(that.recipe);
            }
            return equals;
        }
        
        @Override
        public int hashCode(){
            long finalResult = 0;
            for(int i = 0;i<recipe.size();i++){
                String s = recipe.get(i);
                if(s ==null)continue;
                long t = (s.hashCode()*a1+b1) % p1;
                t ^= t << 17;
                t ^= t >> 5;
                t ^= t << 19;
                finalResult ^= t;
            }
            return (int) finalResult;
        }
    }
    
    public static final class ShapelessContainer{
        public final List<String> recipe;
    
        public ShapelessContainer(List<String> recipe){
            this.recipe = new ArrayList<>();
            for(String itemStack : recipe){
                if(itemStack!=null)this.recipe.add(itemStack);
            }
        }
    
        @Override
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            ShapelessContainer that = (ShapelessContainer) o;
            if(that.recipe.size()!=this.recipe.size())return false;
            return that.recipe.containsAll(this.recipe);
        }
    
        @Override
        public int hashCode(){
            long finalResult = 0;
            
            for(String itemStack : recipe){
                if(itemStack==null)continue;
                finalResult+=(itemStack.hashCode()*a1+b1) % p1;
            }
            return (int) finalResult;
        }
    }
}
