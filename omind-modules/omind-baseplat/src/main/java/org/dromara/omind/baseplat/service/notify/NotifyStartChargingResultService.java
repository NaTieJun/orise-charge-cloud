package org.dromara.omind.baseplat.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface NotifyStartChargingResultService {

    /**
     *
     * @param sysChargeOrder
     * @param isSuccess
     * @param reason    0无 1设备不存在 2设备离线 3-99自定义
     */
    void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason);

}
