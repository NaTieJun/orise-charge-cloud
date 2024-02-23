package org.dromara.omind.baseplat.dubbo;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.service.RemoteSysPriceService;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@DubboService
public class RemoteSysPriceServiceImpl implements RemoteSysPriceService {

    @Autowired
    @Lazy
    SysPriceService priceService;

    @Override
    public List<PolicyInfoData> getConnectorPriceList(String connectorId) {
        return priceService.getHlhtConnectorPriceList(connectorId);
    }

    @Override
    public PolicyInfoData getCurrentPrice(String stationId, Long ts) {
        return priceService.getHlhtCurrentPrice(stationId, ts);
    }


}
