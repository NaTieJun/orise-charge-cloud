package org.dromara.omind.baseplat.dubbo;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.service.RemoteSysChargeOrderService;
import org.dromara.omind.baseplat.service.SysChargeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Log4j2
@Service
public class RemoteSysChargeOrderServiceImpl implements RemoteSysChargeOrderService {

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Override
    public SysChargeOrder getChargeOrderByStartChargeSeq(String startChargeSeq) {
        return chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
    }

    @Override
    public void save(SysChargeOrder sysChargeOrder) throws BaseException {
        chargeOrderService.save(sysChargeOrder);
    }

    @Override
    public void update(SysChargeOrder sysChargeOrder) throws BaseException {
        chargeOrderService.update(sysChargeOrder);
    }
}
