package org.dromara.omind.baseplat.service.pile;

import org.dromara.omind.baseplat.api.domain.entity.PlatConnectorRealtimeData;

import java.util.List;

public interface PlatConnectorRealtimeDataService {

    /**
     * 获取订单充电实时数据列表（从缓存读取）
     * @param tradeNo
     * @return
     */
    List<PlatConnectorRealtimeData> list4TradeNo(String tradeNo);

    /**
     * 获取订单充电实时数据列表
     * @param tradeNo
     * @param refreshCache true 更新缓存 false 从缓存读取
     * @return
     */
    List<PlatConnectorRealtimeData> list4TradeNo(String tradeNo, boolean refreshCache);


    boolean save(PlatConnectorRealtimeData data);

    boolean saveBatch(List<PlatConnectorRealtimeData> dataList);

}
