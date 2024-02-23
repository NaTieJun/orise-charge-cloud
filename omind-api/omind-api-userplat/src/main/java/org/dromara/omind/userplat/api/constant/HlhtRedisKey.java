package org.dromara.omind.userplat.api.constant;

public interface HlhtRedisKey {

    String VER = "v1:cli:";

    /**
     * 用户平台根据用户平台运营商id获取与基础平台通信token
     */
    String USER_OPERATOR_TOKEN = VER + "hlht:uo:ptype:";

    /**
     * 基础平台根据基础平台运营商id获取与用户平台通信token
     */
    String BASE_OPERATOR_TOKEN = VER + "hlht:bo:ptype:";
}
