package org.dromara.omind.userplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.userplat.api.domain.dto.OmindUserListDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserOptDto;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindUserEntityIService;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OmindUserServiceImpl implements OmindUserService {

    @Autowired
    @Lazy
    OmindUserService selfService;

    @Autowired
    @Lazy
    OmindUserEntityIService iService;

    @Override
    public OmindUserEntity getUserByUnionId(String unionId) {
        String key = PlatRedisKey.USER_INFO_BY_UINON_ID + unionId;
        OmindUserEntity odUserEntity = null;
        if (RedisUtils.hasKey(key)) {
            odUserEntity = RedisUtils.getCacheObject(key);
            if (odUserEntity != null) {
                RedisUtils.expire(key, PlatRedisKey.TOKEN_VALID_DURANT);
                RedisUtils.setCacheObject(key, odUserEntity);
                return odUserEntity;
            } else {
                RedisUtils.deleteObject(key);
            }
        }
        //还是通过查uid 再统一查询构建缓存比较合适，以免多次调用
        LambdaQueryWrapper<OmindUserEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(OmindUserEntity::getUnionIdWx, unionId);
        List<OmindUserEntity> list = iService.list(lambdaQuery);
        if (list == null || list.size() == 0) {
            return null;
        }
        odUserEntity = list.get(0);
        RedisUtils.setCacheObject(key, list.get(0));
        return odUserEntity;
    }

    @Override
    public Boolean addUser(OmindUserEntity userEntity) {
        if (TextUtils.isBlank(userEntity.getUnionIdWx())) {
            return false;
        }
        if (selfService.getUserByUnionId(userEntity.getUnionIdWx()) != null) {
            return false;
        }
        if (!iService.save(userEntity)) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean updateById(OmindUserEntity userEntity) {
        if (userEntity.getUnionIdWx() != null) {
            String key = PlatRedisKey.USER_INFO_BY_UINON_ID + userEntity.getUnionIdWx();
            RedisUtils.deleteObject(key);
        }
        String key2 = PlatRedisKey.USER_INFO_BY_UID + userEntity.getUid();
        RedisUtils.deleteObject(key2);

        if (!iService.updateById(userEntity)) {
            return false;
        }
        return true;
    }

    @Override
    public OmindUserEntity getUserById(Long uid) {
        String key = PlatRedisKey.USER_INFO_BY_UID + uid;
        if (RedisUtils.hasKey(key)) {
            RedisUtils.expire(key, PlatRedisKey.TOKEN_VALID_DURANT);

            return (OmindUserEntity) RedisUtils.getCacheObject(key);
        }
        LambdaQueryWrapper<OmindUserEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(OmindUserEntity::getUid, uid);
        List<OmindUserEntity> list = iService.list(lambdaQuery);
        if (list == null || list.size() == 0) {
            return null;
        }
        OmindUserEntity odUserEntity = list.get(0);
        RedisUtils.setCacheObject(key, odUserEntity);
        return odUserEntity;
    }

    @Override
    public List<OmindUserEntity> selectUserListByNickName(String nickName) {
        LambdaQueryWrapper<OmindUserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(OmindUserEntity::getNickName, nickName.replace("%", "\\%"));
        lambdaQueryWrapper.orderByDesc(OmindUserEntity::getUid);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public List<OmindUserEntity> selectUserListByMobile(String mobile) {
        LambdaQueryWrapper<OmindUserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindUserEntity::getMobile, mobile);
        lambdaQueryWrapper.orderByDesc(OmindUserEntity::getUid);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public String validateUserListlFields(OmindUserListDto omindUserListDto) {
        String msg = "";
        if (!TextUtils.isBlank(omindUserListDto.getNickName()) && omindUserListDto.getNickName().length() > 64) {
            return msg = "用户名太长啦";
        }
        if (!TextUtils.isBlank(omindUserListDto.getMobile()) && omindUserListDto.getMobile().length() > 11) {
            return msg = "手机号太长啦";
        }
        return msg;
    }

    @Override
    public TableDataInfo<OmindUserEntity> selectUserList(OmindUserListDto omindUserListDto, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindUserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(omindUserListDto.getNickName())) {
            lambdaQueryWrapper.like(OmindUserEntity::getNickName, omindUserListDto.getNickName().replace("%", "\\%"));
        }
        if (!TextUtils.isBlank(omindUserListDto.getMobile())) {
            lambdaQueryWrapper.eq(OmindUserEntity::getMobile, omindUserListDto.getMobile());
        }
        if (omindUserListDto.getDisableFlag() != null) {
            lambdaQueryWrapper.eq(OmindUserEntity::getDisableFlag, omindUserListDto.getDisableFlag());
        }
        lambdaQueryWrapper.orderByDesc(OmindUserEntity::getUid);

        Page<OmindUserEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindUserEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            if (iPage.getRecords().size() > 0) {
                for (OmindUserEntity userEntity : iPage.getRecords()) {
                    if (userEntity.getAvatar() != null && !userEntity.getAvatar().startsWith("http")) {
                        userEntity.setAvatar("");//(minioS3Utils.getAccessUrl(userEntity.getAvatar()));
                    }
                }
            }
        }
        TableDataInfo<OmindUserEntity> tableDataInfo = TableDataInfo.build(iPage);
        return tableDataInfo;
    }

    @Override
    public int disableUser(OmindUserOptDto omindUserOptDto) throws BaseException {
        Long userId = omindUserOptDto.getUserId();
        Short disableFlag = omindUserOptDto.getDisableFlag();

        if (userId == null || userId <= 0) {
            throw new BaseException("用户id参数无效");
        }
        if (disableFlag == null || disableFlag < 0 || disableFlag > 1) {
            throw new BaseException("用户状态参数无效");
        }
        OmindUserEntity odUserEntity = selfService.getUserById(userId);
        if (odUserEntity == null) {
            throw new BaseException("用户不存在");
        }

        //定义redis
        String key = PlatRedisKey.USER_INFO_BY_UID + userId;
        if(odUserEntity.getUnionIdWx() != null) {
            String keyUnion = PlatRedisKey.USER_INFO_BY_UINON_ID + odUserEntity.getUnionIdWx();
            RedisUtils.deleteObject(keyUnion);
        }

        OmindUserEntity updateUserObjData = new OmindUserEntity();
        updateUserObjData.setUid(userId);
        updateUserObjData.setDisableFlag(disableFlag);
        selfService.updateById(updateUserObjData);

        RedisUtils.deleteObject(key);

        return 0;
    }
}
