package com.quartzy.itemplugin.items;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.abilities.Ability;
import com.quartzy.itemplugin.abilities.TestAbility;
import com.quartzy.itemplugin.blocks.CustomBlock;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemManager{
    
    private HashMap<java.lang.String, CustomItem> items = new HashMap<>();
    private HashMap<java.lang.String, Ability> abilities = new HashMap<>();
    
    public ItemManager(){
        items.put("WORKBENCH_ITEM", new CustomItem(Material.CRAFTING_TABLE, "Workbench", Rarity.COMMON, "Epic crafty boi", null, "WORKBENCH_ITEM"));
        
        TestAbility testAbility = new TestAbility(5);
        items.put("ASPECT_OF_THE_END", new CustomItem(Material.DIAMOND_SWORD, "Aspect of the end", Rarity.EPIC, "Epic sword that do da whooosh", new Ability[]{testAbility}, "ASPECT_OF_THE_END"));
        abilities.put(testAbility.getId(), testAbility);
    }
    
    public Ability getAbility(java.lang.String id){
        return abilities.get(id);
    }
    
    public void loadItems(){
        List<YamlConfiguration> itemConfiguration = ItemPlugin.getINSTANCE().getItemConfiguration();
        for(YamlConfiguration yamlConfiguration : itemConfiguration){
            Set<java.lang.String> keys = yamlConfiguration.getKeys(false);
            for(java.lang.String key : keys){
                Material material = Material.valueOf(yamlConfiguration.getString(key + ".material"));
                java.lang.String name = yamlConfiguration.getString(key + ".name");
                java.lang.String description = yamlConfiguration.getString(key + ".description");
                Rarity rarity = Rarity.valueOf(yamlConfiguration.getString(key + ".rarity"));
                
            }
        }
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
        tag.setString("internalId", item.getId().toUpperCase());
        if(item.getAbilities()!=null && item.getAbilities().length!=0){
            NBTTagList nbtTagList = new NBTTagList();
    
            for(Ability ability : item.getAbilities()){
                try{
                    Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                    constructor.setAccessible(true);
                    NBTTagString nbtTagString = constructor.newInstance(ability.getId());
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
        CustomItem customItem = items.get(id.toUpperCase());
        Material material = customItem==null ? Material.getMaterial(id.toUpperCase()) : customItem.getMaterial();
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
        tag.setString("internalId", id.toUpperCase());
        if(customItem!=null && customItem.getAbilities()!=null && customItem.getAbilities().length!=0){
            NBTTagList nbtTagList = new NBTTagList();
    
            for(Ability ability : customItem.getAbilities()){
                try{
                    Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                    constructor.setAccessible(true);
                    NBTTagString nbtTagString = constructor.newInstance(ability.getId());
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
        java.lang.String pluginVersion = tag.getString("pluginVersion");
        java.lang.String internalId = tag.getString("internalId");
        if(pluginVersion==null || pluginVersion.isEmpty() || internalId==null || internalId.isEmpty()){
            tag.setString("pluginVersion", ItemPlugin.pluginVersion);
            tag.setString("internalId", itemStack.getType().name());
            if(items.containsKey(itemStack.getType().name())){
                tag.setBoolean("inUse", true);
    
                CustomItem customItem = items.get(itemStack.getType().name());
                NBTTagList nbtTagList = new NBTTagList();
                
                for(Ability ability : customItem.getAbilities()){
                    try{
                        Constructor<NBTTagString> constructor = NBTTagString.class.getDeclaredConstructor(java.lang.String.class);
                        constructor.setAccessible(true);
                        NBTTagString nbtTagString = constructor.newInstance(ability.getId());
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
}
