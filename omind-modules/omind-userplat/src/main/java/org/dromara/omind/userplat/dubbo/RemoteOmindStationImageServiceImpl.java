package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindStationImageEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindStationImageService;
import org.dromara.omind.userplat.service.OmindStationImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindStationImageServiceImpl implements RemoteOmindStationImageService {

    @Autowired
    @Lazy
    OmindStationImageService omindStationImageService;

    @Override
    public List<OmindStationImageEntity> all(String stationId) {
        return omindStationImageService.all(stationId);
    }
}
