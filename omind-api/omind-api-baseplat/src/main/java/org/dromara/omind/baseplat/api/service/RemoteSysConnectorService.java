package org.dromara.omind.baseplat.api.service;

import org.dromara.omind.baseplat.api.domain.ConnectorInfoData;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;

import java.util.Date;
import java.util.List;

public interface RemoteSysConnectorService {

    SysConnector getConnectorById(String connectorId);

    List<ConnectorInfoData> getAllByEquipmentId(String equipmentId);

    List<String> getAllIdByEquipmentId(String equipmentId);

    Boolean add(SysConnector sysConnector);

    Boolean updateById(SysConnector sysConnector);

    Boolean remove(SysConnector sysConnector);

    /**
     * 将心跳更新时间更新至缓存，数据库不更新，防止I/O影响性能
     * @param connectorId   充电接口ID
     * @param tm            心跳时间戳
     * @return
     */
    Boolean updatePingTm2Cache(String connectorId, Date tm);


}
