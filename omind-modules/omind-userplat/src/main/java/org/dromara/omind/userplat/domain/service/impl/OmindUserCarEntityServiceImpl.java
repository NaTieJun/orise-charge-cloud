package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindUserCarEntity;
import org.dromara.omind.userplat.domain.mapper.OmindUserCarEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindUserCarEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindUserCarEntityServiceImpl extends ServiceImpl<OmindUserCarEntityMapper,OmindUserCarEntity> implements OmindUserCarEntityIService {
}
