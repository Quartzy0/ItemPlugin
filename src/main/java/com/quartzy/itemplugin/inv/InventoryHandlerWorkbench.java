package com.quartzy.itemplugin.inv;

import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.*;

import java.util.*;

public class InventoryHandlerWorkbench extends InventoryHandler{
    
    private RecipeResult currentRecipe = null;
    
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
    public void itemClickedPost(int slot, InventoryAction action, ClickType clickType){
        System.out.println("Slot: " + slot + " Action: " + action + " Click Type: " + clickType);
    
        resultCheck: {
            if(slot == 9 * 2 + 5){
                if(currentRecipe == null) break resultCheck;
                if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                    PlayerInventory inventory = getPlayer().getInventory();
                    if(inventory.firstEmpty() == -1 && inventory().getItem(9*2+5)!=null)break resultCheck;
                }
                if(action != InventoryAction.PICKUP_ALL && action != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
                ItemStack[] recipeItems = new ItemStack[]{inventory().getItem(9 + 1), inventory().getItem(9 + 2), inventory().getItem(9 + 3),
                        inventory().getItem(9 * 2 + 1), inventory().getItem(9 * 2 + 2), inventory().getItem(9 * 2 + 3),
                        inventory().getItem(9 * 3 + 1), inventory().getItem(9 * 3 + 2), inventory().getItem(9 * 3 + 3)};
        
                List<RecipeChoice> ingredients = new ArrayList<>(currentRecipe.ingredients.size());
                for(int i = 0; i < currentRecipe.ingredients.size(); i++){
                    ingredients.add(null);
                }
                Collections.copy(ingredients, currentRecipe.ingredients);
        
                for(int i = 0; i < recipeItems.length; i++){
                    if(recipeItems[i] == null) continue;
                    int indexOf = -1;
                    for(int i1 = 0; i1 < ingredients.size(); i1++){
                        if(recipeItems[i]==null)continue;
                        if(ingredients.get(i1).test(recipeItems[i])){
                            indexOf = i1;
                            break;
                        }
                    }
                    if(indexOf != -1){
                        int slotIndex = 9 * (i / 3 + 1) + (i % 3) + 1;
                        recipeItems[i].setAmount(recipeItems[i].getAmount() - 1);
                        if(recipeItems[i].getAmount() <= 0){
                            inventory().setItem(slotIndex, null);
                        } else{
                            inventory().setItem(slotIndex, recipeItems[i]);
                        }
                        ingredients.remove(indexOf);
                    }
                }
            }
        }
        
        if(action==InventoryAction.MOVE_TO_OTHER_INVENTORY || slot == 9*1+1 || slot == 9*1+2 || slot == 9*1+3 || slot == 9*2+1 || slot == 9*2+2 || slot == 9*2+3 || slot == 9*3+1 || slot == 9*3+2 || slot == 9*3+3 || slot==9*2+5){
            ItemStack[] recipeItems = new ItemStack[]{inventory().getItem(9 + 1), inventory().getItem(9 + 2), inventory().getItem(9 + 3),
                    inventory().getItem(9 * 2 + 1), inventory().getItem(9 * 2 + 2), inventory().getItem(9 * 2 + 3),
                    inventory().getItem(9 * 3 + 1), inventory().getItem(9 * 3 + 2), inventory().getItem(9 * 3 + 3)};
            
            currentRecipe = getRecipe(recipeItems);
            if(currentRecipe != null){
                inventory().setItem(9 * 2 + 5, currentRecipe.item);
            } else{
                inventory().setItem(9 * 2 + 5, noName(Material.BARRIER));
            }
        }
    }
    
    @Override
    public boolean shouldCancelClick(int slot, InventoryAction action, ClickType clickType){
        if(slot == 9*1+1 || slot == 9*1+2 || slot == 9*1+3 || slot == 9*2+1 || slot == 9*2+2 || slot == 9*2+3 || slot == 9*3+1 || slot == 9*3+2 || slot == 9*3+3) return false;
        if(slot==9*2+5){
            ItemStack item = inventory().getItem(9 * 2 + 5);
            return item==null || item.getType() == Material.BARRIER;
        }
        return isInsideInventory(slot);
    }
    
    @Override
    public boolean shouldCancelDrag(Map<Integer, ItemStack> slots, DragType type){
        return false;
    }
    
    @Override
    public void inventoryDragPost(Map<Integer, ItemStack> slots, DragType type){
        itemClickedPost(9*2+1, null, null);
    }
    
