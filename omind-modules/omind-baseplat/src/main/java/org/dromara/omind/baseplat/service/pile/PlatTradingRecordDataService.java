package org.dromara.omind.baseplat.service.pile;

import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;

public interface PlatTradingRecordDataService {

    PlatTradingRecordData getByTradeNo(String tradeNo);

    boolean save(PlatTradingRecordData platTradingRecordData);

}
