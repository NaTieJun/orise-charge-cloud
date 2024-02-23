package org.dromara.omind.userplat.service;

import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.request.StartChargeRequest;
import org.dromara.omind.userplat.api.domain.response.StartChargeResponseData;
import org.dromara.omind.userplat.api.domain.response.StopChargeResponseData;

public interface OmindChargeService {

    R auth(String operatorId, String connectorId);

    StartChargeResponseData startCharge(StartChargeRequest startChargeRequest, Long userId);

    StopChargeResponseData stopCharge(String startChargeSeq, Long userId);

    TableDataInfo chargeOrderList(ChargeOrderListRequest request, Long uid, PageQuery pageQuery);

    OmindBillEntity chargeOrderInfo(String startChargeSeq);

}
