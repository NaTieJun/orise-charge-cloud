package org.dromara.omind.userplat.constant;

/**
 * 充电订单状态:1、启动中;2、充电中;3、停止中;4、已结束;5、未知;
 */
public interface PlatChargeBillStatusConstant {

    /**o
     * 充电订单状态-启动中
     */
    int CHARGE_BILL_STARTING = 1;

    /**
     * 充电订单状态-充电中
     */
    int CHARGE_BILL_CHARGING = 2;

    /**
     * 充电订单状态-停止中
     */
    int CHARGE_BILL_STOPPING = 3;

    /**
     * 充电订单状态-已结束
     */
    int CHARGE_BILL_FINISH = 4;

    /**
     * 充电订单状态-未知
     */
    int CHARGE_BILL_UNKNOW = 5;

    /**
     * 停止充电阈值
     */
    String THRESHOLD = "0.9";

    /**
     * 充电类型
     * 充满
     */
    int CHARGE_TYPE_FULL = 1;

    /**
     * 充电类型
     * 定额
     */
    int CHARGE_TYPE_QUOTA = 2;

}
