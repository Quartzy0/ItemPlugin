package com.quartzy.itemplugin;

import com.quartzy.itemplugin.blocks.BlockManager;
import com.quartzy.itemplugin.items.ItemManager;

public interface ItemPluginHandler{
    void addItems(ItemManager itemManager);
    
    void addBlocks(BlockManager blockManager);
    
    void addRecipes();
}
