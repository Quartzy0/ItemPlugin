package com.quartzy.itemplugin;

import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.commands.GiveItemCommand;
import com.quartzy.itemplugin.commands.TestCommand;
import com.quartzy.itemplugin.inv.InventoryManager;
import com.quartzy.itemplugin.items.ItemManager;
import com.quartzy.itemplugin.listener.AbilityListener;
import com.quartzy.itemplugin.listener.BlockListener;
import com.quartzy.itemplugin.listener.InventoryListener;
import com.quartzy.itemplugin.listener.ItemValidationListener;
import com.quartzy.itemplugin.util.RecipeHelper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ItemPlugin extends JavaPlugin{
    
    public static final String pluginVersion = "1.0-SNAPSHOT";
    
    @Getter
    private static ItemPlugin INSTANCE;
    
    @Getter
    private ItemManager itemManager;
    @Getter
    private BlockManager blockManager;
    @Getter
    private InventoryManager inventoryManager;
    
    @Getter
    private YamlConfiguration masterConfig;
    @Getter
    private List<YamlConfiguration> itemConfiguration;
    
    private void resolveItemConfigurations(File file){
        if(file==null)return;
        if(!file.isDirectory()){
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try{
                yamlConfiguration.load(file);
            } catch(IOException | InvalidConfigurationException e){
                e.printStackTrace();
            }
            itemConfiguration.add(yamlConfiguration);
        }else{
            for(File listFile : file.listFiles()){
                resolveItemConfigurations(listFile);
            }
        }
    }
    
    @Override
    public void onEnable(){
        INSTANCE = this;
        RecipeHelper.init();
        inventoryManager = new InventoryManager();
        itemManager = new ItemManager();
        blockManager = new BlockManager();
        for(World world : getServer().getWorlds()){
            blockManager.loadData(world);
        }
        
        //Initialise the config files
        Path dataFolder = getDataFolder().toPath();
        File itemConfigurationFolder = dataFolder.resolve("item_config").toFile();
        File masterConfigFile = dataFolder.resolve("config.yml").toFile();
        masterConfig = new YamlConfiguration();
        try{
            if(!itemConfigurationFolder.exists()){
                itemConfigurationFolder.mkdirs();
                itemConfiguration = new ArrayList<>();
                resolveItemConfigurations(itemConfigurationFolder);
            }
            if(!masterConfigFile.exists()){
                dataFolder.toFile().mkdirs();
                masterConfigFile.createNewFile();
            }
            masterConfig.load(masterConfigFile);
        } catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    
        //Register listeners
        getServer().getPluginManager().registerEvents(new ItemValidationListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        
        //Register commands
        getCommand("testcommand").setExecutor(new TestCommand());
        getCommand("gi").setExecutor(new GiveItemCommand());
        
        Bukkit.getServer().getLogger().info("Item Plugin enabled");
    }
    
    @Override
    public void onDisable(){
        blockManager.saveData();
        
        Bukkit.getServer().getLogger().info("Item Plugin disabled");
    }
}
