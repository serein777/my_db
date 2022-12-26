package com.zzw.mydb.backend.common;

/**
 * 异常枚举
 */
public class Error {
    //common
    public  static  final  Exception CacheFullException=new RuntimeException("Cache is full");
    public  static  final  Exception FileExistsException=new RuntimeException("File already exist");
    public  static  final  Exception FileNotExistsException=new RuntimeException("File doesn't exist");
    public  static  final  Exception FileCannotWRException=new RuntimeException("File can not read or write");

    //tm
    public  static  final  Exception BadXidFileException=new RuntimeException("Bad XID file");

    //parser
    public  static  final  Exception InvalidCommandException=new RuntimeException("Invalid Command");
    public  static  final  Exception TableNoIndexException=new RuntimeException("Invalid Command");
}
