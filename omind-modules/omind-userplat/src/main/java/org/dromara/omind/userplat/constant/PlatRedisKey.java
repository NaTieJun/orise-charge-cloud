package org.dromara.omind.userplat.constant;

/**
 * 平台缓存
 */
public interface PlatRedisKey {

    long TOKEN_VALID_DURANT = 31536000; //一年

    String VER = "plat:globe:v1:";

    /**
     * redis启用设置：redis:true启用;false关闭
     */
    Boolean REDIS_FLAG = true;

    /**
     * 根据站点获取充电站信息
     */
    String STATION_INFO = VER + "station:info:";

    /**
     * 根据设备id获取充电桩详情
     */
    String EQUIPMENT_INFO = VER + "equipment:info:";

    /**
     * 根据充电枪id充电枪详情
     */
    String CONNECTOR_INFO = VER + "connector:info:";

    /**
     * 根据站点id获取充电站实时数据
     */
    String REALTIME_STATION_DATA = VER + "realtime:station:data:";

    /**
     * 根据身背id获取充电桩实时数据
     */
    String REALTIME_EQUIPMENT_DATA = VER + "realtime:equipment:data:";

    /**
     * 根据充电枪id获取充电接口实时数据
     */
    String REALTIME_CONNECTOR_DATA = VER + "realtime:connector:data2:";

    /**
     * 根据用户id获取用户详情
     */
    String USER_INFO_BY_UID = VER + "user:info:";

    /**
     * 根据微信unionId获取用户详情
     */
    String USER_INFO_BY_UINON_ID = VER + "user:info4unionid:";


    /**
     * 根据站点id获取充电站的充电桩列表
     */
    String STATION_EQUIPMENT_LIST = VER + "station:equipment:list:";

    /**
     * 根据充电订单序列号获取充电订单信息
     */
    String CHARGE_BILL_INFO = VER + "charge:bill:info:";

    /**
     * 根据站点id查询充电站停车计费数据列表
     */
    String TAG_STATION_PARK_INFO_LIST = VER + "tag:station:park:info:list:";

    /**
     * 根据运营商id获取运营商信息
     */
    String OPERATOR_INFO = VER + "operator:info:";

    /**
     * 根据充电枪id设置充电枪启动、停止分布式key
     */
    String CONNECTOR_START_STOP_CHARGE_LOCK = VER + "connector:start:stop:charge:";

    /**
     * 根据充电枪id设置推送充电枪状态分布式key
     */
    String NOTIFICATION_CONNECTOR_STATUS_LOCK = VER + "notification:connector:status:";

    /**
     * 根据充电订单序列号设置推送充电订单状态分布式key
     */
    String NOTIFICATION_CHARGE_STATUS_LOCK = VER + "notification:charge:status:";

    /**
     * 根据站点id设置推送充电站价格分布式key
     */
    String NOTIFICATION_STATION_FEE_LOCK = VER + "notification:station:fee:";

    /**
     * 根据充电枪id获取充电枪计价规则
     */
    String CONNECTOR_PRICE_LIST = VER + "connector:pricelist:";

    /**
     * 根据充电站和充电桩id获取实时价格
     */
    String STATION_EQUIPMENT_REALTIME_FLAG = VER + "station:equipment:realtime:";

    /**
     * 根据用户id获取用户车辆列表
     */
    String USER_CAR_LIST = VER + "user:car:list:";

    /**
     * 根据车辆id获取车辆详情
     */
    String USER_CAR_INFO = VER + "user:car:info:";

}
