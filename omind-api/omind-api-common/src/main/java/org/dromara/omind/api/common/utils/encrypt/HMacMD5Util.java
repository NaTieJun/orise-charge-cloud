package org.dromara.omind.api.common.utils.encrypt;

import cn.hutool.crypto.SecureUtil;


public class HMacMD5Util {

    public static String getHMacMD5(String key, String operatorId, String encodeData, String timestamp, String seq)
    {
        try {
            String m = new StringBuilder(operatorId).append(encodeData).append(timestamp).append(seq).toString();
            return SecureUtil.hmacMd5(key).digestHex(m);
        }
        catch (Exception ex){
            return "";
        }
    }

    public static String getHMacMD5(String key, String datas){
        try {
            return SecureUtil.hmacMd5(key).digestHex(datas);
        }
        catch (Exception ex){
            return "";
        }
    }
}
