package org.dromara.omind.userplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.omind.userplat.api.domain.entity.OmindStationImageEntity;
import org.dromara.omind.userplat.domain.service.OmindStationImageEntityIService;
import org.dromara.omind.userplat.service.OmindStationImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OmindStationImageServiceImpl implements OmindStationImageService {

    @Autowired
    @Lazy
    OmindStationImageEntityIService iService;

    @Override
    public List<OmindStationImageEntity> all(String stationId) {
        LambdaQueryWrapper<OmindStationImageEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindStationImageEntity::getStationId,stationId);
        lambdaQueryWrapper.orderByAsc(OmindStationImageEntity::getId);

        return iService.list(lambdaQueryWrapper);
    }
}
