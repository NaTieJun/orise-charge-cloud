package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.domain.mapper.OmindOperatorEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindOperatorEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindOperatorEntityServiceImpl extends ServiceImpl<OmindOperatorEntityMapper, OmindOperatorEntity> implements OmindOperatorEntityIService {
}
