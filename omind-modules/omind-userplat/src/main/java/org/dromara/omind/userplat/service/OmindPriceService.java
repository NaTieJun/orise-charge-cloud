package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;

import java.util.List;

public interface OmindPriceService {

    /**
     * 查询站点价格列表，按照0-24点顺序返回
     * @param stationId
     * @return
     */
    List<OmindPriceEntity> getPrice(String stationId);

    OmindPriceEntity getPriceCurrent(String stationId);

    List<PolicyInfoData> queryEquipPrice(String connectorId) throws BaseException;

    List<PolicyInfoData> combine(List<PolicyInfoData> priceList);

    int deletePriceById(Long id) throws BaseException;

    OmindPriceEntity selectPriceById(Long id);

    int batchInsertPrice(List<OmindPriceEntity> priceList) throws BaseException;
}
