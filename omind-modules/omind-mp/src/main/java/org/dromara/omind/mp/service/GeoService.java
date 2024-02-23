package org.dromara.omind.mp.service;

import org.dromara.omind.mp.domain.request.GeoStationListRequest;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.domain.vo.StationVo;

import java.util.List;

public interface GeoService {

    int rebuildGeoRedis();

    List<StationVo> geoList(GeoStationListRequest geoStationListRequest, SignRequest signRequest);

}
