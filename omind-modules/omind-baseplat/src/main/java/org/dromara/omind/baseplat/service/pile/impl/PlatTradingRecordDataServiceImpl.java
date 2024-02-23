package org.dromara.omind.baseplat.service.pile.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.domain.service.PlatTradingRecordDataIService;
import org.dromara.omind.baseplat.service.pile.PlatTradingRecordDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class PlatTradingRecordDataServiceImpl implements PlatTradingRecordDataService {

    @Autowired
    @Lazy
    PlatTradingRecordDataIService iService;

    @Autowired
    @Lazy
    PlatTradingRecordDataService selfService;

    @Override
    public PlatTradingRecordData getByTradeNo(String tradeNo) {

        if(TextUtils.isBlank(tradeNo)){
            return null;
        }

        String key = HlhtRedisKey.PLAT_TRADING_RECORD_DATA + tradeNo;
        if(RedisUtils.hasKey(key)){
            PlatTradingRecordData platTradingRecordData = RedisUtils.getCacheObject(key);
            if(platTradingRecordData != null){
                return platTradingRecordData;
            }
        }

        LambdaQueryWrapper<PlatTradingRecordData> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(PlatTradingRecordData::getTradeNo, tradeNo);
        List<PlatTradingRecordData> list = iService.list(lambdaQueryWrapper);
        if(list != null && list.size() > 0){
            PlatTradingRecordData data = list.get(0);
            RedisUtils.setCacheObject(key, data);
            return data;
        }
        return null;
    }

    @Override
    public boolean save(PlatTradingRecordData platTradingRecordData) {
        if(platTradingRecordData == null || TextUtils.isBlank(platTradingRecordData.getTradeNo())){
            return false;
        }
        if(null != selfService.getByTradeNo(platTradingRecordData.getTradeNo())){
            return true;
        }
        return iService.save(platTradingRecordData);
    }

}
