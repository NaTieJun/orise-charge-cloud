package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindUserListDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserOptDto;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;

import java.util.List;

public interface OmindUserService {

    OmindUserEntity getUserByUnionId(String unionId);

    Boolean addUser(OmindUserEntity userEntity);

    Boolean updateById(OmindUserEntity userEntity);

    OmindUserEntity getUserById(Long uid);

    List<OmindUserEntity> selectUserListByNickName(String nickName);

    List<OmindUserEntity> selectUserListByMobile(String mobile);

    String validateUserListlFields(OmindUserListDto omindUserListDto);

    TableDataInfo<OmindUserEntity> selectUserList(OmindUserListDto omindUserListDto, PageQuery pageQuery);

    int disableUser(OmindUserOptDto omindUserOptDto) throws BaseException;
}
