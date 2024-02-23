package org.dromara.omind.simplat.service.impl;

import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.simplat.service.StopChargingReturnService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class StopChargingReturnServiceImpl implements StopChargingReturnService {


    @Override
    public void stopFail(SysChargeOrder sysChargeOrder) throws BaseException {

    }

    @Override
    public void stopSuccess(SysChargeOrder sysChargeOrder) throws BaseException {

    }
}
