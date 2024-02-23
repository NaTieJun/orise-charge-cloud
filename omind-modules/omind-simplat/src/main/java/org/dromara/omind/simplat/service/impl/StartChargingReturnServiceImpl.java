package org.dromara.omind.simplat.service.impl;

import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.simplat.service.StartChargingReturnService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class StartChargingReturnServiceImpl implements StartChargingReturnService {

    @Override
    public SysChargeOrder startSuccess(SysChargeOrder sysChargeOrder) {
        return null;
    }

    @Override
    public SysChargeOrder startFail(SysChargeOrder sysChargeOrder) {
        return null;
    }
}