    @Override
    public void inventoryClose(){
        ItemStack[] recipeItems = new ItemStack[]{inventory().getItem(9 + 1), inventory().getItem(9 + 2), inventory().getItem(9 + 3),
                inventory().getItem(9 * 2 + 1), inventory().getItem(9 * 2 + 2), inventory().getItem(9 * 2 + 3),
                inventory().getItem(9 * 3 + 1), inventory().getItem(9 * 3 + 2), inventory().getItem(9 * 3 + 3)};
    
        for(int i = 0; i < recipeItems.length; i++){
            if(recipeItems[i]==null)continue;
            PlayerInventory inventory = getPlayer().getInventory();
            HashMap<Integer, ItemStack> leftOver = inventory.addItem(recipeItems[i]);
            for(ItemStack value : leftOver.values()){
                getPlayer().getWorld().dropItem(getPlayer().getLocation(), value);
            }
        }
    }
    
    public static class InvCrafting extends InventoryCrafting{
        private net.minecraft.server.v1_16_R3.ItemStack[] items;
        public IRecipe currentRecipe;
    
        public InvCrafting(Container container, int i, int j, EntityHuman player, ItemStack[] items){
            super(container, i, j, player);
            this.items = new net.minecraft.server.v1_16_R3.ItemStack[items.length];
            for(int i1 = 0; i1 < this.items.length; i1++){
                this.items[i1] = CraftItemStack.asNMSCopy(items[i1]);
            }
        }
    
        @Override
        public net.minecraft.server.v1_16_R3.ItemStack getItem(int i){
            return items[i];
        }
    
        @Override
        public List<net.minecraft.server.v1_16_R3.ItemStack> getContents(){
            return Arrays.asList(items);
        }
    
        @Override
        public void setCurrentRecipe(IRecipe currentRecipe){
            this.currentRecipe = currentRecipe;
        }
    
        @Override
        public IRecipe getCurrentRecipe(){
            return this.currentRecipe;
        }
    
        @Override
        public int getMaxStackSize(){
            return 9;
        }
    
        @Override
        public int getSize(){
            return this.items.length;
        }
    
        @Override
        public int g(){
            return 3;
        }
    
        @Override
        public int f(){
            return 3;
        }
    }
    
    public RecipeResult getRecipe(ItemStack[] items){
        
        InvCrafting inventoryCrafting = new InvCrafting(null, 0, 0, ((CraftPlayer) getPlayer()).getHandle(), items);
        Iterator<WorldServer> worlds = MinecraftServer.getServer().getWorlds().iterator();
        WorldServer worldServer = null;
        while(worlds.hasNext()){
            WorldServer next = worlds.next();
            if(next.getWorld().getName().equals(getPlayer().getWorld().getName())){
                worldServer = next;
                break;
            }
        }
        Optional<RecipeCrafting> optional = MinecraftServer.getServer().getCraftingManager().craft(Recipes.CRAFTING, inventoryCrafting, worldServer);
        System.out.println(optional.toString());
        if (optional.isPresent()) {
            RecipeCrafting recipecrafting = optional.get();
            if(recipecrafting instanceof ShapedRecipes){
                ShapedRecipe shapedRecipe = ((ShapedRecipes) recipecrafting).toBukkitRecipe();
    
                List<RecipeChoice> ingredients = new ArrayList<>();
                
                String[] shape = shapedRecipe.getShape();
                Map<Character, RecipeChoice> ingredientMap = shapedRecipe.getChoiceMap();
                for(int i = 0; i < shape.length; i++){
                    for(int j = 0; j < shape[i].length(); j++){
                        RecipeChoice choice = ingredientMap.get(shape[i].charAt(j));
                        if(choice!=null){
                            ingredients.add(choice);
                        }
                    }
                }
                return new RecipeResult(shapedRecipe.getResult(), ingredients);
            }else if(recipecrafting instanceof ShapelessRecipes){
                ShapelessRecipe shapelessRecipe = ((ShapelessRecipes) recipecrafting).toBukkitRecipe();
    
                return new RecipeResult(shapelessRecipe.getResult(), shapelessRecipe.getChoiceList());
            }
        }
        
        return null;
    }
    
    private class RecipeResult{
        public final ItemStack item;
        public final List<RecipeChoice> ingredients;
    
        public RecipeResult(ItemStack item, List<RecipeChoice> ingredients){
            this.item = item;
            this.ingredients = ingredients;
        }
    }
}
