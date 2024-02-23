package org.dromara.omind.baseplat.service;

import org.dromara.omind.baseplat.api.domain.ChargeDetailData;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationEquipChargeStatusData;

import java.util.List;

public interface TradeService {

    /**
     * 获取充电明细（状态推送用）
     * @param startChargeSeq 交易订单号
     * @return
     */
    List<ChargeDetailData> getChargeOrderDetails(String startChargeSeq, boolean refreshCache);

    /**
     * 订单信息推送数据生成器，通过订单号，查询交易结果
     * @param startChargeSeq    充电订单号
     * @return
     */
    NotificationChargeOrderInfoData getCharingTradeInfo(String startChargeSeq);

    /**
     * 充电状态推送数据生成器
     * @param startChargeSeq
     * @return
     */
    NotificationEquipChargeStatusData getChargingStatus(String startChargeSeq);

    boolean finishTrade(PlatTradingRecordData platTradingRecordData);

}
