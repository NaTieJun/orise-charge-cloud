package org.dromara.omind.userplat.api.service;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.request.StartChargeRequest;

public interface RemoteOmindChargeService {

    R auth(String operatorId, String connectorId) throws BaseException;

    OmindBillEntity startCharge(StartChargeRequest startChargeRequest, Long uid) throws BaseException;

    R stopCharge(String startChargeSeq, Long uid) throws BaseException;

    TableDataInfo chargeOrderList(ChargeOrderListRequest request, Long uid, PageQuery pageQuery);

    OmindBillEntity chargeOrderInfo(String startChargeSeq);
}
