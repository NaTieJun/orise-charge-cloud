package org.dromara.omind.baseplat.api.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;

/**
 * 枪状态改变入口——
 * 恢复心跳
 * 心跳超时
 * 上传实时监测数据
 */
public interface RemoteNotifyConnectorStatusService {

    /**
     * 心跳链接
     * @param sysConnector
     */
    void heartConnect(SysConnector sysConnector);

    /**
     * 心跳丢失
     * @param sysConnector
     */
    void heartDisConnect(SysConnector sysConnector);

    /**
     * 实时状态数据
     * @param connectorId
     */
    void realtimeData(String connectorId);

    /**
     * 每日复位
     * @param connectorId
     */
    void dailyCheck(String connectorId);

}
