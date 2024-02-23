package org.dromara.omind.baseplat.api.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

/**
 * 远程启动充电命令回复
 */
public interface RemoteNotifyStartChargingResultService {

    /**
     *
     * @param sysChargeOrder
     * @param isSuccess
     * @param reason    0无 1设备不存在 2设备离线 3-99自定义
     */
    void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason);

}
