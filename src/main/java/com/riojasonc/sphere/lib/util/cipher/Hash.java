package com.riojasonc.sphere.lib.util.cipher;

public class Hash {
    /*
    * RS算法哈希
    * @param str 字符串
    * @return int
     */
    public static int RSHash(String str) {
        int b = 378551;
        int a = 63689;
        int hash = 0;
        for(int i = 0; i < str.length(); i++) {
            hash = hash * a  + str.charAt(i);
            a = a * b;
        }
        return (hash & 0x7FFFFFFF);
    }
}
