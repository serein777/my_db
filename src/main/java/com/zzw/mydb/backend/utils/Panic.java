package com.zzw.mydb.backend.utils;

/**
 * 数据库后端异常处理类
 */
public class Panic {
    public  static  void panic(Exception err){
        err.printStackTrace();
        //遇到无法处理的错误，虚拟机退出
        System.exit(1);
    }
}
