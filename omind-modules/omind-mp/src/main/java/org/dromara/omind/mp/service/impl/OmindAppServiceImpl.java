package org.dromara.omind.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.mp.constant.XcxRedisKey;
import org.dromara.omind.mp.domain.service.OmindAppEntityIService;
import org.dromara.omind.mp.service.OmindAppService;
import org.dromara.omind.userplat.api.domain.entity.OmindAppEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Log4j2
@Service
public class OmindAppServiceImpl implements OmindAppService {

    @Autowired
    @Lazy
    OmindAppEntityIService iService;

    @Override
    public OmindAppEntity getApp(String appkey) {
        String key = XcxRedisKey.SYS_APP_INFO + appkey;

        if(RedisUtils.hasKey(key)){
            try
            {
                OmindAppEntity appEntity = RedisUtils.getCacheObject(key);
                if(appEntity != null){
                    RedisUtils.expire(key, 3600);
                    return appEntity;
                }
            }
            catch (Exception ex){
                log.error(ex.toString());
            }
            RedisUtils.deleteObject(key);
        }

        LambdaQueryWrapper<OmindAppEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(OmindAppEntity::getAppKey, appkey);
        List<OmindAppEntity> list = iService.list(lambdaQuery);
        if(list == null || list.size() == 0){
            return null;
        }
        OmindAppEntity sysAppEntity = list.get(0);
        RedisUtils.setCacheObject(key, sysAppEntity, Duration.ofHours(1));
        return sysAppEntity;
    }
}
