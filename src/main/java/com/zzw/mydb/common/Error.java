package com.zzw.mydb.common;

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

    //dm
    public static final Exception BadLogFileException = new RuntimeException("Bad log file!");
    public static final Exception MemTooSmallException = new RuntimeException("Memory too small!");
    public static final Exception DataTooLargeException = new RuntimeException("Data too large!");
    public static final Exception DatabaseBusyException = new RuntimeException("Database is busy!");


    //parser
    public  static  final  Exception InvalidCommandException=new RuntimeException("Invalid Command");
    public  static  final  Exception TableNoIndexException=new RuntimeException("Invalid Command");
}
