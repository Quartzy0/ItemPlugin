package com.quartzy.itemplugin.commands;

import com.google.gson.JsonObject;
import net.minecraft.server.v1_16_R3.ArgumentSerializer;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.PacketDataSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Supplier;

public class QPacketSerializer<T extends QArgumentType<?>> implements ArgumentSerializer<T>{
    private final Supplier<T> a;
    
    public QPacketSerializer(Supplier<T> var0) {
        this.a = var0;
    }
    
    private void removeBytesFromEnd(PacketDataSerializer bf, int n) {
        bf.writerIndex(bf.writerIndex()-n);
    }
    
    //Serialize
    @Override
    public void a(T t, PacketDataSerializer packetDataSerializer){
        int a = PacketDataSerializer.a(32);
        removeBytesFromEnd(packetDataSerializer, 32 + a);
        
        MinecraftKey minecraftkey = new MinecraftKey(t.getSerializerId());
        int first = packetDataSerializer.writerIndex();
        packetDataSerializer.a(minecraftkey);
        packetDataSerializer.readerIndex(first);
        byte[] bytes = new byte[packetDataSerializer.writerIndex()-first];
        packetDataSerializer.readBytes(bytes);
        System.out.println(Arrays.toString(bytes));
        System.out.println(bytes.length);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
        packetDataSerializer.readerIndex(0);
        
    }
    
    //Deserialize
    @Override
    public T b(PacketDataSerializer packetDataSerializer){
        return a.get();
    }
    
    //:(
    @Override
    public void a(T t, JsonObject jsonObject){
    
    }
}
