package com.zzw.mydb.backend.dm.pageCache;

import com.zzw.mydb.backend.common.AbstractCache;
import com.zzw.mydb.backend.dm.page.Page;
import com.zzw.mydb.backend.utils.Panic;
import com.zzw.mydb.common.Error;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public interface PageCache   {
    public static  final int PAGE_SIZE=1<<13; //页面大小 8k
    int newPage(byte[] initData);
    Page getPage(int pgno) throws Exception;
    void close();
    void release(Page page);
    void truncateByBgno(int maxPgno);
    int getPageNumber();
    void flushPage(Page page);

    public  static  PageCacheImpl create(String path,long memory) {
        File file=new File(path+PageCacheImpl.DB_SUFFIX);
        try {
            if(!file.createNewFile()){

            }
        }
        catch (Exception e){
            Panic.panic(Error.FileExistsException);
        }
        if(!file.canRead()||!file.canWrite()){
            Panic.panic(Error.FileCannotWRException);
        }
        FileChannel fc=null;
        RandomAccessFile raf=null;
        try{
            raf=new RandomAccessFile(file,"rw");
            fc=raf.getChannel();

        }
        catch (FileNotFoundException e){
            Panic.panic(e);
        }
        return  new PageCacheImpl(raf,fc,(int)memory/PAGE_SIZE);
    }

    public  static  PageCacheImpl open(String path,long memory){
        File f = new File(path+PageCacheImpl.DB_SUFFIX);
        if(!f.exists()) {
            Panic.panic(Error.FileNotExistsException);
        }
        if(!f.canRead() || !f.canWrite()) {
            Panic.panic(Error.FileCannotWRException);
        }

        FileChannel fc = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
            fc = raf.getChannel();
        } catch (FileNotFoundException e) {
            Panic.panic(e);
        }
        return new PageCacheImpl(raf, fc, (int)memory/PAGE_SIZE);
    }
}
