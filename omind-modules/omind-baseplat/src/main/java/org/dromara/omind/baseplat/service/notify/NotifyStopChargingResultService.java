package org.dromara.omind.baseplat.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface NotifyStopChargingResultService {

    /**
     *
     * @param sysChargeOrder
     * @param isSuccess
     * @param reason 0无 1此设备不存在 2此设备离线 3设备已停止充电 4-99自定义
     */
    void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason);

}
