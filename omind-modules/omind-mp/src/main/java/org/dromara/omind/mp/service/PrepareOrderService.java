package org.dromara.omind.mp.service;

import org.dromara.omind.mp.domain.vo.PrepareChargingVo;

public interface PrepareOrderService {

    PrepareChargingVo prepareOrderInfo(Long uid, String connectorId, Short hasPrice);

}
