package com.riojasonc.sphere.global;

public class Constant {
    public static final int Success = 0;
    public static final int Failure = 1;

    public class ERROR{
        public class LOGIN{
            public static final int FRONT = 2;//id, name错误
            public static final int PASSWORD = 3;//密码错误
        }
        public class SIGNUP{
            public static final int NAME = 4;//name不符合要求
            public static final int PASSWORD = 5;//password不符合要求
            public static final int CDKEY = 6;//cdkey错误
            public static final int CDKEYUSED = 7;//cdkey已经被使用
        }
        public class UTIL{
            public static final int MYSQL = -1;//SQL出现问题
        }
    }

    public Constant(){

    }
}
