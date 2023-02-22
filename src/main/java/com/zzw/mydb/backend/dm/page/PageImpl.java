package com.zzw.mydb.backend.dm.page;

import com.zzw.mydb.backend.dm.pageCache.PageCache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PageImpl implements Page{
    int pageNumber;  //页面号
    private  byte[] data; //数据
    private  boolean dirty; //是否为脏页
    private Lock lock;

    PageCache pc; //页面缓存
    public PageImpl(){

    }
    public PageImpl(int pageNumber, byte[]data, PageCache pc){
            this.pageNumber=pageNumber;
            this.data=data;
            this.pc=pc;
            lock=new ReentrantLock();
    }
    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public void release() {
        pc.release(this);
    }

    @Override
    public void setDirty(boolean dirty) {
            this.dirty=dirty;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public byte[] getData() {
        return this.data;
    }
}
