package com.quartzy.itemplugin.inv;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.util.RecipeHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.*;

import java.util.*;

public class InventoryHandlerWorkbench extends InventoryHandler{
    
    @Override
    public Inventory inventoryOpen(){
        Inventory inventory = createInventory(54, "Workbench");
        fillInventory(noName(), inventory);
        
        inventory.setItem(9*1+1, null);
        inventory.setItem(9*1+2, null);
        inventory.setItem(9*1+3, null);
        inventory.setItem(9*2+1, null);
        inventory.setItem(9*2+2, null);
        inventory.setItem(9*2+3, null);
        inventory.setItem(9*3+1, null);
        inventory.setItem(9*3+2, null);
        inventory.setItem(9*3+3, null);
        
        inventory.setItem(9*2+5, noName(Material.BARRIER));
        inventory.setItem(9*5+4, noName(Material.BARRIER));
        
        inventory.setItem(9*5, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+1, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+2, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+3, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+5, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+6, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+7, noName(Material.RED_STAINED_GLASS_PANE));
        inventory.setItem(9*5+8, noName(Material.RED_STAINED_GLASS_PANE));
        return inventory;
    }
    
    @Override
    public boolean itemClicked(int slot, InventoryAction action, ClickType clickType){
        Bukkit.getScheduler().runTaskLater(ItemPlugin.getINSTANCE(), new Runnable(){
            @Override
            public void run(){
                System.out.println("Slot: " + slot + " Action: " + action + " Click Type: " + clickType);
    
                ItemStack[] recipeItems = new ItemStack[]{inventory().getItem(9+1), inventory().getItem(9+2), inventory().getItem(9+3),
                        inventory().getItem(9*2+1), inventory().getItem(9*2+2), inventory().getItem(9*2+3),
                        inventory().getItem(9*3+1), inventory().getItem(9*3+2), inventory().getItem(9*3+3)};
                long start = System.currentTimeMillis();
                ItemStack recipe = getRecipe(recipeItems);
                System.out.println("Took " + (System.currentTimeMillis()-start) + "ms to find recipe");
                if(recipe!=null){
                    inventory().setItem(9*2+5, recipe);
                }else{
                    inventory().setItem(9*2+5, noName(Material.BARRIER));
                }
            }
        }, 1L);
    
        if(slot == 9*1+1 || slot == 9*1+2 || slot == 9*1+3 || slot == 9*2+1 || slot == 9*2+2 || slot == 9*2+3 || slot == 9*3+1 || slot == 9*3+2 || slot == 9*3+3) return false;
        return isInsideInventory(slot);
    }
    
    public ItemStack getRecipe(ItemStack[] items){
        List<String> neededThings = new ArrayList<>();
        for(int i = 0; i < items.length; i++){
            if(items[i]!=null)neededThings.add(items[i].getType().name());
            else neededThings.add(null);
        }
        ShapelessRecipe recipe = RecipeHelper.getShapeless().get(new RecipeHelper.ShapelessContainer(neededThings));
        if(recipe==null){
            ShapedRecipe shapedRecipe = RecipeHelper.getShaped().get(new RecipeHelper.ShapedContainer(neededThings));
            if(shapedRecipe==null)return null;
            return shapedRecipe.getResult();
        }
        System.out.println(recipe.getResult());
        return recipe.getResult();
        
        /*Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while(recipeIterator.hasNext()){
            Recipe recipe1 = recipeIterator.next();
            if(recipe1 instanceof ShapelessRecipe){
                ShapelessRecipe recipe = (ShapelessRecipe) recipe1;
                HashMap<ItemStack, Integer> foundStuff = new HashMap<>();
                for(int i = 0; i < items.length; i++){
                    if(recipe.getIngredientList().contains(items[i])){
                        if(foundStuff.containsKey(items[i])){
                            foundStuff.put(items[i], foundStuff.get(items[i])+1);
                        }else{
                            foundStuff.put(items[i], 1);
                        }
                    }
                }
                HashMap<ItemStack, Integer> neededStuff = new HashMap<>();
                for(int i = 0; i < recipe.getIngredientList().size(); i++){
                    if(recipe.getIngredientList().contains(recipe.getIngredientList().get(i))){
                        if(neededStuff.containsKey(recipe.getIngredientList().get(i))){
                            neededStuff.put(recipe.getIngredientList().get(i), neededStuff.get(recipe.getIngredientList().get(i))+1);
                        }else{
                            neededStuff.put(recipe.getIngredientList().get(i), 1);
                        }
                    }
                }
                if(foundStuff.equals(neededStuff)){
                    return recipe.getResult();
                }
            }else if(recipe1 instanceof ShapedRecipe){
            
            }
        }
        return null;*/
    }
}
