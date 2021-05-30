package com.quartzy.itemplugin.listener;

import com.quartzy.itemplugin.ItemPlugin;
import com.quartzy.itemplugin.abilities.Ability;
import com.quartzy.itemplugin.items.ItemManager;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener{
    
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItemStack.getTag();
        if(tag==null)return;
        if(!tag.getBoolean("inUse"))return;
        if(!tag.hasKey("abilities"))return;
        NBTTagList abilities = tag.getList("abilities", CraftMagicNumbers.NBT.TAG_STRING);
        if(abilities.isEmpty())return;
        for(NBTBase abilityTag : abilities){
            NBTTagString nbtTagString = (NBTTagString) abilityTag;
            ItemManager itemManager = ItemPlugin.getItemManager();
            Ability ability = itemManager.getAbility(nbtTagString.asString());
            if(ability.getAction().isEquivalent(event.getAction())){
                ability.onAction(item, event.getPlayer());
            }
        }
    }
}
