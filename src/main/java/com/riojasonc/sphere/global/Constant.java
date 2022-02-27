package com.riojasonc.sphere.global;

public class Constant {
    public static final int Success = 0;
    public static final int Failure = 1;

    public class Error{
        public class Login{
            public static final int front = 2;//id, name错误
            public static final int password = 3;//密码错误
        }
        public class SignUp{
            public static final int name = 4;//name不符合要求
            public static final int password = 5;//password不符合要求
            public static final int cdkey = 6;//cdkey错误
            public static final int cdkeyused = 7;//cdkey已经被使用
        }
    }

    public Constant(){

    }
}
