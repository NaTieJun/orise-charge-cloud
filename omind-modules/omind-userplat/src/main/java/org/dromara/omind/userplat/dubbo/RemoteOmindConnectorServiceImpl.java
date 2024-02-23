package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindConnectorService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindConnectorServiceImpl implements RemoteOmindConnectorService {

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Override
    public List<OmindConnectorEntity> all(String equipmentId) {
        return omindConnectorService.all(equipmentId);
    }

    @Override
    public OmindConnectorEntity get(String connectorId) {
        return omindConnectorService.get(connectorId);
    }

    @Override
    public List<OmindConnectorEntity> allByStationId(String stationId) {
        return omindConnectorService.getByStationId(stationId);
    }
}
