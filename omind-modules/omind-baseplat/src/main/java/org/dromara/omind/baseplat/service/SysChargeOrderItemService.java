package org.dromara.omind.baseplat.service;

import org.dromara.omind.baseplat.api.domain.ChargeDetailData;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrderItem;

import java.util.List;

public interface SysChargeOrderItemService {

    List<ChargeDetailData> getList4StartChargeSeq(String startChargeSeq);

    /**
     * 更新订单明细（会删除旧的明细）
     * @param startChargeSeq
     * @param list
     * @return
     */
    boolean saveBatch(String startChargeSeq, List<SysChargeOrderItem> list);

}
