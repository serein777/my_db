package com.zzw.mydb.backend.dm.pageCache;

import com.zzw.mydb.backend.common.AbstractCache;
import com.zzw.mydb.backend.dm.page.PageImpl;
import com.zzw.mydb.common.Error;
import com.zzw.mydb.backend.dm.page.Page;
import com.zzw.mydb.backend.utils.Panic;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PageCacheImpl extends AbstractCache<Page> implements PageCache{
    private static  final int MEM_MIN_LIM =10;

    public  static  final String DB_SUFFIX=".db"; //文件后缀
    private RandomAccessFile file;
    private FileChannel fc;
    private Lock fileLock;

    private AtomicInteger pageNumbers; //页面数量
    PageCacheImpl (RandomAccessFile file,FileChannel fileChannel,int maxResource){
            super(maxResource);
            if(maxResource<MEM_MIN_LIM){
                Panic.panic(Error.MemTooSmallException);
            }
            long length=0;
            try{
                length=file.length();
            }
            catch (IOException e){
                Panic.panic(e);
            }
            this.file=file;
            this.fc=fileChannel;
            this.fileLock=new ReentrantLock();
            this.pageNumbers=new AtomicInteger((int)length/PAGE_SIZE);

    }

    /**
     * 根据pageNumber从数据库文件中读取页数据，并包裹成Page
     */
    @Override
    protected Page getForCache(long key) throws Exception {
            int pgno=(int) key;
            long offset=PageCacheImpl.pageOffset(pgno);
            ByteBuffer buf=ByteBuffer.allocate(PAGE_SIZE);
            fileLock.lock();
            try {
                fc.position(offset);
                fc.read(buf);
            }
            catch (IOException e){
                Panic.panic(e);
            }
            fileLock.unlock();
            return new PageImpl(pgno,buf.array(),this);
    }

    @Override
    protected void releaseForCache(Page pg) {
            if(pg.isDirty()){
                flushPage(pg);
                pg.setDirty(false);
            }
    }

    @Override
    public int newPage(byte[] initData) {
       int pgno=pageNumbers.incrementAndGet();
       Page page=new PageImpl(pgno,initData,null);
       flush(page);
       return pgno;
    }
    //从缓存池里获取某个页面
    @Override
    public Page getPage(int pgno) throws Exception {
        return get((long)pgno);
    }

    @Override
    public void close() {
        super.close();
        try {
            fc.close();
            file.close();
        } catch (IOException e) {
            Panic.panic(e);
        }
    }

    @Override
    public void release(Page page) {
        release(page.getPageNumber());
    }

    @Override
    public void truncateByBgno(int maxPgno) {
        long size=pageOffset(maxPgno+1);
        try{
            file.setLength(size);
        }
        catch (IOException e){
            Panic.panic(e);
        }
        pageNumbers.set(maxPgno);
    }

    @Override
    public void flushPage(Page page) {
        flush(page);
    }
    private static long pageOffset(int pgno) {
        return (pgno-1) * PAGE_SIZE;
    }

    public int getPageNumber(){
        return  pageNumbers.intValue();
    }

    private void flush(Page pg) {
        int pgno = pg.getPageNumber();
        long offset = pageOffset(pgno);

        fileLock.lock();
        try {
            ByteBuffer buf = ByteBuffer.wrap(pg.getData());
            fc.position(offset);
            fc.write(buf);
            fc.force(false);
        } catch(IOException e) {
            Panic.panic(e);
        } finally {
            fileLock.unlock();
        }
    }
}
