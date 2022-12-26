package com.zzw.mydb.tm;

import com.zzw.mydb.backend.tm.TransactionManager;
import com.zzw.mydb.backend.tm.TransactionManagerImpl;
import org.junit.Test;

public class TestTM {
    @Test
    public void  test1(){
        TransactionManagerImpl tm = TransactionManager.create("D://tm");
        System.out.println(tm);
        //long tran1 = tm.begin();
        System.out.println(tm.isCommitted(0));
      //  System.out.println(tm.isCommitted(tran1));

    }
    @Test
    public void  test2(){
        TransactionManagerImpl tm = TransactionManager.open("D://tm");
    }



}
