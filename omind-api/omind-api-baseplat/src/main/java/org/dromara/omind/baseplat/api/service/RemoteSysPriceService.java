package org.dromara.omind.baseplat.api.service;

import org.dromara.omind.baseplat.api.domain.PolicyInfoData;

import java.util.List;

public interface RemoteSysPriceService {

    List<PolicyInfoData> getConnectorPriceList(String connectorId);

    PolicyInfoData getCurrentPrice(String stationId, Long ts);

}
