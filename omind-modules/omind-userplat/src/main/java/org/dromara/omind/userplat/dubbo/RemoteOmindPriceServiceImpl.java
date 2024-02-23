package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindPriceService;
import org.dromara.omind.userplat.service.OmindPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindPriceServiceImpl implements RemoteOmindPriceService {

    @Autowired
    @Lazy
    OmindPriceService omindPriceService;

    @Override
    public List<OmindPriceEntity> getPrice(String stationId) {
        return omindPriceService.getPrice(stationId);
    }

    @Override
    public OmindPriceEntity getPriceCurrent(String stationId) {
        return omindPriceService.getPriceCurrent(stationId);
    }
}
