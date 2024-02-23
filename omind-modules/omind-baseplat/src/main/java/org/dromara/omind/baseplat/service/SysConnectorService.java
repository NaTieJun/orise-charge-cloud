package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.ConnectorInfoData;
import org.dromara.omind.baseplat.api.domain.ConnectorStatusInfoData;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysConnectorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;

import java.util.Date;
import java.util.List;

public interface SysConnectorService {


    SysConnector getConnectorById(String connectorId);

    /**
     * 判断充电接口ID是否已存在
     * @param connectorId
     * @return
     */
    Boolean isConnectorIdValid(String connectorId);

    TableDataInfo<SysConnector> getConnectorPageList(QuerySysConnectorDto querySysConnectorDto, PageQuery pageQuery);

    List<ConnectorInfoData> getAllByEquipmentId(String equipmentId);

    int countByEquipmentId(String equipmentId);

    List<String> getAllIdByEquipmentId(String equipmentId);

    List<SysConnector> getAllConnectorByEquipmentId(String equipmentId, Boolean skipCache);

    List<ConnectorStatusInfoData> getAllStatusByEquipmentId(String equipmentId);

    Boolean add(SysConnector sysConnector);

    Boolean updateById(SysConnector sysConnector);

    Boolean updateBatchById(List<SysConnector> sysConnectorList, boolean refreshCache);

    Boolean remove(SysConnector sysConnector);

    /**
     * 将心跳更新时间更新至缓存，数据库不更新，防止I/O影响性能
     * @param connectorId   充电接口ID
     * @param tm            心跳时间戳
     * @return
     */
    Boolean updatePingTm2Cache(String connectorId, Date tm);

    SysOperator getDefaultOperatorByConnectorId(String connectorId);

    SysStation getStationInfoByConnectorId(String connectorId);

    /**
     * 获得所有无心跳但是连接的充电桩
     * @return
     */
    List<SysConnector> getAllUnaliveConnector();

    boolean offlineWithConnectorIds(List<String> connectorIdList);

}
