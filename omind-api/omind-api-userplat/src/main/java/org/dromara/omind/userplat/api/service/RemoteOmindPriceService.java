package org.dromara.omind.userplat.api.service;

import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;

import java.util.List;

public interface RemoteOmindPriceService {

    /**
     * 查询站点价格列表，按照0-24点顺序返回
     * @param stationId
     * @return
     */
    List<OmindPriceEntity> getPrice(String stationId);

    OmindPriceEntity getPriceCurrent(String stationId);

}
