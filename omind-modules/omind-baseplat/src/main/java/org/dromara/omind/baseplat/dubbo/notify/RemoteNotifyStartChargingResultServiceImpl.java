package org.dromara.omind.baseplat.dubbo.notify;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyStartChargingResultService;
import org.dromara.omind.baseplat.service.notify.NotifyStartChargingResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@DubboService
@Service
public class RemoteNotifyStartChargingResultServiceImpl implements RemoteNotifyStartChargingResultService {

    @Autowired
    @Lazy
    NotifyStartChargingResultService notifyStartChargingResultService;

    @Override
    public void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason) {
        notifyStartChargingResultService.send(sysChargeOrder, isSuccess, reason);
    }
}
