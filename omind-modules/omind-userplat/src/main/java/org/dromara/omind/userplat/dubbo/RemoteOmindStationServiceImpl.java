package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindStationService;
import org.dromara.omind.userplat.service.OmindStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindStationServiceImpl implements RemoteOmindStationService {

    @Autowired
    @Lazy
    OmindStationService omindStationService;

    @Override
    public OmindStationEntity get(String stationId) {
        return omindStationService.get(stationId);
    }

    @Override
    public List<OmindStationEntity> getAllGeoData() {
        return omindStationService.getAllGeoData();
    }
}
