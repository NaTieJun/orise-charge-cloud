package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationStationFeeData;
import org.dromara.omind.userplat.api.domain.request.StationPageRequest;

import java.util.List;

public interface OmindStationService {

    OmindStationEntity get(String stationId);

    List<OmindStationEntity> getAllGeoData();

    void stationFeeDeal(NotificationStationFeeData notificationStationFeeData) throws BaseException;

    void stationCacheDel(String stationId);

    int batchInsertStation(List<OmindStationEntity> stationList) throws BaseException;

    int batchUpdateStation(List<OmindStationEntity> stationList) throws BaseException;

    TableDataInfo<OmindStationEntity> selectStationList(StationPageRequest stationPageRequest, PageQuery pageQuery);
}
