package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.domain.mapper.OmindStationEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindStationEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindStationEntityServiceImpl extends ServiceImpl<OmindStationEntityMapper, OmindStationEntity> implements OmindStationEntityIService {
}
