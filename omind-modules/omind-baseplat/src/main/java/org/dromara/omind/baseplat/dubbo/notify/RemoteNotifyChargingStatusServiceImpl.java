package org.dromara.omind.baseplat.dubbo.notify;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyChargingStatusService;
import org.dromara.omind.baseplat.service.notify.NotifyChargingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Slf4j
@Service
public class RemoteNotifyChargingStatusServiceImpl implements RemoteNotifyChargingStatusService {

    @Autowired
    @Lazy
    NotifyChargingStatusService notifyChargingStatusService;

    @Override
    public void remoteStopCharging(SysChargeOrder sysChargeOrder)  throws BaseException {
        notifyChargingStatusService.remoteStopCharging(sysChargeOrder);
    }

    @Override
    public void realtimeData(SysChargeOrder sysChargeOrder)  throws BaseException{
        notifyChargingStatusService.realtimeData(sysChargeOrder);
    }

    @Override
    public void chargerStop(SysChargeOrder sysChargeOrder)  throws BaseException{
        notifyChargingStatusService.chargerStop(sysChargeOrder);
    }

    @Override
    public void disconnect(String startChargeSeq)  throws BaseException{
        notifyChargingStatusService.disconnect(startChargeSeq);
    }

}
