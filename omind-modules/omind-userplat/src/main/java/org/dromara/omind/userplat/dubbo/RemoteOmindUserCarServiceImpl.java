package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarInsertDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarUpdateDto;
import org.dromara.omind.userplat.api.domain.entity.OmindUserCarEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindUserCarService;
import org.dromara.omind.userplat.service.OmindUserCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@DubboService
@Service
public class RemoteOmindUserCarServiceImpl implements RemoteOmindUserCarService {

    @Autowired
    @Lazy
    OmindUserCarService omindUserCarService;

    @Override
    public OmindUserCarEntity get(Long uid, String plateNo) {
        return omindUserCarService.get(uid,plateNo);
    }

    @Override
    public void insert(OmindUserCarInsertDto userCarInsertDto) {
        omindUserCarService.insert(userCarInsertDto);
    }

    @Override
    public void del(Long id) {
        omindUserCarService.del(id);
    }

    @Override
    public OmindUserCarEntity info(Long id) {
        return omindUserCarService.info(id);
    }

    @Override
    public List<OmindUserCarEntity> list(Long uid) {
        return omindUserCarService.list(uid);
    }

    @Override
    public void upCar(OmindUserCarUpdateDto userCarUpdateDto) {
        omindUserCarService.upCar(userCarUpdateDto);
    }
}
