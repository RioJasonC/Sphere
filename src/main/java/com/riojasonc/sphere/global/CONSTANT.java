package com.riojasonc.sphere.global;

public class CONSTANT {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    public class BASIC {
        public class WEB {
            public static final String ROOTURL = ""; //网页的根目录
        }
    }

    public class ERROR {
        public class LOGIN {
            public static final int FRONT = 2; //id, name错误
            public static final int PASSWORD = 3; //密码错误
        }
        public class SIGNUP {
            public static final int NAME = 4; //name不符合要求
            public static final int PASSWORD = 5; //password不符合要求
            public static final int CDKEY = 6; //cdkey错误
            public static final int CDKEYUSED = 7; //cdkey已经被使用
        }
        public class UTIL {
            public static final int MYSQL = -1; //SQL出现问题
        }
    }

    public class SERVER_RESPONSE {
        public class SERVER {
            public static final String SUCCESS = "Success"; //服务器响应成功
            public static final String FAILURE = "Failure"; //服务器响应失败（网络或者数据本身不合法等）
        }

        public class DATA {
            public static final String SUCCESS = "Success"; //数据响应成功
            public static final String FAILURE = "Failure"; //数据响应失败
            public static final String INVALID = "InvalidRequest"; //数据请求无效
        }
    }

    public CONSTANT(){

    }
}
