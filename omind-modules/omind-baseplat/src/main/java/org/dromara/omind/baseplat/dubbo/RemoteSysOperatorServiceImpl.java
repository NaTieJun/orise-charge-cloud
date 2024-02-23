package org.dromara.omind.baseplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.service.RemoteSysOperatorService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteSysOperatorServiceImpl implements RemoteSysOperatorService {


    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @Override
    public SysOperator getOperatorById(String operatorId) {
        return operatorService.getOperatorById(operatorId);
    }

}
