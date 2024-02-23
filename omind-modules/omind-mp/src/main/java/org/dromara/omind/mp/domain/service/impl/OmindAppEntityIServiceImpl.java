package org.dromara.omind.mp.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.mp.domain.mapper.OmindAppEntityMapper;
import org.dromara.omind.mp.domain.service.OmindAppEntityIService;
import org.dromara.omind.userplat.api.domain.entity.OmindAppEntity;
import org.springframework.stereotype.Service;

@Service
public class OmindAppEntityIServiceImpl extends ServiceImpl<OmindAppEntityMapper, OmindAppEntity> implements OmindAppEntityIService {
}
