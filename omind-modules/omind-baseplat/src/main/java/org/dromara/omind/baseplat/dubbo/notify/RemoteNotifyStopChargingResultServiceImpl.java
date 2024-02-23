package org.dromara.omind.baseplat.dubbo.notify;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyStopChargingResultService;
import org.dromara.omind.baseplat.service.notify.NotifyStopChargingResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@DubboService
@Service
public class RemoteNotifyStopChargingResultServiceImpl implements RemoteNotifyStopChargingResultService {

    @Autowired
    @Lazy
    NotifyStopChargingResultService notifyStopChargingResultService;

    @Override
    public void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason) {
        notifyStopChargingResultService.send(sysChargeOrder, isSuccess, reason);
    }
}
