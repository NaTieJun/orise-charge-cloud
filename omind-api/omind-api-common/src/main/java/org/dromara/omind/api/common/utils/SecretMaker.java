package org.dromara.omind.api.common.utils;

import java.util.Random;

public class SecretMaker {
    private static final String HEX_STR = "1234567890abcdef";

    private static final String ALL_STR = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String getRandom32HSecretMaker(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<16;i++){
            sb.append(HEX_STR.charAt(r.nextInt(HEX_STR.length())));
        }
        return sb.toString();
    }

    public static String getRandom16SecretMaker(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<16;i++){
            sb.append(ALL_STR.charAt(r.nextInt(ALL_STR.length())));
        }
        return sb.toString();
    }
}
