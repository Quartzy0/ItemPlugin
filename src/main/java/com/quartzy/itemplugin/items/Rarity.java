package com.quartzy.itemplugin.items;

import org.bukkit.ChatColor;

public enum Rarity{
    COMMON(ChatColor.WHITE + "COMMON"), UNCOMMON(ChatColor.GREEN + "UNCOMMON"),
    RARE(ChatColor.BLUE + "RARE"), EPIC(ChatColor.DARK_PURPLE + "EPIC"),
    LEGENDARY(ChatColor.GOLD + "LEGENDARY"), MYTHIC(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "MYTHIC" + ChatColor.MAGIC + "a"),
    SUPREME(ChatColor.RED + "" + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.RED + "SUPREME" + ChatColor.MAGIC + "a"), SUPER_SUPREME(ChatColor.RED + "" + ChatColor.MAGIC + "aa " + ChatColor.RESET + ChatColor.RED + "SUPER SUPREME" + ChatColor.MAGIC + " aa");
    
    public final String fullName;
    
    Rarity(String fullName){
        this.fullName = fullName;
    }
}
