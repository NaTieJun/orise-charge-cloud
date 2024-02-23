package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.domain.mapper.OmindBillEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindBillEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindBillEntityServiceImpl extends ServiceImpl<OmindBillEntityMapper, OmindBillEntity> implements OmindBillEntityIService {
}
