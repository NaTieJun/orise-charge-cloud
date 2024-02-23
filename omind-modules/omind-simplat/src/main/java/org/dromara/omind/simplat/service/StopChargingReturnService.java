package org.dromara.omind.simplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface StopChargingReturnService {

    void stopFail(SysChargeOrder sysChargeOrder)throws BaseException;

    void stopSuccess(SysChargeOrder sysChargeOrder)throws BaseException;

}
