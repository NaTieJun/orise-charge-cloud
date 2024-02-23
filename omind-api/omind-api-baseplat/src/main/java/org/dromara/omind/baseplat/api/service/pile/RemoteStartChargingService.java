package org.dromara.omind.baseplat.api.service.pile;


import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStartChargeData;

public interface RemoteStartChargingService {

    /**
     * 返回交易流水号
     * 为空则启动失败
     * @param queryStartChargeData
     * @return
     */
    int startCharging(SysOperator sysOperator, QueryStartChargeData queryStartChargeData) throws BaseException;

}
