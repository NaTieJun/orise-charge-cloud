package org.dromara.omind.mq.api.contants;

public interface TopicKeys {

    /**
     * 充电桩价格推送
     */
    String PilePriceSender = "priceSender-out";

    /**
     * 充电桩心跳-batch
     */
    String PileHeartBeat = "heartBeat-out";

    /**
     * 充电桩实时数据-batch
     */
    String PileRealtimeData = "realtimeData-out";

    /**
     * 充电订单
     */
    String PileChargeOrder = "chargeOrderData-out";

    /**
     * 充电价格下发至用户平台
     */
    String StationPriceSender = "stationPriceSender-out";
}
