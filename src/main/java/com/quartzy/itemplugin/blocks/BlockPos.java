package com.quartzy.itemplugin.blocks;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.MathHelper;

import java.util.Objects;

public class BlockPos{
    @Getter
    @Setter
    private int x, y, z;
    
//    private static final int[] multiplyDeBruijnBitPosition = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
//
//    public static int roundUpToPowerOfTwo(int value)
//    {
//        int i = value - 1;
//        i = i | i >> 1;
//        i = i | i >> 2;
//        i = i | i >> 4;
//        i = i | i >> 8;
//        i = i | i >> 16;
//        return i + 1;
//    }
//
//    private static boolean isPowerOfTwo(int value)
//    {
//        return value != 0 && (value & value - 1) == 0;
//    }
//
//    private static int calculateLogBaseTwoDeBruijn(int value)
//    {
//        value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
//        return multiplyDeBruijnBitPosition[(int)((long)value * 125613361L >> 27) & 31];
//    }
//
//    public static int calculateLogBaseTwo(int value)
//    {
//        return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
//    }
    
    private static final int NUM_X_BITS = 26;
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = 0 + NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
    
    public BlockPos(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public long toLong(){
        return ((long) this.getX() & X_MASK) << X_SHIFT | ((long) this.getY() & Y_MASK) << Y_SHIFT | ((long) this.getZ() & Z_MASK) << 0;
    }
    
    public static BlockPos fromLong(long serialized)
    {
        int x = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int y = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int z = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(x, y, z);
    }
    
    @Override
    public String toString(){
        return "BlockPos{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        BlockPos blockPos = (BlockPos) o;
        return x == blockPos.x && y == blockPos.y && z == blockPos.z;
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(x, y, z);
    }
}
