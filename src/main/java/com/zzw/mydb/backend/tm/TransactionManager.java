package com.zzw.mydb.backend.tm;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

//没有任何事务时，
// XID 0 是一个超级事务（Super Transaction）。
// 当一些操作想在没有申请事务的情况下进行，那么可以将操作的
// XID 设置为 0。XID 为 0 的事务的状态永远是 committed。
public interface TransactionManager {
    long begin(); //开启一个事务
    void commit(long xid); //提交一个事务
    void abort(long xid); //取消一个事务(撤销 回滚)
    boolean isActive(long xid); //查询一个事务的状态是否是正在进行的状态
    boolean isCommitted(long xid); //查询一个事务的状态是否是已提交的状态
    boolean isAbort(long xid); //查询一个事务的状态是否是撤销的状态
    void  close(); //关闭tm
    public static TransactionManagerImpl  create(String path){
        return null;
    }

    public static  TransactionManagerImpl open(){
     return null;
    }
}
