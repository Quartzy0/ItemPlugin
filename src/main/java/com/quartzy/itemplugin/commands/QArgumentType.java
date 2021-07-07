package com.quartzy.itemplugin.commands;

import com.mojang.brigadier.arguments.ArgumentType;

public interface QArgumentType<T> extends ArgumentType<T>{
    String getSerializerId();
}
