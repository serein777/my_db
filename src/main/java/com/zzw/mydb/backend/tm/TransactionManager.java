package com.zzw.mydb.backend.tm;

import com.zzw.mydb.backend.common.Error;
import com.zzw.mydb.backend.utils.Panic;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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
    //创建新的xid文件生成 tm
    public static TransactionManagerImpl  create(String path){
        File f=new File(path+TransactionManagerImpl.XID_SUFFIX);
        try {
            if(!f.createNewFile()){
                    Panic.panic(Error.FileExistsException);
            }
        }
        catch (IOException e){
            Panic.panic(e);
        }
        if(!f.canRead()||!f.canWrite()){
            Panic.panic(Error.FileCannotWRException);
        }
        FileChannel fc=null;
        RandomAccessFile raf=null;
        try {
            raf=new RandomAccessFile(f,"rw");
            fc=raf.getChannel();
        }
        catch (IOException e){
            Panic.panic(e);
        }
        //写入xid头部
        ByteBuffer buf=ByteBuffer.wrap(new byte[TransactionManagerImpl.LEN_XID_HEADER_LENGTH]);
        try{
            fc.position(0);
            fc.write(buf);
        }
        catch (IOException e){
            Panic.panic(e);
        }
        //立即刷盘，防止数据丢失
        try{
            //参数为是否同步文件的元数据
            fc.force(false);
        }
        catch (IOException e){
            Panic.panic(e);
        }
        return new TransactionManagerImpl(raf,fc);
    }

    public static  TransactionManagerImpl open(String path){
        File f=new File(path+TransactionManagerImpl.XID_SUFFIX);
        if(!f.exists()){
            Panic.panic(Error.FileNotExistsException);
        }
        if(!f.canRead()||!f.canWrite()){
            Panic.panic(Error.FileCannotWRException);
        }
        FileChannel fc=null;
        RandomAccessFile raf=null;
        try {
            raf=new RandomAccessFile(f,"rw");
            fc=raf.getChannel();
        }
        catch (IOException e){
            Panic.panic(e);
        }
        return new TransactionManagerImpl(raf,fc);
    }
}
