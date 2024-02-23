package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrderItem;
import org.dromara.omind.baseplat.domain.mapper.SysChargeOrderItemMapper;
import org.dromara.omind.baseplat.domain.service.SysChargeOrderItemIService;
import org.springframework.stereotype.Service;

@Service
public class SysChargeOrderItemIServiceImpl extends ServiceImpl<SysChargeOrderItemMapper, SysChargeOrderItem> implements SysChargeOrderItemIService {
}
