package org.dromara.omind.userplat.api.service;

import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;

import java.util.List;

public interface RemoteOmindConnectorService {
    List<OmindConnectorEntity> all(String equipmentId);

    OmindConnectorEntity get(String connectorId);

    List<OmindConnectorEntity> allByStationId(String stationId);
}
