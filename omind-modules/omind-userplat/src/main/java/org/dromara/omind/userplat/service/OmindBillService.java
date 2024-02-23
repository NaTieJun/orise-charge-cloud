package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.userplat.api.domain.notifications.NotificationEquipChargeStatusData;
import org.dromara.omind.userplat.api.domain.request.BillForceStopRequest;
import org.dromara.omind.userplat.api.domain.request.BillPageRequest;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.response.QueryStopChargeResponseData;
import org.dromara.omind.userplat.api.domain.vo.ChargeOrderSimpleVo;
import org.dromara.omind.userplat.api.domain.vo.OmindBillInfoVo;

public interface OmindBillService {

    OmindBillEntity get(String startChargeSeq);

    OmindBillEntity getLatest(String connectorId);

    OmindBillEntity insertBillInfo(OmindBillEntity odBillInfo) throws BaseException;

    boolean updateBillInfo(OmindBillEntity odBillInfo) throws BaseException;

    QueryStopChargeResponseData billStopChargeDeal(String startChargeSeq, String connectorId, Long billId) throws BaseException;

    TableDataInfo chargeOrderList(ChargeOrderListRequest chargeOrderListRequest, Long userId, PageQuery pageQuery);

    QueryStopChargeResponseData stopCharge(Long billId) throws BaseException;

    void equipChargeStatusDeal(NotificationEquipChargeStatusData notificationEquipChargeStatusData, OmindConnectorEntity odConnectorEntity, String operatorId, Short platType) throws BaseException;

    void chargeOrderInfoDeal(NotificationChargeOrderInfoData notificationChargeOrderInfoData, OmindConnectorEntity odConnectorEntity, String operatorId, Short platType) throws BaseException;

    OmindBillEntity selectBillInfoById(Long billId);

    void forceStopBill(BillForceStopRequest billForceStopRequest) throws BaseException;

    void forceStopBillDeal(BillForceStopRequest billForceStopRequest) throws BaseException;

    String validateBillListlFields(BillPageRequest billPageRequest);

    TableDataInfo<OmindBillEntity> getBillList(BillPageRequest billPageRequest, PageQuery pageQuery);

}
