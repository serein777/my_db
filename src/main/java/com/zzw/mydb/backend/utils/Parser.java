package com.zzw.mydb.backend.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 用于字节和short、int、long String的互相转换
 */
public class Parser {

    public static byte[] short2Byte(short value){
        return ByteBuffer.allocate(Short.SIZE/Byte.SIZE).putShort(value).array();
    }

    public  static  short byte2Short(byte[] buf){
        ByteBuffer buffer=ByteBuffer.wrap(buf,0,2);
        return  buffer.getShort();
    }

    public static byte[] int2Byte(int value){
        return ByteBuffer.allocate(Integer.SIZE/Byte.SIZE).putInt(value).array();
    }

    public  static  int byte2Int(byte[] buf){
        ByteBuffer buffer=ByteBuffer.wrap(buf,0,4);
        return  buffer.getInt();
    }

    public static byte[] long2Byte(long value){
        return ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(value).array();
    }

    public  static  long byte2Long(byte[] buf){
        ByteBuffer buffer=ByteBuffer.wrap(buf,0,8);
        return  buffer.getLong();
    }

}
