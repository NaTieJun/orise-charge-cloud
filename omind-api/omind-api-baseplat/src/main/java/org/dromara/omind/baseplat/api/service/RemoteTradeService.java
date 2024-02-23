package org.dromara.omind.baseplat.api.service;

import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;

public interface RemoteTradeService {

    boolean finishTrade(PlatTradingRecordData platTradingRecordData);

}
