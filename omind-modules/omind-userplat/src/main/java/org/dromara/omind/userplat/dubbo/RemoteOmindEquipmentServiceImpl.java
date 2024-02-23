package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindEquipmentEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindEquipmentService;
import org.dromara.omind.userplat.service.OmindEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindEquipmentServiceImpl implements RemoteOmindEquipmentService {

    @Autowired
    @Lazy
    OmindEquipmentService omindEquipmentService;

    @Override
    public List<OmindEquipmentEntity> all(String stationId) {
        return omindEquipmentService.all(stationId);
    }

    @Override
    public OmindEquipmentEntity get(String equipmentId) {
        return omindEquipmentService.get(equipmentId);
    }
}
