package org.dromara.omind.userplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.SecretMaker;
import org.dromara.omind.userplat.api.domain.dto.OmindOperationListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindOperatorEntityIService;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OmindOperatorServiceImpl implements OmindOperatorService {

    @Autowired
    @Lazy
    OmindOperatorEntityIService iService;

    @Autowired
    @Lazy
    OmindOperatorService selfService;

    @Override
    public OmindOperatorEntity selectOperatorInfoById(String operatorId) {
        String key = PlatRedisKey.OPERATOR_INFO + operatorId;
        if (PlatRedisKey.REDIS_FLAG) {
            if (RedisUtils.hasKey(key)) {
                OmindOperatorEntity odOperatorInfoData = RedisUtils.getCacheObject(key);
                if (odOperatorInfoData != null) {
                    return odOperatorInfoData;
                }
            }
        }

        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindOperatorEntity::getOperatorId, operatorId).last("limit 1");
        OmindOperatorEntity odOperatorInfo = iService.getOne(lambdaQueryWrapper);

        //添加到redis
        RedisUtils.setCacheObject(key, odOperatorInfo);

        return odOperatorInfo;
    }

    @Override
    public OmindOperatorEntity selectOperatorInfo(String userOperatorId, Short platType) {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindOperatorEntity::getPlatType, platType);
        lambdaQueryWrapper.eq(OmindOperatorEntity::getUserOperatorId, userOperatorId).last("limit 1");

        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public OmindOperatorEntity getOperatorInfo(String baseOperatorId, String userOperatorId) {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindOperatorEntity::getOperatorId, baseOperatorId);
        lambdaQueryWrapper.eq(OmindOperatorEntity::getUserOperatorId, userOperatorId).last("limit 1");

        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public TableDataInfo<OmindOperatorEntity> selectOperatorInfoList(OmindOperationListDto omindOperationListDto, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(omindOperationListDto.getOperatorName())) {
            lambdaQueryWrapper.like(OmindOperatorEntity::getOperatorName, omindOperationListDto.getOperatorName().replace("%", "\\%"));
        }
        lambdaQueryWrapper.orderByDesc(OmindOperatorEntity::getId);

        Page<OmindOperatorEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindOperatorEntity> iPage = iService.page(page, lambdaQueryWrapper);

        return TableDataInfo.build(iPage);
    }

    @Override
    public boolean insertOperatorInfo(OmindOperatorEntity odOperatorInfo) throws BaseException {
        boolean repeatFlag = selfService.checkOperatorInfoUnique(odOperatorInfo);
        if (repeatFlag) {
            throw new BaseException("数据已存在");
        }

        if (!iService.save(odOperatorInfo)) {
            throw new BaseException("数据保存失败");
        }

        return true;
    }

    @Override
    public boolean updateOperatorInfo(OmindOperatorEntity odOperatorInfo) throws BaseException {
        OmindOperatorEntity odOperatorEntity = selfService.getOperatorInfoById(odOperatorInfo.getId().intValue());
        if (odOperatorEntity == null) {
            throw new BaseException("数据不存在");
        }

        boolean repeatFlag = selfService.checkOperatorInfoUnique(odOperatorInfo);
        if (repeatFlag) {
            throw new BaseException("数据已存在");
        }

        if (!iService.updateById(odOperatorInfo)) {
            throw new BaseException("数据更新失败");
        }

        //缓存redis处理
        String key = PlatRedisKey.OPERATOR_INFO + odOperatorInfo.getOperatorId();
        RedisUtils.deleteObject(key);

        return true;
    }

    @Override
    public Boolean checkOperatorInfoUnique(OmindOperatorEntity odOperatorInfo) {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindOperatorEntity::getOperatorId, odOperatorInfo.getOperatorId());
        OmindOperatorEntity odOperatorCheck = iService.getOne(lambdaQueryWrapper);

        boolean repeatFlag = false;
        if (odOperatorCheck != null) {
            if (!Objects.equals(odOperatorCheck.getId(), odOperatorInfo.getId())) {
                repeatFlag = true;
            }
        }
        return repeatFlag;
    }

    @Override
    public String validateOperatorFields(OmindOperatorEntity odOperatorInfo) {
        String msg = "";
        if (TextUtils.isBlank(odOperatorInfo.getOperatorId())) {
            return msg = "运营商组织代码为空";
        }
        if (odOperatorInfo.getOperatorId().length() > 10) {
            return msg = "运营商组织代码太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getOperatorName())) {
            return msg = "运营商名称为空";
        }
        if (odOperatorInfo.getOperatorName().length() > 64) {
            return msg = "运营商名称太长啦";
        }
        if (!TextUtils.isBlank(odOperatorInfo.getOperatorTel1()) && odOperatorInfo.getOperatorTel1().length() > 32) {
            return msg = "运营商电话1太长啦";
        }
        if (!TextUtils.isBlank(odOperatorInfo.getOperatorTel2()) && odOperatorInfo.getOperatorTel2().length() > 32) {
            return msg = "运营商电话2太长啦";
        }
        if (!TextUtils.isBlank(odOperatorInfo.getOperatorRegAddress()) && odOperatorInfo.getOperatorRegAddress().length() > 64) {
            return msg = "运营商注册地址太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getBaseDataSecret())) {
            return msg = "消息密钥为空";
        }
        if (odOperatorInfo.getBaseDataSecret().length() > 64) {
            return msg = "消息密钥太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getBaseDataSecretIv())) {
            return msg = "消息密钥初始化向量为空";
        }
        if (odOperatorInfo.getBaseDataSecretIv().length() > 16) {
            return msg = "消息密钥初始化向量太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getBaseOperatorSecret())) {
            return msg = "运营商密钥为空";
        }
        if (odOperatorInfo.getBaseOperatorSecret().length() > 64) {
            return msg = "运营商密钥太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getBaseSigSecret())) {
            return msg = "签名密钥为空";
        }
        if (odOperatorInfo.getBaseSigSecret().length() > 64) {
            return msg = "签名密钥太长啦";
        }
        if (TextUtils.isBlank(odOperatorInfo.getApiUrl())) {
            return msg = "接口地址为空";
        }
        if (odOperatorInfo.getApiUrl().length() > 128) {
            return msg = "接口地址太长啦";
        }
        if (odOperatorInfo.getPlatType() == null || odOperatorInfo.getPlatType() <= 0) {
            return msg = "未运营商平台类型或类型错误";
        }

        return msg;
    }

    @Override
    public OmindOperatorEntity getOperatorInfoById(Integer id) {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindOperatorEntity::getId, id);
        OmindOperatorEntity odOperatorInfo = iService.getOne(lambdaQueryWrapper);
        if (odOperatorInfo == null) {
            throw new BaseException("数据不存在");
        }
        return odOperatorInfo;
    }

    @Override
    public OmindOperatorEntity resetSecret(String operatorId) throws BaseException {
        if (TextUtils.isBlank(operatorId)) {
            throw new BaseException("参数无效");
        }
        OmindOperatorEntity odOperatorEntity = selfService.selectOperatorInfoById(operatorId);
        odOperatorEntity.setOperatorSecret(SecretMaker.getRandom32HSecretMaker());
        odOperatorEntity.setDataSecret(SecretMaker.getRandom16SecretMaker());
        odOperatorEntity.setDataSecretIv(SecretMaker.getRandom16SecretMaker());
        odOperatorEntity.setSigSecret(SecretMaker.getRandom32HSecretMaker());

        if (!iService.updateById(odOperatorEntity)) {
            throw new BaseException("数据更新失败");
        }

        //缓存redis处理
        String key = PlatRedisKey.OPERATOR_INFO + operatorId;
        RedisUtils.deleteObject(key);

        return odOperatorEntity;
    }

    @Override
    public int deleteOperatorInfoById(String operatorId) throws BaseException {
        OmindOperatorEntity odOperatorEntity = selfService.selectOperatorInfoById(operatorId);
        if (odOperatorEntity == null) {
            throw new BaseException("数据不存在");
        }
        boolean delFlag = iService.remove(new LambdaQueryWrapper<OmindOperatorEntity>()
                .eq(OmindOperatorEntity::getOperatorId, operatorId));

        //缓存redis处理
        if (delFlag) {
            String key = PlatRedisKey.OPERATOR_INFO + operatorId;
            RedisUtils.deleteObject(key);
        }

        return delFlag ? 1 : 0;
    }

    @Override
    public List<OmindOperatorEntity> selectOperatorList() {
        LambdaQueryWrapper<OmindOperatorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(OmindOperatorEntity::getId);

        return iService.list(lambdaQueryWrapper);
    }
}
