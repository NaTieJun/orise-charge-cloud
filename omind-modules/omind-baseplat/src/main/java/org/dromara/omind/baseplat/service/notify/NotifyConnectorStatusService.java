package org.dromara.omind.baseplat.service.notify;

import org.dromara.omind.baseplat.api.domain.ConnectorStatusInfoData;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;

/**
 * 触发时机：
 * 1、桩TCP——充电桩登录认证
 *     实时发送
 * 2、桩Socket——恢复心跳
 *     实时发送
 * 3、桩Socket——心跳超时
 *      实时发送
 * 4、桩Socket——从服务器断开
 *     实时发送
 * 5、桩TCP——上传实时监测数据
 *     状态改变时实时发送，不改变不发送
 * 6、桩TCP——地锁数据上送（状态改变）
 *     实时发送
 *
 */
public interface NotifyConnectorStatusService {

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
     * tcp链接断开
     * @param sysEquipment
     */
    void socketClose(SysEquipment sysEquipment);

    /**
     * 实时状态数据
     * @param connectorId
     */
    void realtimeData(String connectorId);

    /**
     * 每日固定状态复位
     * @param connectorId
     */
    void dailyCheck(String connectorId);

    void sendStatus(ConnectorStatusInfoData infoData, SysConnector sysConnector);

    void sendStatus(ConnectorStatusInfoData infoData, SysOperator sysOperator, int retryTime);

}
