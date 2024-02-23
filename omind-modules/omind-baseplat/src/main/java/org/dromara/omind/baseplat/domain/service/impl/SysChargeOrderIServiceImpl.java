package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.domain.mapper.SysChargeOrderMapper;
import org.dromara.omind.baseplat.domain.service.SysChargeOrderIService;
import org.springframework.stereotype.Service;

@Service
public class SysChargeOrderIServiceImpl extends ServiceImpl<SysChargeOrderMapper, SysChargeOrder> implements SysChargeOrderIService {
}
