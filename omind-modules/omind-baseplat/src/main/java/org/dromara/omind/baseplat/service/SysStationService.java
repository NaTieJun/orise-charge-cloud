package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysStationDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatsData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatusData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationsInfoData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatsResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatusResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationsInfoResponseData;
import org.dromara.omind.baseplat.domain.vo.Stats4ConnectorVo;
import org.dromara.omind.baseplat.domain.vo.TreeNodeVo;

import java.util.List;

public interface SysStationService {

    SysStation getStationById(String stationId);

    Boolean isStationIdValid(String stationId);

    /**
     * 内部调用
     * @param querySysStationDto
     * @return
     */
    TableDataInfo<SysStation> getStationPageList(QuerySysStationDto querySysStationDto, PageQuery pageQuery);

    /**
     * 互联互通接口调用
     * @param queryStationsInfoData
     * @return
     */
    QueryStationsInfoResponseData getStationPageList(SysOperator sysOperator, QueryStationsInfoData queryStationsInfoData);

    QueryStationStatusResponseData getStationStatusInfos(QueryStationStatusData queryStationStatusData);

    QueryStationStatsResponseData getStationStatsInfos(QueryStationStatsData queryStationStatsData);

    List<SysStation> getAllByOperatorId(String operatorId);

    List<String> getAllOpenStationId();

    Boolean save(SysStation sysStation);

    Boolean updateById(SysStation sysStation);

    Boolean remove(SysStation sysStation);

    List<TreeNodeVo> allTree();

    Stats4ConnectorVo getGunsShow(String stationId);

}
