package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.domain.response.PriceInfoResponse;
import org.dromara.omind.baseplat.domain.request.PriceAddRequest;
import org.dromara.omind.baseplat.domain.request.PriceEditRequest;
import org.dromara.omind.baseplat.domain.request.PriceLinkStationsRequest;

import java.util.List;

public interface SysPriceService {

    TableDataInfo getPricePage(String keyword, PageQuery pageQuery);

    PriceInfoResponse getPriceInfo(Long priceCode);

    PriceInfoResponse getStationPriceInfo(String stationId);

    Long addPrice(PriceAddRequest priceAddRequest);

    void editPrice(PriceEditRequest priceEditRequest);

    void linkStations(PriceLinkStationsRequest priceLinkStationsRequest);

    void removePrice(Long priceCode);

    Long getMaxPriceCode();

    List<PolicyInfoData> getHlhtPriceByCode(Long priceCode);

    List<PolicyInfoData> getHlhtPrice4Station(String stationId);

    List<PolicyInfoData> getHlhtConnectorPriceList(String connectorId);

    PolicyInfoData getHlhtCurrentPrice(String stationId, Long ts);
}
