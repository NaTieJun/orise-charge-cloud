package org.dromara.omind.baseplat.dubbo;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.api.service.*;
import org.dromara.omind.baseplat.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@DubboService
@Service
public class RemoteTradeServiceImpl implements RemoteTradeService {

    @Autowired
    @Lazy
    TradeService tradeService;

    @Override
    public boolean finishTrade(PlatTradingRecordData platTradingRecordData) {
        return tradeService.finishTrade(platTradingRecordData);
    }
}
