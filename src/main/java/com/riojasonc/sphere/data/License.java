package com.riojasonc.sphere.data;

import java.util.Objects;

public class License {
    private static final int cdkeyLength = 19;
    private static final String defaultCdKey = "0000-0000-0000-0000";
    private static final String defaultUserType = "user";
    private static final String defaultStatus = "unknown";

    public String cdkey = defaultCdKey;
    public String userType = defaultUserType;
    public String status = defaultStatus;//"unused","used","unknown"

    public License(){

    }

    public License(String cdkey){
        if(cdkey.length() == cdkeyLength) {
            this.cdkey = cdkey;
        }
    }

    public License(String cdkey, String status){
        if(cdkey.length() == cdkeyLength) {
            this.cdkey = cdkey;
        }
        if(statusLegalCheck(status)) {
            this.status = status;
        }
    }

    private Boolean statusLegalCheck(String status){
        return Objects.equals(status, "unused") || Objects.equals(status, "used") || Objects.equals(status, "unknown");
    }
}
