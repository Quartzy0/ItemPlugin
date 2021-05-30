package com.quartzy.itemplugin;

import com.quartzy.itemplugin.abilities.Ability;
import com.quartzy.itemplugin.abilities.TestAbility;
import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.blocks.CustomBlockImpl;
import com.quartzy.itemplugin.commands.*;
import com.quartzy.itemplugin.inv.InventoryManager;
import com.quartzy.itemplugin.items.CustomItem;
import com.quartzy.itemplugin.items.ItemManager;
import com.quartzy.itemplugin.items.Rarity;
import com.quartzy.itemplugin.listener.AbilityListener;
import com.quartzy.itemplugin.listener.BlockListener;
import com.quartzy.itemplugin.listener.InventoryListener;
import com.quartzy.itemplugin.listener.ItemValidationListener;
import com.quartzy.itemplugin.util.RecipeHelper;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class ItemPlugin extends JavaPlugin implements ItemPluginHandler{
    
    public static final String pluginVersion = "1.0-SNAPSHOT";
    
    @Getter
    private static ItemPlugin INSTANCE;
    
    private List<ItemPluginHandler> handlers = new ArrayList<>();
    
    private ItemManager itemManager;
    private BlockManager blockManager;
    private InventoryManager inventoryManager;
    private QCommandHandler commandHandler;
    
    @Getter
    private YamlConfiguration masterConfig;
    @Getter
    private List<YamlConfiguration> itemConfiguration;
    @Getter
    private List<YamlConfiguration> blockConfiguration;
    
    private void resolveConfigurations(File file, List<YamlConfiguration> config){
        if(file==null)return;
        if(!file.isDirectory()){
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try{
                yamlConfiguration.load(file);
            } catch(IOException | InvalidConfigurationException e){
                e.printStackTrace();
            }
            config.add(yamlConfiguration);
        }else{
            for(File listFile : file.listFiles()){
                resolveConfigurations(listFile, config);
            }
        }
    }
    
    private void loadItemsFromConfig(){
        for(YamlConfiguration yamlConfiguration : itemConfiguration){
            Set<String> keys = yamlConfiguration.getKeys(false);
            for(String key : keys){
                ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
                Material mat = Material.getMaterial(ifNull(configurationSection.getString("material"), "AIR").toUpperCase());
                if(mat.isAir()){
                    Bukkit.getLogger().warning("Item " + key + " has an invalid material (material cannot be empty or air)");
                    continue;
                }
                String name = ifNull(configurationSection.getString("name"), key);
                Rarity rarity = Rarity.valueOf(ifNull(configurationSection.getString("rarity"), "COMMON").toUpperCase());
                String description = configurationSection.getString("description");
                List<String> abilitiesS = configurationSection.getStringList("abilities");
                if(abilitiesS.size() == 0){
                    String pos1 = configurationSection.getString("abilities");
                    String pos2 = configurationSection.getString("ability");
                    if(pos1!=null){
                        abilitiesS.add(pos1);
                    }else if(pos2!=null){
                        abilitiesS.add(pos2);
                    }
                }
                List<Ability> abilities = new ArrayList<>();
                for(int i = 0; i < abilitiesS.size(); i++){
                    Ability ability = itemManager.getAbility(abilitiesS.get(i));
                    if(ability != null){
                        abilities.add(ability);
                    }
                }
                CustomItem newItem = new CustomItem(mat, name, rarity, description, abilities.toArray(new Ability[0]), new MinecraftKey(key));
                if(itemManager.getItemById(key) != null){
                    Bukkit.getLogger().warning("Item " + key + " already exists and will be overwritten");
                }
                itemManager.addItem(newItem);
            }
        }
    }
    
    private void loadBlocksFromConfig(){
        for(YamlConfiguration yamlConfiguration : blockConfiguration){
            Set<String> keys = yamlConfiguration.getKeys(false);
            for(String key : keys){
                ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
                Material mat = Material.getMaterial(ifNull(configurationSection.getString("material"), "AIR").toUpperCase());
                if(mat.isAir()){
                    Bukkit.getLogger().warning("Block " + key + " has an invalid material (material cannot be empty or air)");
                    continue;
                }
                if(!mat.isBlock()){
                    Bukkit.getLogger().warning("Block " + key + " has an invalid material (material must be a block)");
                    continue;
                }
                String blockName = ifNull(configurationSection.getString("name"), key);
                MinecraftKey blockItem = new MinecraftKey(ifNull(configurationSection.getString("block_item"), mat.getKey().toString()));
                String description = configurationSection.getString("description");
                Rarity rarity = Rarity.valueOf(ifNull(configurationSection.getString("rarity"), "COMMON").toUpperCase());
                String _action_type = configurationSection.getString("action_type");
                ActionType actionType = null;
                if(_action_type!=null) actionType = ActionType.valueOf(_action_type.toUpperCase());
                String commandToExecute = configurationSection.getString("command");
                boolean asPlayer = configurationSection.getBoolean("as_player");
                
                CustomBlockImpl customBlock = new CustomBlockImpl(blockItem, blockName, description, new MinecraftKey(key), mat, rarity, actionType, commandToExecute, asPlayer);
                if(blockManager.getBlockById(key)!=null){
                    Bukkit.getLogger().warning("Block with id " + key + " already exists and will be overwritten");
                }
                blockManager.addBlock(customBlock);
            }
        }
    }
    
    public void reloadItems(){
        itemManager.clearItems();
    
        //Load all items from addons' handlers
        for(ItemPluginHandler handler : handlers){
            handler.addItems(itemManager);
        }
    
        //Load all items from the config files and warn user if a duplicate exists
        loadItemsFromConfig();
    }
    
    public void reloadBlocks(){
        blockManager.clearBlocks();
        
        //Load all blocks from addons' handlers
        for(ItemPluginHandler handler : handlers){
            handler.addBlocks(blockManager);
        }
        
        loadBlocksFromConfig();
    }
    
    
    @Override
    public void onLoad(){
        INSTANCE = this;
        inventoryManager = new InventoryManager();
        itemManager = new ItemManager();
        blockManager = new BlockManager();
        for(World world : getServer().getWorlds()){
            blockManager.loadData(world);
        }
        RecipeHelper.init();
    }
    
    @Override
    public void onEnable(){
        
        //Initialise the config files
        Path dataFolder = getDataFolder().toPath();
        File itemConfigurationFolder = dataFolder.resolve("item_config").toFile();
        File blockConfigurationFolder = dataFolder.resolve("block_config").toFile();
        File masterConfigFile = dataFolder.resolve("config.yml").toFile();
        masterConfig = new YamlConfiguration();
        try{
            if(!itemConfigurationFolder.exists()){
                itemConfigurationFolder.mkdirs();
            }
            if(!blockConfigurationFolder.exists()){
                blockConfigurationFolder.mkdirs();
            }
            itemConfiguration = new ArrayList<>();
            blockConfiguration = new ArrayList<>();
            resolveConfigurations(itemConfigurationFolder, itemConfiguration);
            resolveConfigurations(blockConfigurationFolder, blockConfiguration);
            if(!masterConfigFile.exists()){
                dataFolder.toFile().mkdirs();
                masterConfigFile.createNewFile();
            }
            masterConfig.load(masterConfigFile);
        } catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
        //Add self as handler to add custom blocks
        addHandler(this);
    
        //Register listeners
        getServer().getPluginManager().registerEvents(new ItemValidationListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        
        //Register commands
        getCommand("testcommand").setExecutor(new TestCommand());
        getCommand("refresh").setExecutor(new RefreshCommand());
    
        commandHandler = new QCommandHandler();
        commandHandler.addCommand(new GiveItemCommand());
    
        commandHandler.init(MinecraftServer.getServer().getCommandDispatcher());
        
        Bukkit.getServer().getLogger().info("Item Plugin enabled");
        
        //Load items after all plugins have been initialized
        Bukkit.getScheduler().runTaskLater(this, () -> {
            //Load all items
            reloadItems();
            //Load all blocks
            reloadBlocks();
            //Load all recipes
            reloadRecipes();
        }, 1L);
    }
    
    public void reloadRecipes(){
        RecipeHelper.init();
    
        for(ItemPluginHandler handler : handlers){
            handler.addRecipes();
        }
    }
    
    
    @Override
    public void onDisable(){
        blockManager.saveData();
        
        Bukkit.getServer().getLogger().info("Item Plugin disabled");
    }
    
    public static ItemManager getItemManager(){
        return INSTANCE.itemManager;
    }
    
    public static BlockManager getBlockManager(){
        return INSTANCE.blockManager;
    }
    
    public static InventoryManager getInventoryManager(){
        return INSTANCE.inventoryManager;
    }
    
    public static void addHandler(@NonNull ItemPluginHandler handler){
        INSTANCE.handlers.add(handler);
    }
    
    private static <T> T ifNull(T in, T def){
        return in==null ? def : in;
    }
    
    @Override
    public void addItems(ItemManager itemManager){
        TestAbility testAbility = new TestAbility(5);
        itemManager.addItem(new CustomItem(Material.DIAMOND_SWORD, "Aspect of the end", Rarity.EPIC, "Epic sword that do da whooosh", new Ability[]{testAbility}, new MinecraftKey("itemplugin:aspect_of_the_end")));
        itemManager.addAbility(testAbility);
        itemManager.addItem(new CustomItem(Material.DIAMOND, "Special diamond", Rarity.RARE, "Rare shiny diamond", null, new MinecraftKey("itemplugin:special_diamond")));
    }
    
    @Override
    public void addBlocks(BlockManager blockManager){
    }
    
    @Override
    public void addRecipes(){
        HashMap<Character, RecipeHelper.MaterialChoice> charMap = new HashMap<>();
        charMap.put('d', new RecipeHelper.MaterialChoice("itemplugin:special_diamond"));
        charMap.put('s', new RecipeHelper.MaterialChoice("minecraft:stick"));
    
        ItemStack aspect_of_the_end = itemManager.createItem("itemplugin:aspect_of_the_end", 1);
        RecipeHelper.addShapedRecipe(aspect_of_the_end, charMap, "d", "d", "s");
    
        List<RecipeHelper.MaterialChoice> ing = new ArrayList<>();
        ing.add(new RecipeHelper.MaterialChoice("itemplugin:special_diamond"));
        RecipeHelper.addShapelessRecipe(ing, itemManager.createItem("minecraft:diamond", 64));
    }
}
