package org.dromara.omind.baseplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.api.service.pile.RemotePlatTradingRecordDataService;
import org.dromara.omind.baseplat.service.pile.PlatTradingRecordDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemotePlatTradingRecordDataServiceImpl implements RemotePlatTradingRecordDataService {

    @Autowired
    @Lazy
    PlatTradingRecordDataService platTradingRecordDataService;

    @Override
    public PlatTradingRecordData getByTradeNo(String tradeNo) {
        return platTradingRecordDataService.getByTradeNo(tradeNo);
    }

    @Override
    public boolean save(PlatTradingRecordData platTradingRecordData) {
        return platTradingRecordDataService.save(platTradingRecordData);
    }
}
