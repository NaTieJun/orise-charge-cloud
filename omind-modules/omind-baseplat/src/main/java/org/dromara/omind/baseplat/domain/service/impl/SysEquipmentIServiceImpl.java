package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.domain.mapper.SysEquipmentMapper;
import org.dromara.omind.baseplat.domain.service.SysEquipmentIService;
import org.springframework.stereotype.Service;

@Service
public class SysEquipmentIServiceImpl extends ServiceImpl<SysEquipmentMapper, SysEquipment> implements SysEquipmentIService {
}
