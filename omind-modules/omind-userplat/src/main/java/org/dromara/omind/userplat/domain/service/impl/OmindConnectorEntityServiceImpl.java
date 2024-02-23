package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.domain.mapper.OmindConnectorEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindConnectorEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindConnectorEntityServiceImpl extends ServiceImpl<OmindConnectorEntityMapper,OmindConnectorEntity> implements OmindConnectorEntityIService {
}
