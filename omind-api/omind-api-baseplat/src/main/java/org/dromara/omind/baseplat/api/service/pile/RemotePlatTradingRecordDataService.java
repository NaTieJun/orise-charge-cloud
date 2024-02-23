package org.dromara.omind.baseplat.api.service.pile;

import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;

public interface RemotePlatTradingRecordDataService {

    PlatTradingRecordData getByTradeNo(String tradeNo);

    boolean save(PlatTradingRecordData platTradingRecordData);

}
