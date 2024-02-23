package org.dromara.omind.baseplat.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.StationFeeDetailData;
import org.dromara.omind.baseplat.api.domain.entity.SysPrice;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.api.domain.entity.SysStationPrice;
import org.dromara.omind.baseplat.domain.response.PriceInfoResponse;
import org.dromara.omind.baseplat.domain.vo.PriceInfoData;
import org.dromara.omind.baseplat.domain.request.PriceAddRequest;
import org.dromara.omind.baseplat.domain.request.PriceEditRequest;
import org.dromara.omind.baseplat.domain.request.PriceLinkStationsRequest;
import org.dromara.omind.baseplat.domain.service.SysPriceIService;
import org.dromara.omind.baseplat.domain.vo.PriceListDto;
import org.dromara.omind.baseplat.domain.vo.PriceTypeInfoData;
import org.dromara.omind.baseplat.service.*;
import org.dromara.omind.mq.api.producer.PriceSendProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;

@Log4j2
@Service
public class SysPriceServiceImpl implements SysPriceService {

    @Autowired
    @Lazy
    SysPriceIService iService;

    @Autowired
    @Lazy
    SysPriceService selfService;

    @Autowired
    @Lazy
    SysStationPriceService stationPriceService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysStationService stationService;

    @Autowired
    PriceSendProducer priceSendProducer;

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Override
    public TableDataInfo getPricePage(String keyword, PageQuery pageQuery) {

        LambdaQueryWrapper<SysPrice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysPrice::getMainPoint, 1);
        if(!TextUtils.isBlank(keyword)) {
            queryWrapper.like(SysPrice::getRemark, keyword);
        }
        queryWrapper.select(SysPrice::getPriceCode, SysPrice::getRemark, SysPrice::getUpdateTime);
        Page<SysPrice> page = iService.page(pageQuery.build(), queryWrapper);
        List<PriceListDto> priceListDtoList = new ArrayList<>();
        for(SysPrice sysPrice : page.getRecords()){
            PriceListDto priceListDto = new PriceListDto();
            priceListDto.setPriceCode(sysPrice.getPriceCode());
            priceListDto.setRemark(sysPrice.getRemark());
            if(sysPrice.getUpdateTime() != null) {
                priceListDto.setTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sysPrice.getUpdateTime()));
            }
            priceListDtoList.add(priceListDto);
        }
        Page<PriceListDto> newPage = new Page<>();
        newPage.setPages(page.getPages());
        newPage.setRecords(priceListDtoList);
        newPage.setSize(page.getSize());
        newPage.setCurrent(page.getCurrent());
        newPage.setTotal(page.getTotal());
        return TableDataInfo.build(newPage);
    }

    @Override
    public PriceInfoResponse getPriceInfo(Long priceCode) {
        PriceInfoResponse priceInfoResponse = new PriceInfoResponse();

        LambdaQueryWrapper<SysPrice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysPrice::getPriceCode, priceCode);
        queryWrapper.orderByAsc(SysPrice::getStartTime);
        List<SysPrice> list = iService.list(queryWrapper);
        if(list == null || list.size() == 0 && priceCode != 0){
            return selfService.getPriceInfo(0L);
        }
        List<PriceInfoData> priceInfoDataList = new ArrayList<>();
        List<PriceTypeInfoData> priceTypeInfoDataList = new ArrayList<>();
        PriceTypeInfoData type0 = new PriceTypeInfoData((short)0);
        PriceTypeInfoData type1 = new PriceTypeInfoData((short)1);
        PriceTypeInfoData type2 = new PriceTypeInfoData((short)2);
        PriceTypeInfoData type3 = new PriceTypeInfoData((short)3);
        priceTypeInfoDataList.add(type0);
        priceTypeInfoDataList.add(type1);
        priceTypeInfoDataList.add(type2);
        priceTypeInfoDataList.add(type3);

        for(SysPrice price : list){
            if(!TextUtils.isBlank(price.getRemark()) && TextUtils.isBlank(priceInfoResponse.getRemark())){
                priceInfoResponse.setRemark(price.getRemark());
            }
            PriceInfoData data = new PriceInfoData();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(price.getStartTime());
            data.setStartHour(calendar.get(Calendar.HOUR_OF_DAY));

            data.setPriceType(price.getPriceType());
            data.setElecPrice(price.getElecPrice());
            data.setServicePrice(price.getServicePrice());
            priceInfoDataList.add(data);

            if(price.getPriceType() == 0){
                type0.setElecPrice(price.getElecPrice());
                type0.setServicePrice(price.getServicePrice());
            }
            else if(price.getPriceType() == 1){
                type1.setElecPrice(price.getElecPrice());
                type1.setServicePrice(price.getServicePrice());
            }
            else if(price.getPriceType() == 2){
                type2.setElecPrice(price.getElecPrice());
                type2.setServicePrice(price.getServicePrice());
            }
            else if(price.getPriceType() == 3){
                type3.setElecPrice(price.getElecPrice());
                type3.setServicePrice(price.getServicePrice());
            }
        }
        priceInfoResponse.setPriceCode(priceCode);
        priceInfoResponse.setPriceList(priceInfoDataList);
        priceInfoResponse.setPriceTypeList(priceTypeInfoDataList);

        return priceInfoResponse;
    }

    @Override
    public PriceInfoResponse getStationPriceInfo(String stationId) {
        SysStationPrice sysStationPrice = stationPriceService.get(stationId);
        Long priceCode = 0L;
        if(sysStationPrice != null){
            priceCode = sysStationPrice.getPriceCode();
        }
        PriceInfoResponse priceInfoResponse = selfService.getPriceInfo(priceCode);
        priceInfoResponse.setStationId(stationId);
        return priceInfoResponse;
    }

    @Override
    public Long addPrice(PriceAddRequest priceAddRequest) {
        //锁
        Long maxPriceCode = selfService.getMaxPriceCode();

        List<SysPrice> priceList = new ArrayList<>();

        //测试价格结构是否正确
        isPriceInfoValid(priceAddRequest.getPriceList());

        Date upTime = new Date();
        for(PriceInfoData priceInfoData : priceAddRequest.getPriceList()){

            SysPrice sysPrice = new SysPrice();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2000);
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, priceInfoData.getStartHour());
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            sysPrice.setStartTime(calendar.getTime());
            sysPrice.setServicePrice(priceInfoData.getServicePrice());
            sysPrice.setElecPrice(priceInfoData.getElecPrice());
            sysPrice.setPriceType(priceInfoData.getPriceType());
            sysPrice.setPriceCode(maxPriceCode);
            if(priceInfoData.getStartHour() == 0){
                sysPrice.setRemark(priceAddRequest.getRemark());
                sysPrice.setMainPoint((short)1);
            }
            sysPrice.setUpdateTime(upTime);
            priceList.add(sysPrice);
        }

        if(!iService.saveBatch(priceList)){
            throw new BaseException("价格保存失败");
        }
        return maxPriceCode;
    }

    @Override
    public void editPrice(PriceEditRequest priceEditRequest) {

        LambdaUpdateWrapper<SysPrice> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(SysPrice::getPriceCode, priceEditRequest.getPriceCode());
        updateWrapper.eq(SysPrice::getMainPoint, 1);
        updateWrapper.set(SysPrice::getRemark, priceEditRequest.getRemark());
        if(!iService.update(updateWrapper)){
            throw new BaseException("更新失败");
        }
    }

    @Override
    public void linkStations(PriceLinkStationsRequest priceLinkStationsRequest) {
        if(priceLinkStationsRequest.getStationIds() == null || priceLinkStationsRequest.getStationIds().size() <= 0){
            throw new BaseException("无效的站点列表");
        }
        if(priceLinkStationsRequest.getPriceCode() == null || priceLinkStationsRequest.getPriceCode() < 0){
            throw new BaseException("无效的价格编码");
        }
        //todo
        if(priceLinkStationsRequest.getStationIds() != null && priceLinkStationsRequest.getPriceCode() >= 0){
            for(String stationId : priceLinkStationsRequest.getStationIds()){
                stationPriceService.set(stationId, priceLinkStationsRequest.getPriceCode(), priceLinkStationsRequest.getRemark());
            }
            for(String stationId : priceLinkStationsRequest.getStationIds()){
                //无异常，加入价格推送队列
                List<String> equipmentList = equipmentService.getAllEquipmentIdByStationId(stationId);
                for(String equipmentId : equipmentList) {
                    priceSendProducer.sendMsg(equipmentId);
                }
            }
        }
    }

    @Override
    public void removePrice(Long priceCode) {
        //首先检查有无关联station
        if(priceCode == 0){
            throw new BaseException("默认模版无法删除");
        }
        else if(priceCode <= 0){
            throw new BaseException("无效的价格编码");
        }

        List<String> list = stationPriceService.getLinkStations(priceCode);
        if(list != null && list.size() > 0){
            throw new BaseException("不可删除，有" + list.size() + "个关联的站点。" + JsonUtils.toJsonString(list));
        }

        if(!iService.removeById(priceCode)){
            throw new BaseException("价格删除失败");
        }
    }

    @Override
    public Long getMaxPriceCode() {
        //todo add redis
        LambdaQueryWrapper<SysPrice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(SysPrice::getPriceCode);
        queryWrapper.orderByDesc(SysPrice::getPriceCode);
        queryWrapper.last("limit 1");
        SysPrice sysPrice = iService.getOne(queryWrapper);
        if(sysPrice == null){
            return 1L;
        }
        else{
            return sysPrice.getPriceCode() + RandomUtil.randomLong(1,5);
        }
    }

    @Override
    public List<PolicyInfoData> getHlhtPriceByCode(Long priceCode) {
        List<PolicyInfoData> policyInfoDataList = new ArrayList<>();

        if(priceCode == null || priceCode < 0){
            //todo 返回默认价格
            log.info("未找到价格模版，返回默认价格");
            return policyInfoDataList;
        }
        log.info("价格模版ID：" + priceCode);

        LambdaQueryWrapper<SysPrice> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysPrice::getPriceCode, priceCode);
        lambdaQueryWrapper.orderByAsc(SysPrice::getStartTime);
        List<SysPrice> priceList = iService.list(lambdaQueryWrapper);
        if(priceList != null){
            for(int i = 0; i < 24; i++) {
                PolicyInfoData policyInfoData = new PolicyInfoData();
                policyInfoData.setStartTime(String.format("%02d0000", i));
                SysPrice cPrice = null;    //命中价格
                for(SysPrice price : priceList){
                    if(cPrice == null){
                        cPrice = price;
                        continue;
                    }
                    if(i >= price.getHour() && ( (i - price.getHour()) < (i - cPrice.getHour()))){
                        cPrice = price;
                    }
                }
                policyInfoData.setElecPrice(cPrice.getElecPrice());
                policyInfoData.setSevicePrice(cPrice.getServicePrice());
                policyInfoData.setPriceType(cPrice.getPriceType()==null?(short)2:cPrice.getPriceType());
                policyInfoDataList.add(policyInfoData);
            }
        }
        //todo 增加缓存
        return combine(policyInfoDataList);
    }

    @Override
    public List<PolicyInfoData> getHlhtPrice4Station(String stationId) {
        List<PolicyInfoData> policyInfoDataList = new ArrayList<>();
        SysStationPrice sysStationPrice = stationPriceService.get(stationId);
        if(sysStationPrice == null){
            //todo 获取默认价格
            log.info("获取默认价格");
            return policyInfoDataList;
        }

        log.info("价格关联模版：" + JsonUtils.toJsonString(sysStationPrice));
        Long priceCode = sysStationPrice.getPriceCode();
        policyInfoDataList = selfService.getHlhtPriceByCode(priceCode);
        //todo 增加缓存
        return policyInfoDataList;
    }

    @Override
    public List<PolicyInfoData> getHlhtConnectorPriceList(String connectorId) {
        SysStation sysStation = connectorService.getStationInfoByConnectorId(connectorId);
        return selfService.getHlhtPrice4Station(sysStation.getStationId());
    }

    @Override
    public PolicyInfoData getHlhtCurrentPrice(String stationId, Long ts) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        int findHour = calendar.get(Calendar.HOUR_OF_DAY);
        String tmpKey = "SysConnectorPriceServiceImpl_getCurrentPrice_v2_" + stationId + "_" + findHour;
        if(RedisUtils.hasKey(tmpKey)){
            PolicyInfoData data = RedisUtils.getCacheObject(tmpKey);
            if(data != null){
                return data;
            }
        }

        List<PolicyInfoData> policyInfoDataList = selfService.getHlhtPrice4Station(stationId);
        if(policyInfoDataList == null || policyInfoDataList.size() == 0)
        {
            return null;
        }
        PolicyInfoData rightPrice = policyInfoDataList.get(0);
        for(PolicyInfoData policyInfoData : policyInfoDataList){
            if(rightPrice == null){
                rightPrice = policyInfoData;
                continue;
            }
            if(!TextUtils.isBlank(policyInfoData.getStartTime()) && policyInfoData.getStartTime().length() > 2){
                int hour = Integer.valueOf(policyInfoData.getStartTime().substring(0,2));
                if(hour <= findHour){
                    rightPrice = policyInfoData;
                }
            }
        }
        if(rightPrice != null){
            RedisUtils.setCacheObject(tmpKey, rightPrice, Duration.ofMinutes(10));
        }
        return rightPrice;
    }

    List<PolicyInfoData> combine(List<PolicyInfoData> priceList)
    {
        List<PolicyInfoData> newPriceList = new ArrayList();
        PolicyInfoData newData = null;
        for(PolicyInfoData policyInfoData : priceList){
            if(newData == null){
                newData = policyInfoData;
                continue;
            }
            if(policyInfoData.getSevicePrice().compareTo(newData.getSevicePrice()) == 0
                    && policyInfoData.getPriceType().compareTo(newData.getPriceType()) == 0
                    && policyInfoData.getElecPrice().compareTo(newData.getElecPrice()) == 0){
                //一样的价格
                continue;
            }
            else{
                newPriceList.add(newData);
                newData = policyInfoData;
            }

        }
        if(newData != null){
            newPriceList.add(newData);
        }

        if(newPriceList.size() == 0 || newPriceList.size() >= priceList.size()){
            return priceList;
        }
        return newPriceList;
    }

    private Short getPriceType(StationFeeDetailData detailData, List<StationFeeDetailData> feeList)
    {
        List<BigDecimal> priceList = new ArrayList<>();
        Set<String> repeatSet = new HashSet<>();
        BigDecimal nowPrice = detailData.getServiceFee().add(detailData.getElectricityFee()).setScale(4, RoundingMode.UP);
        for(StationFeeDetailData data : feeList){
            BigDecimal dataPrice = data.getServiceFee().add(data.getElectricityFee()).setScale(4, RoundingMode.UP);
            String key = dataPrice.toPlainString();
            if(!repeatSet.contains(key)){
                priceList.add(dataPrice);
                repeatSet.add(key);
            }
        }
        //从大到小
        priceList.sort(new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o2.compareTo(o1);
            }
        });

        short index = 0;
        int total = priceList.size();
        for(int i = 0;i<total;i++){
            BigDecimal price = priceList.get(i);
            if(nowPrice.compareTo(price) >= 0) {
                index = (short)i;
                break;
            }
        }
        if(total <= 4){
            if(total == 4) {
                if (index > 3) {
                    return Short.valueOf((short)3);
                }
                return Short.valueOf(index);
            }
            else if(total == 3){
                return Short.valueOf((short)(index + 1));
            }
            else if(total == 2){
                return Short.valueOf((short)(index + 2));
            }
            else{
                return (short)2;
            }
        }
        else if(index < 2){
            return Short.valueOf(index);
        }
        else if(index > total - 3){
            return Short.valueOf((short)(3 - (total - index - 1)));
        }
        else{
            return Short.valueOf((short)2);
        }
    }

    void isPriceInfoValid(List<PriceInfoData> priceInfoDataList){

        if(priceInfoDataList == null || priceInfoDataList.size() == 0){
            throw new BaseException("无效的价格信息-无数据");
        }
        if(priceInfoDataList.get(0).getStartHour() != 0){
            throw new BaseException("价格列表应从时刻0开始");
        }

        BigDecimal t0s = new BigDecimal("-0.0001");
        BigDecimal t0e = new BigDecimal("-0.0001");
        BigDecimal t1s = new BigDecimal("-0.0001");
        BigDecimal t1e = new BigDecimal("-0.0001");
        BigDecimal t2s = new BigDecimal("-0.0001");
        BigDecimal t2e = new BigDecimal("-0.0001");
        BigDecimal t3s = new BigDecimal("-0.0001");
        BigDecimal t3e = new BigDecimal("-0.0001");

        int startHour = -1;

        for(PriceInfoData priceInfoData : priceInfoDataList){
            if(priceInfoData.getStartHour() <= startHour){
                throw new BaseException("请按照时间从小到大顺序提交价格");
            }
            startHour = priceInfoData.getStartHour();

            if(priceInfoData.getPriceType() == 0){
                if(t0s.floatValue() >= 0){
                    if(t0s.compareTo(priceInfoData.getServicePrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t0s = priceInfoData.getServicePrice().setScale(4, RoundingMode.HALF_EVEN);
                }

                if(t0e.floatValue() >= 0){
                    if(t0e.compareTo(priceInfoData.getElecPrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t0e = priceInfoData.getElecPrice().setScale(4, RoundingMode.HALF_EVEN);
                }
            }
            else if(priceInfoData.getPriceType() == 1)
            {
                if(t1s.floatValue() >= 0){
                    if(t1s.compareTo(priceInfoData.getServicePrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t1s = priceInfoData.getServicePrice().setScale(4, RoundingMode.HALF_EVEN);
                }

                if(t1e.floatValue() >= 0){
                    if(t1e.compareTo(priceInfoData.getElecPrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t1e = priceInfoData.getElecPrice().setScale(4, RoundingMode.HALF_EVEN);
                }
            }
            else if(priceInfoData.getPriceType() == 2)
            {
                if(t2s.floatValue() >= 0){
                    if(t2s.compareTo(priceInfoData.getServicePrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t2s = priceInfoData.getServicePrice().setScale(4, RoundingMode.HALF_EVEN);
                }

                if(t2e.floatValue() >= 0){
                    if(t2e.compareTo(priceInfoData.getElecPrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t2e = priceInfoData.getElecPrice().setScale(4, RoundingMode.HALF_EVEN);
                }
            }
            else if(priceInfoData.getPriceType() == 3)
            {
                if(t3s.floatValue() >= 0){
                    if(t3s.compareTo(priceInfoData.getServicePrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t3s = priceInfoData.getServicePrice().setScale(4, RoundingMode.HALF_EVEN);
                }

                if(t3e.floatValue() >= 0){
                    if(t3e.compareTo(priceInfoData.getElecPrice()) != 0){
                        throw new BaseException("尖时服务费不一致");
                    }
                }
                else{
                    t3e = priceInfoData.getElecPrice().setScale(4, RoundingMode.HALF_EVEN);
                }
            }
        }
    }
}
