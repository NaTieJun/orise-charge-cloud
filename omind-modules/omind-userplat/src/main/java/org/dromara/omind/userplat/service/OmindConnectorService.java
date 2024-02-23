package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.request.ConnectorPageRequest;

import java.util.List;

public interface OmindConnectorService {
    List<OmindConnectorEntity> all(String equipmentId);

    OmindConnectorEntity selectConnectorInfo(String connectorId);

    OmindConnectorEntity get(String connectorId);

    List<OmindConnectorEntity> getByStationId(String stationId);

    int batchInsertConnector(List<OmindConnectorEntity> connectorList) throws BaseException;

    int batchUpdateConnector(List<OmindConnectorEntity> connectorList) throws BaseException;

    Integer connectorCount(String stationId, String equipmentId);

    OmindConnectorEntity info(Integer id);

    TableDataInfo<OmindConnectorEntity> selectConnectorList(ConnectorPageRequest connectorPageRequest, PageQuery pageQuery);

    boolean updateConnector(OmindConnectorEntity odConnector) throws BaseException;

    void connectorPriceCacheDeal(String StationId);
}
