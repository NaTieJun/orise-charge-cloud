package org.dromara.omind.baseplat.api.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface RemoteSysChargeOrderService {


    SysChargeOrder getChargeOrderByStartChargeSeq(String startChargeSeq);

    void save(SysChargeOrder sysChargeOrder) throws BaseException;

    void update(SysChargeOrder sysChargeOrder) throws BaseException;

}
