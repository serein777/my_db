package com.zzw.mydb.backend.common;

import com.zzw.mydb.common.Error;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 引用计数器缓存类
 */
public  abstract class AbstractCache<T>  {
    private Map<Long,T> cache;           //缓存中的资源
    private Map<Long,Integer>references;    //某资源被引用的次数
    private Map<Long,Boolean>getting;       //正在被某个线程获取的资源
    private int maxResource;               //最大缓存资源个数
    private int count;                     //当前缓存中资源个数
    ReentrantLock lock;
    public AbstractCache(){

    }
    public AbstractCache(int maxResource) {
        cache=new HashMap<>();
        references=new HashMap<>();
        getting=new HashMap<>();
        this.maxResource=maxResource;
        lock=new ReentrantLock();
    }

    protected T get(long key) throws Exception{
        while (true){
            lock.lock();
            //当前资源正在加载
            if(getting.containsKey(key)){
                lock.unlock();
                try{
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    continue;
                }
                continue;
            }

            //资源在缓存中，可以直接返回
            if(cache.containsKey(key)){
                T obj=cache.get(key);
                references.put(key,references.get(key)+1);
                lock.unlock();
                return  obj;
            }

            //尝试加载资源
            if(maxResource>0&&count==maxResource){
                lock.unlock();
                throw Error.CacheFullException;
            }
            count++;
            getting.put(key,true);
            lock.unlock();
            break;
        }
        T obj=null;
        try{
            obj=getForCache(key);
        }
        catch (Exception e){
            lock.lock();
            count--;
            getting.remove(key);
            lock.unlock();
            throw  e;
        }
        lock.lock();
        getting.remove(key);
        cache.put(key,obj);
        references.put(key,1);
        lock.lock();
        return  obj;
    }
    protected void  release(long key){
        lock.lock();
        try{
        int ref=references.get(key);
        if(ref-1==0){
            T obj=cache.get(key);
            releaseForCache(obj);
            references.remove(key);
            cache.remove(key);
            count--;
        }
        else{
            references.put(key,ref-1);
        }
            }
        finally {
            lock.unlock();
        }

    }
    //关闭缓存，强制回源所有资源
    protected void close(){
        lock.lock();
        Set<Long> set=cache.keySet();
        try {
            for (long key:set){
                T obj=cache.get(key);
                releaseForCache(obj);
                references.remove(key);
                cache.remove(key);
            }
            count=0;
        }
        finally {
            lock.lock();
        }

    }
    //数据不在缓存内时的获取行为
    protected abstract T getForCache(long key) throws Exception;

    //资源被驱逐时的写回行为
    protected abstract void releaseForCache(T obj);
}
