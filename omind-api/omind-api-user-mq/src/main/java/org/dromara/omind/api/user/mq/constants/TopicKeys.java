package org.dromara.omind.api.user.mq.constants;

public interface TopicKeys {

    /**
     * 用户平台--处理基础推送设备状态变化信息
     */
    String PileUpStationStatus = "upStationStatus-out";

    /**
     * 用户平台--处理基础平台推送充电设备的充电状态
     */
    String PileUpEquipChargeStatus = "upEquipChargeStatus-out";

    /**
     * 用户平台--处理基础平台推送充电订单信息
     */
    String PileUpChargeOrderInfo = "upChargeOrderInfo-out";
}
