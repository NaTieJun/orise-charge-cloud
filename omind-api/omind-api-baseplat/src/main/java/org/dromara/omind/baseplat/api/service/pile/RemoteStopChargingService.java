package org.dromara.omind.baseplat.api.service.pile;


import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStopChargeData;

public interface RemoteStopChargingService {

    int stopCharging(SysOperator sysOperator, QueryStopChargeData queryStopChargeData) throws BaseException;

}
