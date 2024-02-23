package org.dromara.omind.baseplat.dubbo.notify;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyConnectorStatusService;
import org.dromara.omind.baseplat.service.notify.NotifyConnectorStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@DubboService
@Service
public class RemoteNotifyConnectorStatusServiceImpl implements RemoteNotifyConnectorStatusService {

    @Autowired
    @Lazy
    NotifyConnectorStatusService notifyConnectorStatusService;


    @Override
    public void heartConnect(SysConnector sysConnector) {
        notifyConnectorStatusService.heartConnect(sysConnector);
    }

    @Override
    public void heartDisConnect(SysConnector sysConnector) {
        notifyConnectorStatusService.heartDisConnect(sysConnector);
    }

    @Override
    public void realtimeData(String connectorId) {
        notifyConnectorStatusService.realtimeData(connectorId);
    }

    @Override
    public void dailyCheck(String connectorId) {
        notifyConnectorStatusService.dailyCheck(connectorId);
    }
}
