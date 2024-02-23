package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindEquipmentEntity;
import org.dromara.omind.userplat.domain.mapper.OmindEquipmentEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindEquipmentEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindEquipmentEntityServiceImpl extends ServiceImpl<OmindEquipmentEntityMapper, OmindEquipmentEntity> implements OmindEquipmentEntityIService {
}
