package org.dromara.omind.baseplat.service;

import org.dromara.omind.baseplat.api.domain.entity.SysStationPrice;

import java.util.List;

public interface SysStationPriceService {

    void set(String stationId, Long priceCode, String remark);

    SysStationPrice get(String stationId);

    List<String> getLinkStations(Long priceCode);

}
