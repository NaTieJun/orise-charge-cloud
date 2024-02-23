package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.domain.mapper.OmindPriceEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindPriceEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindPriceEntityServiceImpl extends ServiceImpl<OmindPriceEntityMapper, OmindPriceEntity> implements OmindPriceEntityIService {
}
