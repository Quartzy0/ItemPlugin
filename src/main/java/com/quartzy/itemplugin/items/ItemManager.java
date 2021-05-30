package com.quartzy.itemplugin.items;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.abilities.Ability;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemManager{
    
    private HashMap<MinecraftKey, CustomItem> items = new HashMap<>();
    private HashMap<MinecraftKey, Ability> abilities = new HashMap<>();
    
    public void addItem(CustomItem item){
        items.put(item.getId(), item);
    }
    
    public CustomItem getItemById(String id){
        return items.get(id);
    }
    
    public List<MinecraftKey> getAllItems(){
        ArrayList<MinecraftKey> strings = new ArrayList<>();
        strings.addAll(items.keySet());
        for(Material value : Material.values()){
            if(!value.isLegacy())strings.add(CraftNamespacedKey.toMinecraft(value.getKey()));
        }
        return strings;
    }
    
    public List<String> getAllItemsS(){
        ArrayList<String> strings = new ArrayList<>();
        for(MinecraftKey minecraftKey : items.keySet()){
            strings.add(minecraftKey.toString());
        }
        for(Material value : Material.values()){
            if(!value.isLegacy())strings.add(CraftNamespacedKey.toMinecraft(value.getKey()).toString());
        }
        return strings;
    }
    
    public static MinecraftKey getItemId(ItemStack item){
        if(item==null)return null;
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItemStack.getTag();
        if(tag==null)return CraftNamespacedKey.toMinecraft(item.getType().getKey());
        if(!tag.hasKey("internalId"))return CraftNamespacedKey.toMinecraft(item.getType().getKey());
        return new MinecraftKey(tag.getString("internalId"));
    }
    
    public static MinecraftKey getItemId(net.minecraft.server.v1_16_R3.ItemStack item){
        return getItemId(CraftItemStack.asBukkitCopy(item));
    }
    
    public Ability getAbility(String id){
        return abilities.get(new MinecraftKey(id));
    }
    
    public Ability getAbility(MinecraftKey id){
        return abilities.get(id);
    }
    
    public void addAbility(Ability ability){
        abilities.put(ability.getId(), ability);
    }
    
    public boolean isItemValid(@Nullable ItemStack itemStack){
        if(itemStack==null)return false;
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItemStack.getTag();
        if(tag==null)return false;
        java.lang.String pluginVersion = tag.getString("pluginVersion");
        if(pluginVersion==null || !pluginVersion.equals(ItemPlugin.pluginVersion)) return false;
        java.lang.String internalId = tag.getString("internalId");
        if(internalId==null || internalId.isEmpty()) return false;
        if(!tag.hasKey("inUse")) return false;
        return true;
    }
    
    public ItemStack createItem(CustomItem item, int amount){
        ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
    
        itemMeta.setLore(item.generateLore());
        itemMeta.setDisplayName(ChatColor.RESET + item.getName());
        
        itemStack.setItemMeta(itemMeta);
    
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItemStack.getOrCreateTag();
        tag.setString("pluginVersion", ItemPlugin.pluginVersion);
        tag.setString("internalId", item.getId().toString());
        if(item.getAbilities()!=null && item.getAbilities().length!=0){
            NBTTagList nbtTagList = new NBTTagList();
    
            for(Ability ability : item.getAbilities()){
                try{
                    Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                    constructor.setAccessible(true);
                    NBTTagString nbtTagString = constructor.newInstance(ability.getId().toString());
                    nbtTagList.add(nbtTagString);
                } catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            tag.set("abilities", nbtTagList);
        }
        
        tag.setBoolean("inUse", true);
        nmsItemStack.setTag(tag);
        itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
    
        return itemStack;
    }
    
    public ItemStack createItem(String id, int amount){
        return createItem(new MinecraftKey(id), amount);
    }
    
    public ItemStack createItem(MinecraftKey id, int amount){
        if(amount==0)return null;
        CustomItem customItem = items.get(id);
        Material material = customItem==null ? Material.matchMaterial(id.toString(), false) : customItem.getMaterial();
        if(material==null)return null;
        
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        
        if(customItem==null){
            itemMeta.setLore(Arrays.asList("", ChatColor.BOLD + Rarity.COMMON.fullName));
        }else{
            itemMeta.setLore(customItem.generateLore());
            itemMeta.setDisplayName(ChatColor.RESET + customItem.getName());
        }
        itemStack.setItemMeta(itemMeta);
    
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItemStack.getOrCreateTag();
        tag.setString("pluginVersion", ItemPlugin.pluginVersion);
        tag.setString("internalId", id.toString());
        if(customItem!=null && customItem.getAbilities()!=null && customItem.getAbilities().length!=0){
            NBTTagList nbtTagList = new NBTTagList();
    
            for(Ability ability : customItem.getAbilities()){
                try{
                    Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                    constructor.setAccessible(true);
                    NBTTagString nbtTagString = constructor.newInstance(ability.getId().toString());
                    nbtTagList.add(nbtTagString);
                } catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            tag.set("abilities", nbtTagList);
        }
        tag.setBoolean("inUse", customItem!=null);
        nmsItemStack.setTag(tag);
        itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
        
        return itemStack;
    }
    
    public ItemStack validateItem(@Nullable ItemStack itemStack){
        if(itemStack==null)return null;
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItemStack.getOrCreateTag();
        String pluginVersion = tag.getString("pluginVersion");
        String internalIdString = tag.getString("internalId");
        MinecraftKey internalId = new MinecraftKey(internalIdString);
        if(pluginVersion==null || pluginVersion.isEmpty() || internalIdString==null || internalIdString.isEmpty()){
            tag.setString("pluginVersion", ItemPlugin.pluginVersion);
            tag.setString("internalId", itemStack.getType().getKey().toString());
            if(items.containsKey(CraftNamespacedKey.toMinecraft(itemStack.getType().getKey()))){
                tag.setBoolean("inUse", true);
    
                CustomItem customItem = items.get(CraftNamespacedKey.toMinecraft(itemStack.getType().getKey()));
                NBTTagList nbtTagList = new NBTTagList();
                
                for(Ability ability : customItem.getAbilities()){
                    try{
                        Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                        constructor.setAccessible(true);
                        NBTTagString nbtTagString = constructor.newInstance(ability.getId().toString());
                        nbtTagList.add(nbtTagString);
                    } catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                tag.set("abilities", nbtTagList);
                
                
                nmsItemStack.setTag(tag);
                itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
                
                ItemMeta itemMeta = itemStack.getItemMeta();
                
                itemMeta.setLore(customItem.generateLore());
                itemMeta.setDisplayName(ChatColor.RESET + customItem.getName());
                
                itemStack.setItemMeta(itemMeta);
            }else{
                tag.setBoolean("inUse", false);
                nmsItemStack.setTag(tag);
                itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
                
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<java.lang.String> lore = new ArrayList<>();
                
                lore.add("");
                lore.add(ChatColor.BOLD + Rarity.COMMON.fullName);
                
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
        if(!items.containsKey(internalId) && tag.getBoolean("inUse")){
            ItemMeta itemMeta = itemStack.getItemMeta();
    
            List<java.lang.String> lore = itemMeta.getLore();
            lore.add("");
            lore.add(ChatColor.DARK_RED + "Unrecognized");
            
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    public void clearItems(){
        items.clear();
    }
}
