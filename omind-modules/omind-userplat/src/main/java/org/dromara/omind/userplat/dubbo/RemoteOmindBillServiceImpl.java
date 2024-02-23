package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindBillService;
import org.dromara.omind.userplat.service.OmindBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteOmindBillServiceImpl implements RemoteOmindBillService {

    @Autowired
    @Lazy
    OmindBillService billService;

    @Override
    public OmindBillEntity get(String startChargeSeq) {
        return billService.get(startChargeSeq);
    }

    @Override
    public OmindBillEntity getLatest(String connectorId) {
        return billService.getLatest(connectorId);
    }
}
