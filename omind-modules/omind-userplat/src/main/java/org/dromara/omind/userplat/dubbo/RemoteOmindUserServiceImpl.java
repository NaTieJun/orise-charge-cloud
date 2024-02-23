package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindUserService;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteOmindUserServiceImpl implements RemoteOmindUserService {

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    @Override
    public OmindUserEntity getUserByUnionId(String unionId) {
        return omindUserService.getUserByUnionId(unionId);
    }

    @Override
    public Boolean addUser(OmindUserEntity userEntity) {
        return omindUserService.addUser(userEntity);
    }

    @Override
    public Boolean updateById(OmindUserEntity userEntity) {
        return omindUserService.updateById(userEntity);
    }

    @Override
    public OmindUserEntity getUserById(Long uid) {
        return omindUserService.getUserById(uid);
    }


}
