package com.zzw.mydb.backend.tm;

import com.zzw.mydb.backend.common.Error;
import com.zzw.mydb.backend.utils.Panic;
import com.zzw.mydb.backend.utils.Parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionManagerImpl implements TransactionManager{
    //XID文件头长度，用来保存该XID文件中事务个数
    static  final int LEN_XID_HEADER_LENGTH=8;
    //每个事务的占用长度,用来保存每个事务当前状态
    private static  final int XID_FIELD_SIZE=1;
    //事务的三种状态
    private static final byte FIELD_TRAN_ACTIVE=0;
    private static final byte FIELD_TRAN_COMMITTED=1;
    private static final byte FIELD_TRAN_ABORT=2;
    //超级事务,一直为committed状态
    public  static  final  long SUPER_XID=0;
    //XID文件后缀
    static  final String XID_SUFFIX=".xid";
    private RandomAccessFile file;
    private FileChannel fc;
    private long xidCounter;
    private Lock counterLock;

    public TransactionManagerImpl() {
    }

    public TransactionManagerImpl(RandomAccessFile raf, FileChannel fc) {
        this.file = raf;
        this.fc = fc;
        counterLock=new ReentrantLock();
        checkXIDCounter();
    }
    /*
     *检查XID文件是否合法
     * 读取XID_FILE_HEADER中的xidCounter（事务个数）.根据它计算文件的理论长度，对比实际长度
     */
    private void checkXIDCounter() {
        long fileLen=0;
        try {
            fileLen=file.length();
        } catch (IOException e) {
            Panic.panic(e);
        }
        //文件长度小于8
        if(fileLen<LEN_XID_HEADER_LENGTH){
            Panic.panic(Error.BadXidFileException);
        }
        ByteBuffer buf=ByteBuffer.allocate(LEN_XID_HEADER_LENGTH);
        try{
        fc.position(0);
        fc.read(buf);
        }
        catch (IOException e){
            Panic.panic(e);
        }
        this.xidCounter= Parser.byte2Long(buf.array());
        long end= getXidPosition(xidCounter+1);
        if(fileLen!=end){
            Panic.panic(Error.BadXidFileException);
        }
    }

    private long getXidPosition(long xid) {
        return LEN_XID_HEADER_LENGTH+(xid-1)*XID_FIELD_SIZE;
    }

    @Override
    public long begin() {
        counterLock.lock();
        try{
            long xid=xidCounter+1;
            updateXid(xid,FIELD_TRAN_ACTIVE);
            incrXidCounter();
            return  xid;
        }
        finally {
            counterLock.unlock();
        }
    }

    //xid自增1,并同步到XID文件头
    private void incrXidCounter() {
        ByteBuffer buf=ByteBuffer.wrap(Parser.long2Byte(this.xidCounter));
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
    }

    private void updateXid(long xid, byte status) {
        long offset=getXidPosition(xid);
        byte[] temp=new byte[XID_FIELD_SIZE];
        temp[0]=status;
        try {
            fc.position(offset);
            ByteBuffer buf = ByteBuffer.wrap(temp);
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
    }
    //检查xid事务是否为status状态
    public  boolean checkXIDStatus(long xid,byte status){
        long offset=getXidPosition(xid);
        byte[]temp=new byte[XID_FIELD_SIZE];
        try{
            fc.position(offset);
            fc.read(ByteBuffer.wrap(temp));
        }
        catch (IOException e){
            Panic.panic(e);
        }
        return temp[0]==status;
    }


    @Override
    public void commit(long xid) {
      updateXid(xid,FIELD_TRAN_COMMITTED);
    }

    @Override
    public void abort(long xid) {
        updateXid(xid,FIELD_TRAN_ABORT);
    }

    @Override
    public boolean isActive(long xid) {
        if(xid==SUPER_XID) return false;
        return checkXIDStatus(xid,FIELD_TRAN_ACTIVE);
    }

    @Override
    public boolean isCommitted(long xid) {
        if(xid==SUPER_XID) return false;
        return checkXIDStatus(xid,FIELD_TRAN_COMMITTED);
    }

    @Override
    public boolean isAbort(long xid) {
        if(xid==SUPER_XID) return false;
        return checkXIDStatus(xid,FIELD_TRAN_ABORT);
    }

    @Override
    public void close() {
        try {
            fc.close();
            file.close();
        }
        catch (IOException e){
            Panic.panic(e);
        }
    }
}
