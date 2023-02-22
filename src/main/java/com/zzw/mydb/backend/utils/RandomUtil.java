package com.zzw.mydb.backend.utils;
import java.security.SecureRandom;
import java.util.Random;
/*
        产生一个 length长度的数组
 */
public class RandomUtil {
        public static byte[] randomBytes(int length) {
            Random r = new SecureRandom();
            byte[] buf = new byte[length];
            r.nextBytes(buf);
            return buf;
        }
}
