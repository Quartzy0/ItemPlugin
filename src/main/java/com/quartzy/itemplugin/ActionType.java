package com.quartzy.itemplugin;

import org.bukkit.event.block.Action;

public enum ActionType{
        RIGHT_CLICK("RIGHT CLICK"), LEFT_CLICK("LEFT CLICK"), PASSIVE("PASSIVE");
        
        public final String description;
        
        public boolean isEquivalent(Action action){
            if(action==Action.LEFT_CLICK_AIR && this==LEFT_CLICK)return true;
            if(action==Action.LEFT_CLICK_BLOCK && this==LEFT_CLICK)return true;
            if(action==Action.RIGHT_CLICK_AIR && this==RIGHT_CLICK)return true;
            if(action==Action.RIGHT_CLICK_BLOCK && this==RIGHT_CLICK)return true;
            return false;
        }
        
        ActionType(String description){
            this.description = description;
        }
    }