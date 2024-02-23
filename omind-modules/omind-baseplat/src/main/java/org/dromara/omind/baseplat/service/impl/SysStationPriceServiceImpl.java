package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysStationPrice;
import org.dromara.omind.baseplat.domain.response.PriceInfoResponse;
import org.dromara.omind.baseplat.domain.service.SysStationPriceIService;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.dromara.omind.baseplat.service.SysStationPriceService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.dromara.omind.mq.api.producer.StationPriceSendProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysStationPriceServiceImpl implements SysStationPriceService {

    @Autowired
    @Lazy
    SysStationPriceIService iService;

    @Autowired
    @Lazy
    SysStationPriceService selfService;

    @Autowired
    @Lazy
    SysStationService stationService;

    @Autowired
    @Lazy
    SysPriceService priceService;

    @Autowired
    @Lazy
    StationPriceSendProducer stationPriceSendProducer;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void set(String stationId, Long priceCode, String remark) {
        //todo 增加redis
        //判断stationId是否有效
        if(null == stationService.getStationById(stationId)){
            throw new BaseException("无效的站点ID" + stationId);
        }
        PriceInfoResponse priceInfos = priceService.getPriceInfo(priceCode);
        if(priceInfos == null || priceInfos.getPriceList() == null || priceInfos.getPriceTypeList().size() == 0){
            throw new BaseException("无效的价格编码" + priceCode);
        }
        SysStationPrice oldPrice = selfService.get(stationId);
        if(oldPrice != null && oldPrice.getPriceCode() == priceCode){
            return;
        }

        SysStationPrice sysStationPrice = new SysStationPrice();
        sysStationPrice.setStationId(stationId);
        sysStationPrice.setPriceCode(priceCode);
        sysStationPrice.setPriceType(0);
        sysStationPrice.setRemark(remark);
        sysStationPrice.setIsUse((short)1);
        sysStationPrice.setCreateById(0L);
        sysStationPrice.setCreateTime(new Date());
        sysStationPrice.setUpdateById(0L);
        sysStationPrice.setUpdateTime(new Date());
        Boolean isSuccess = iService.save(sysStationPrice);
        if(null == oldPrice){
            if(!isSuccess){
                throw new BaseException("保存失败");
            }
        }
        else{
            LambdaUpdateWrapper<SysStationPrice> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(SysStationPrice::getStationId, stationId);
            updateWrapper.ne(SysStationPrice::getId, sysStationPrice.getId());
            updateWrapper.eq(SysStationPrice::getIsUse, 1);
            updateWrapper.set(SysStationPrice::getIsUse, 0);
            if(!iService.update(updateWrapper)){
                throw new BaseException("更新价格失败");
            }
            stationPriceSendProducer.sendMsg(stationId);
        }
    }

    @Override
    public SysStationPrice get(String stationId) {
        //todo 增加redis
        LambdaQueryWrapper<SysStationPrice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStationPrice::getStationId, stationId);
        queryWrapper.eq(SysStationPrice::getIsUse, 1);
        queryWrapper.orderByDesc(SysStationPrice::getId);
        queryWrapper.last("limit 1");
        SysStationPrice sysStationPrice = iService.getOne(queryWrapper);
        return sysStationPrice;
    }

    @Override
    public List<String> getLinkStations(Long priceCode) {
        LambdaQueryWrapper<SysStationPrice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStationPrice::getPriceCode, priceCode);
        queryWrapper.eq(SysStationPrice::getIsUse, 1);
        queryWrapper.select(SysStationPrice::getStationId);
        List<SysStationPrice> list = iService.list(queryWrapper);
        List<String> stationIdList = new ArrayList<>();
        for(SysStationPrice sysStationPrice : list){
            stationIdList.add(sysStationPrice.getStationId());
        }
        return stationIdList;
    }
}
