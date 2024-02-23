package org.dromara.omind.baseplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.ConnectorStatsInfoData;
import org.dromara.omind.baseplat.api.domain.dto.SysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

import java.util.List;

public interface SysChargeOrderService {

    ConnectorStatsInfoData getStatsInfo(String connectorId, long start, long end);

    SysChargeOrder getChargeOrderByStartChargeSeq(String startChargeSeq);

    SysChargeOrder getChargeOrderByTradeNo(String tradeNo);

    /**
     * 返回最新的充电订单
     * @param connectorId
     * @return
     */
    SysChargeOrder getChargingOrderByConnectorId(String connectorId);

    List<SysChargeOrder> getUnSyncChargeOrderList();

    /**
     * 处理超过24小时的无效订单
     */
    void dealWithExChargeOrder();


    void save(SysChargeOrder sysChargeOrder) throws BaseException;

    void update(SysChargeOrder sysChargeOrder) throws BaseException;

    TableDataInfo<SysChargeOrderDto> getChargeOrderList(QuerySysChargeOrderDto querySysChargeOrderDto, PageQuery pageQuery);

    List<SysChargeOrder> getChargeOrderList(QuerySysChargeOrderDto querySysChargeOrderDto);

}
