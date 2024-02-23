package org.dromara.omind.baseplat.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

/**
 * 触发时机：
 * 1、桩TCP——远程停机命令回复
 *     实时发送
 * 2、桩TCP——上传实时监测数据（有订单号）
 *     1分钟发送一次
 * 3、桩TCP——充电阶段BMS中止
 *     实时发送
 * 4、桩TCP——充电阶段电机中止
 *     实时发送
 * 5、桩TCP——充电结束
 *     实时发送
 * 6、桩TCP——运营平台确认启动充电
 *     实时发送
 * 7、桩Socket——从服务器断开
 *     实时发送
 */
public interface NotifyChargingStatusService {

    /**
     * 远程停机
     * @param sysChargeOrder
     */
    void remoteStopCharging(SysChargeOrder sysChargeOrder);

    void realtimeData(SysChargeOrder sysChargeOrder);

    void bmsStop(SysChargeOrder sysChargeOrder);

    void chargerStop(SysChargeOrder sysChargeOrder);

    void chargingFinish(SysChargeOrder sysChargeOrder);

    void remoteConfirmCharging(SysChargeOrder sysChargeOrder);

    void disconnect(String startChargeSeq);

    void sendBySysChargeOrder(SysChargeOrder sysChargeOrder);

}
