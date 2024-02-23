package org.dromara.omind.simplat.service;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface StartChargingReturnService {

    SysChargeOrder startSuccess(SysChargeOrder sysChargeOrder);

    SysChargeOrder startFail(SysChargeOrder sysChargeOrder);

}
