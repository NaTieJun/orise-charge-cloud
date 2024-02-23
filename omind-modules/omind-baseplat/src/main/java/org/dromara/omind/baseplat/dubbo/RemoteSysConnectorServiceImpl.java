package org.dromara.omind.baseplat.dubbo;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.ConnectorInfoData;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.service.*;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@DubboService
@Log4j2
@Service
public class RemoteSysConnectorServiceImpl implements RemoteSysConnectorService {

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Override
    public SysConnector getConnectorById(String connectorId) {
        return connectorService.getConnectorById(connectorId);
    }

    @Override
    public List<ConnectorInfoData> getAllByEquipmentId(String equipmentId) {
        return connectorService.getAllByEquipmentId(equipmentId);
    }

    @Override
    public List<String> getAllIdByEquipmentId(String equipmentId) {
        return connectorService.getAllIdByEquipmentId(equipmentId);
    }

    @Override
    public Boolean add(SysConnector sysConnector) {
        return connectorService.add(sysConnector);
    }

    @Override
    public Boolean updateById(SysConnector sysConnector) {
        return connectorService.updateById(sysConnector);
    }

    @Override
    public Boolean remove(SysConnector sysConnector) {
        return connectorService.remove(sysConnector);
    }

    @Override
    public Boolean updatePingTm2Cache(String connectorId, Date tm) {
        return connectorService.updatePingTm2Cache(connectorId,tm);
    }
}
