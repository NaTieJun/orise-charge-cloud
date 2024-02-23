package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.ChargeDetailData;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrderItem;
import org.dromara.omind.baseplat.domain.service.SysChargeOrderItemIService;
import org.dromara.omind.baseplat.service.SysChargeOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class SysChargeOrderItemServiceImpl implements SysChargeOrderItemService {

    @Autowired
    SysChargeOrderItemIService iService;

    @Override
    public List<ChargeDetailData> getList4StartChargeSeq(String startChargeSeq) {
        List<ChargeDetailData> resultList = new ArrayList<>();

        if(TextUtils.isBlank(startChargeSeq)){
            return resultList;
        }
        String key = HlhtRedisKey.SYS_CHARGE_ORDER_ITEM_LIST + startChargeSeq;

        List<ChargeDetailData> dList = RedisUtils.getCacheObject(key);
        if(dList != null){
            return dList;
        }

        LambdaQueryWrapper<SysChargeOrderItem> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysChargeOrderItem::getStartChargeSeq, startChargeSeq);

        List<SysChargeOrderItem> list = iService.list(lambdaQuery);
        if(list == null || list.size() <= 0){
            return resultList;
        }
        for(SysChargeOrderItem sysChargeOrderItem : list){
            ChargeDetailData chargeDetailData = new ChargeDetailData();
            chargeDetailData.setDetailPower(sysChargeOrderItem.getPower());
            chargeDetailData.setDetailElecMoney(sysChargeOrderItem.getElecMoney());
            chargeDetailData.setDetailSeviceMoney(sysChargeOrderItem.getServiceMoney());
            chargeDetailData.setDetailStartTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", sysChargeOrderItem.getStartTime()));
            chargeDetailData.setDetailEndTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", sysChargeOrderItem.getEndTime()));
            chargeDetailData.setElecPrice(sysChargeOrderItem.getElecPrice());
            chargeDetailData.setSevicePrice(sysChargeOrderItem.getSevicePrice());
            resultList.add(chargeDetailData);
        }
        if(resultList != null){
            RedisUtils.setCacheObject(key, resultList);
        }
        return resultList;
    }

    @Override
    public boolean saveBatch(String startChargeSeq, List<SysChargeOrderItem> list) {
        if(TextUtils.isBlank(startChargeSeq) || list == null || list.size() <= 0){
            return false;
        }
        String key = HlhtRedisKey.SYS_CHARGE_ORDER_ITEM_LIST + startChargeSeq;
        RedisUtils.deleteObject(key);

        LambdaQueryWrapper<SysChargeOrderItem> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysChargeOrderItem::getStartChargeSeq, startChargeSeq);
        boolean result = iService.remove(lambdaQueryWrapper);

        try {
            result = iService.saveBatch(list);
        }catch (Exception ex){
            log.error(ex.toString());
        }
        return result;
    }

}
