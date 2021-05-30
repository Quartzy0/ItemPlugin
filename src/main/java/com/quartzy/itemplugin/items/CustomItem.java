package com.quartzy.itemplugin.items;

import com.quartzy.itemplugin.abilities.Ability;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class CustomItem{
    
    @Getter
    private Material material;
    @Getter
    private String name;
    @Getter
    private Rarity rarity;
    @Getter
    private String description;
    @Getter
    private Ability[] abilities;
    @Getter
    private MinecraftKey id;
    
    public CustomItem(Material material, java.lang.String name, Rarity rarity, String description, Ability[] ability, MinecraftKey id){
        this.material = material;
        this.name = name;
        this.rarity = rarity;
        this.description = description;
        this.abilities = ability;
        this.id = id;
    }
    
    /**
     * @return Lore. Every element in the list is a line
     */
    public List<java.lang.String> generateLore(){
        List<java.lang.String> lore = new ArrayList<>();
        for(java.lang.String s : description.split("\n")){
            lore.add(ChatColor.GRAY + s);
        }
        lore.add("");
        lore.add("");
        if(abilities != null && abilities.length!=0){
            for(Ability ability : abilities){
                lore.add(ChatColor.GOLD + "Item Ability: " + ability.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.BOLD + ability.getAction().description);
                for(java.lang.String s : ability.getDescription().split("\n")){
                    lore.add(ChatColor.GRAY + s);
                }
                lore.add("");
            }
        }
        lore.add(ChatColor.BOLD + rarity.fullName);
        return lore;
    }
}
