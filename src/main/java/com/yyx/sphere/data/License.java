package com.yyx.sphere.data;

public class License {
    private static final int cdkeyLength = 19;
    private static final String defaultCdKey = "0000-0000-0000-0000";
    private static final String defaultUsertype = "user";
    private static final String defaultStatus = "unknown";

    public String cdkey = defaultCdKey;
    public String usertype = defaultUsertype;
    public String status = defaultStatus;//"unused","used","unknown"

    public License(){

    }

    public License(String cdkey){
        if(cdkey.length() == cdkeyLength) this.cdkey = cdkey;
    }

    public License(String cdkey, String status){
        if(cdkey.length() == cdkeyLength) this.cdkey = cdkey;
        if(statusLegalCheck(status)) this.status = status;
    }

    private Boolean statusLegalCheck(String status){
        return status == "unused" || status == "used" || status == "unknown";
    }
}
