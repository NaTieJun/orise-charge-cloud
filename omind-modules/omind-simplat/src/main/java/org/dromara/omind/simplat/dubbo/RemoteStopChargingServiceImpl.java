package org.dromara.omind.simplat.dubbo;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStopChargeData;
import org.dromara.omind.baseplat.api.service.RemoteSysChargeOrderService;
import org.dromara.omind.baseplat.api.service.RemoteSysConnectorService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStopChargingService;
import org.dromara.omind.simplat.config.SimCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@DubboService
@Service
public class RemoteStopChargingServiceImpl implements RemoteStopChargingService {

    @DubboReference
    RemoteSysConnectorService remoteSysConnectorService;

    @Autowired
    @Lazy
    SimCenter simCenter;

    @DubboReference
    RemoteSysChargeOrderService remoteSysChargeOrderService;

    @Override
    public int stopCharging(SysOperator sysOperator, QueryStopChargeData queryStopChargeData) throws BaseException {

        SysConnector sysConnector = remoteSysConnectorService.getConnectorById(queryStopChargeData.getConnectorID());
        if(sysConnector == null){
            return 1;
        }
        if(!simCenter.isOnline(queryStopChargeData.getConnectorID())){
            return 2;
        }
        SysChargeOrder sysChargeOrder = remoteSysChargeOrderService.getChargeOrderByStartChargeSeq(queryStopChargeData.getStartChargeSeq());
        if(sysChargeOrder == null || sysChargeOrder.getStartChargeSeqStat() >= 3){
            return 2;
        }

        simCenter.stopCharge(sysChargeOrder);
        return 0;
    }
}
