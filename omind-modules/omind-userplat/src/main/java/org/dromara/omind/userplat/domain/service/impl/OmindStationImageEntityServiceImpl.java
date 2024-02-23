package org.dromara.omind.userplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.userplat.api.domain.entity.OmindStationImageEntity;
import org.dromara.omind.userplat.domain.mapper.OmindStationImageEntityMapper;
import org.dromara.omind.userplat.domain.service.OmindStationImageEntityIService;
import org.springframework.stereotype.Repository;

@Repository
public class OmindStationImageEntityServiceImpl extends ServiceImpl<OmindStationImageEntityMapper, OmindStationImageEntity> implements OmindStationImageEntityIService {
}
