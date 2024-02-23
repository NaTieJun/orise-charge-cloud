package org.dromara.omind.baseplat.service.notify;

import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface NotifyTradeInfoService {

    void send(SysChargeOrder sysChargeOrder);

}
