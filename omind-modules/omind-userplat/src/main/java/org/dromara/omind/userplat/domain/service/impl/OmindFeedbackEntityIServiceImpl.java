package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.domain.mapper.OmindFeedbackEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindFeedbackEntityIService;
import org.springframework.stereotype.Service;

@Service
public class OmindFeedbackEntityIServiceImpl extends ServiceImpl<OmindFeedbackEntityMapper, OmindFeedbackEntity> implements OmindFeedbackEntityIService {
}
