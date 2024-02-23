package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.api.domain.entity.SysStationOperatorLink;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorRequest;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorUpdateRequest;
import org.dromara.omind.baseplat.api.domain.request.QueryStationsInfoData;

import java.util.List;

public interface StationOperatorLinkService {

    List<SysStationOperatorLink> getList4OperatorId(String operatorId);

    List<SysStationOperatorLink> getList4Station(String stationId);

    Boolean linkOperatorAndStation(LinkStationOperatorRequest linkStationOperatorRequest);

    Boolean updateLink(LinkStationOperatorUpdateRequest linkStationOperatorUpdateRequest);

    Boolean delLink(Long id);

    Boolean delLink4Operator(String operatorId);

    Boolean delLink4Station(String stationId);

    void stationUpdate(String stationId);

    TableDataInfo<SysStation> page4OperatorId(String operatorId, QueryStationsInfoData queryStationsInfoData, PageQuery pageQuery);

}
