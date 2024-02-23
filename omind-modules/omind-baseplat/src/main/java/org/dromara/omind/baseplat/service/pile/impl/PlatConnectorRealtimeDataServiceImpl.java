package org.dromara.omind.baseplat.service.pile.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.entity.PlatConnectorRealtimeData;
import org.dromara.omind.baseplat.domain.service.PlatConnectorRealtimeDataIService;
import org.dromara.omind.baseplat.service.pile.PlatConnectorRealtimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PlatConnectorRealtimeDataServiceImpl implements PlatConnectorRealtimeDataService {

    @Autowired
    @Lazy
    PlatConnectorRealtimeDataIService iService;

    @Autowired
    @Lazy
    PlatConnectorRealtimeDataService selfService;

    @Override
    public List<PlatConnectorRealtimeData> list4TradeNo(String tradeNo) {
        return selfService.list4TradeNo(tradeNo, false);
    }

    @Override
    public List<PlatConnectorRealtimeData> list4TradeNo(String tradeNo, boolean refreshCache) {
        if(TextUtils.isBlank(tradeNo)){
            return new ArrayList<>();
        }
        else if(tradeNo.matches("^[0]+$")){  //如果不全是0
            return new ArrayList<>();
        }
        String key = HlhtRedisKey.PLAT_CONNECTOR_REALTIME_DATA_LIST + tradeNo;
        if(refreshCache){
            RedisUtils.deleteObject(key);
        }
        else {
            if (RedisUtils.hasKey(key)) {
                List<PlatConnectorRealtimeData> list = RedisUtils.getCacheObject(key);
                if(list != null){
                    return list;
                }
            }
        }
        LambdaQueryWrapper<PlatConnectorRealtimeData> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(PlatConnectorRealtimeData::getTradeNo, tradeNo);
        lambdaQueryWrapper.orderByAsc(PlatConnectorRealtimeData::getCreateTime);
        List<PlatConnectorRealtimeData> list = iService.list(lambdaQueryWrapper);
        if(list != null) {
            RedisUtils.setCacheObject(key, list, Duration.ofSeconds(119));
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean save(PlatConnectorRealtimeData data) {
        if(data == null){
            return false;
        }
        if(iService.save(data)){
            if(!TextUtils.isBlank(data.getTradeNo())){
                if (!data.getTradeNo().matches("^[0]+$")) {
                    //如果不全是0
                    String key = HlhtRedisKey.PLAT_CONNECTOR_REALTIME_DATA_LIST + data.getTradeNo();
                    List<PlatConnectorRealtimeData> list = null;
                    if(RedisUtils.hasKey(key)){
                        list = RedisUtils.getCacheObject(key);
                        if(list == null){
                            list = new ArrayList<>();
                        }
                    }
                    else{
                        list = new ArrayList<>();
                    }
                    list.add(data);
                    RedisUtils.setCacheObject(key, list);
                }
            }
            return true;
        }
        else{
            log.error("[平台实时数据保存失败] connectorId=" + data.getConnectorId());
            return false;
        }
    }

    @Override
    public boolean saveBatch(List<PlatConnectorRealtimeData> dataList) {
        if(dataList == null || dataList.size() <= 0){
            return false;
        }
        if(iService.saveBatch(dataList)){
            for(PlatConnectorRealtimeData data : dataList) {
                if (!TextUtils.isBlank(data.getTradeNo())) {
                    if (!data.getTradeNo().matches("^[0]+$")) {
                        //如果不全是0
                        String key = HlhtRedisKey.PLAT_CONNECTOR_REALTIME_DATA_LIST + data.getTradeNo();
                        List<PlatConnectorRealtimeData> list = null;
                        if (RedisUtils.hasKey(key)) {
                            list = RedisUtils.getCacheObject(key);
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                        } else {
                            list = new ArrayList<>();
                        }
                        list.add(data);
                        RedisUtils.setCacheObject(key, list);
                    }
                }
            }
            return true;
        }
        else{
            log.error("[平台实时数据保存失败] connectorIds=" + JSON.toJSONString(dataList));
            return false;
        }
    }

}
