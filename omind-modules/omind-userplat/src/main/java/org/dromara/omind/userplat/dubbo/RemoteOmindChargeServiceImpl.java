package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.request.StartChargeRequest;
import org.dromara.omind.userplat.api.domain.response.StartChargeResponseData;
import org.dromara.omind.userplat.api.domain.response.StopChargeResponseData;
import org.dromara.omind.userplat.api.service.RemoteOmindChargeService;
import org.dromara.omind.userplat.service.OmindBillService;
import org.dromara.omind.userplat.service.OmindChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteOmindChargeServiceImpl implements RemoteOmindChargeService {

    @Autowired
    @Lazy
    OmindChargeService omindChargeService;

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    @Override
    public R auth(String operatorId, String connectorId) {
        return omindChargeService.auth(operatorId, connectorId);
    }

    @Override
    public OmindBillEntity startCharge(StartChargeRequest startChargeRequest, Long uid) {
        StartChargeResponseData startChargeResponseData = omindChargeService.startCharge(startChargeRequest, uid);
        return startChargeResponseData.getBillInfo();
    }

    @Override
    public R stopCharge(String startChargeSeq, Long uid) {
        StopChargeResponseData stopChargeResponseData = omindChargeService.stopCharge(startChargeSeq, uid);
        if(stopChargeResponseData.getSuccStat() == 0){
            return R.ok();
        }
        return R.fail("停止失败");
    }

    @Override
    public TableDataInfo chargeOrderList(ChargeOrderListRequest request, Long uid, PageQuery pageQuery) {
        return omindBillService.chargeOrderList(request,uid,pageQuery);
    }

    @Override
    public OmindBillEntity chargeOrderInfo(String startChargeSeq) {
        return omindBillService.get(startChargeSeq);
    }
}
