package org.dromara.omind.userplat.api.service;

import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;

public interface RemoteOmindBillService {

    OmindBillEntity get(String startChargeSeq);

    OmindBillEntity getLatest(String connectorId);

}
