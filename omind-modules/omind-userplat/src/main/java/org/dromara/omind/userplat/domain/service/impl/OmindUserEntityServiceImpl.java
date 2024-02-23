package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.domain.mapper.OmindUserEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindUserEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindUserEntityServiceImpl extends ServiceImpl<OmindUserEntityMapper, OmindUserEntity> implements OmindUserEntityIService {
}
