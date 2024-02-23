package org.dromara.omind.userplat.api.service;

import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;

public interface RemoteOmindUserService {

    OmindUserEntity getUserByUnionId(String unionId);

    Boolean addUser(OmindUserEntity userEntity);

    Boolean updateById(OmindUserEntity userEntity);

    OmindUserEntity getUserById(Long uid);
}
