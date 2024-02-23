package org.dromara.omind.baseplat.api.constant;

public interface HlhtRedisKey {

    String VER = "keys:v1:";

    /**
     * 中电联互联互通协议，用户平台缓存TOKEN
     * + token值
     */
    String OPERATOR_TOKEN = VER + "hlht:operator:token:";

    /**
     * 订单详情
     * + startChargeSeq
     */
    String SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ = VER + "hlht:sys:charge_order:by:charge:seq:";

    /**
     * 订单详情（流水号查询）
     */
    String SYS_CHARGE_ORDER_INFO_BY_TRADENO = VER + "hlht:sys:charge_order:by:trade:no:";

    /**
     * 订单详情明细列表
     * + startChargeSeq
     */
    String SYS_CHARGE_ORDER_ITEM_LIST = VER + "hlht:sys:list:charge_order_item:";

    /**
     * 充电接口详情
     * + connectorId
     */
    String SYS_CONNECTOR = VER + "hlht:sys:connector:";

    /**
     * 充电接口对应充电站信息接口
     * + connectorId
     */
   String SYS_STATION_INFO_FOR_CONNECTOR_ID = VER + "hlht:sys:station:info4connectorid:";

    /**
     * 充电设备详情
     * + equipmentId
     */
    String SYS_EQUIPMENT = VER + "hlht:sys:equipment:";

    /**
     * 充电设备关联设备接口connectorId列表
     * + equipmentID
     */
    String SYS_CONNECTOR_ID_LIST_4_EQUIPMENT = VER + "hlht:sys:connectoridlist4equipment_v2:";

    /**
     * 充电设备关联设备接口全量connector列表
     * + equipmentID
     */
    String SYS_CONNECTOR_ALL_LIST_4_EQUIPMENT = VER + "hlht:sys:connectoralllist4equipment:";

    /**
     * 用户平台运营商详情
     * + operatorId
     */
    String SYS_OPERATOR = VER + "hlht:sys:operator:";

    /**
     * 充电站详情
     * + stationId
     */
    String SYS_STATION = VER + "hlht:sys:station:";


    /**
     * 基础平台访问用户平台的token
     * +operatorID
     */
    String UPLAT_TOKEN = "hlht:user:platform:token3:";

    /**
     * 请求seq生成器
     * + 当前秒值，即10位timestamp
     */
    String HLHT_API_SEQ_KEY = VER + "hlht:api:seq:key:";

    /***
     * 订单实时充电明细数据列表
     * + tradeNo
     */
    String PLAT_CONNECTOR_REALTIME_DATA_LIST = VER + "hlht:connector:realtime:data:list:";


    /**
     * 充电订单上报数据详情
     * + tradeNo
     */
    String PLAT_TRADING_RECORD_DATA = VER + "hlht:tading:record:data:";

    /**
     * 充电实时状态末次计算时间
     * + startChargeSeq
     */
    String PLAT_CHARGE_ORDER_DETAIL_LAST_CUL_TM = VER + "plat:charge:order:detail:last:cur:tm:";


    /**
     * 锁-充电订单更新
     * + startChargeSeq
     */
    String LOCK_KEY_CHARGE_ORDER_INFO = VER + "hlht:lock:charge:order:info";


    /**
     * 用户平台运营商关联的充电站列表
     * + operatorId
     */
    String SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID = VER + "hlht:sys:link:station:operator:list:by:operatorid:";

    /**
     * 充电站-运营商数据关联列表
     * + operatorId
     */
    String SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID = VER + "hlht:sys:link:station:operator:list:by:stationid:";

}
