package com.zzw.mydb.utils;

import com.zzw.mydb.backend.utils.Panic;
import com.zzw.mydb.common.Error;
import org.junit.Test;

import java.nio.ByteBuffer;

public class PanicTest {
    @Test
    public void  testPanic(){
       Panic.panic(Error.BadXidFileException);
    }
    @Test
    public  void  testBuffer(){

            System.out.println("----------Test allocate--------");
            System.out.println("before alocate:"
                    + Runtime.getRuntime().freeMemory());

            // 如果分配的内存过小，调用Runtime.getRuntime().freeMemory()大小不会变化？
            // 要超过多少内存大小JVM才能感觉到？
            ByteBuffer buffer = ByteBuffer.allocate(102400);
            System.out.println("buffer = " + buffer);

            System.out.println("after alocate:"
                    + Runtime.getRuntime().freeMemory());

            // 这部分直接用的系统内存，所以对JVM的内存没有影响
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(102400);
            System.out.println("directBuffer = " + directBuffer);
            System.out.println("after direct alocate:"
                    + Runtime.getRuntime().freeMemory());

            System.out.println("----------Test wrap--------");
            byte[] bytes = new byte[32];
            buffer = ByteBuffer.wrap(bytes);
            System.out.println(buffer);

            buffer = ByteBuffer.wrap(bytes, 10, 10);
            System.out.println(buffer);
        }

}
