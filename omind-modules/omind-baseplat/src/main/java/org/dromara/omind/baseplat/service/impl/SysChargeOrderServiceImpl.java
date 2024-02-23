package org.dromara.omind.baseplat.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.ConnectorStatsInfoData;
import org.dromara.omind.baseplat.api.domain.dto.SysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.domain.service.SysChargeOrderIService;
import org.dromara.omind.baseplat.service.SysChargeOrderService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Log4j2
@Service
public class SysChargeOrderServiceImpl implements SysChargeOrderService {


    @Autowired
    @Lazy
    SysChargeOrderIService iService;

    @Autowired
    @Lazy
    SysChargeOrderService selfService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Override
    public ConnectorStatsInfoData getStatsInfo(String connectorId, long start, long end) {
        if(TextUtils.isBlank(connectorId) || start > end){
            return new ConnectorStatsInfoData();
        }
        Date startDate = new Date(start);
        Date endDate = new Date(end);

        LambdaQueryWrapper<SysChargeOrder> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.select(SysChargeOrder::getTotalPower);
        lambdaQuery.eq(SysChargeOrder::getConnectorId, connectorId);
        lambdaQuery.ge(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, startDate));
        lambdaQuery.le(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endDate));
        List<SysChargeOrder> orderList = iService.list(lambdaQuery);

        ConnectorStatsInfoData infoData = new ConnectorStatsInfoData();
        infoData.setConnectorID(connectorId);
        BigDecimal total = new BigDecimal(0.00);
        for(SysChargeOrder sysChargeOrder : orderList){
            if(sysChargeOrder.getTotalPower() != null) {
                total = total.add(sysChargeOrder.getTotalPower());
            }
        }
        infoData.setConnectorElectricity(total.setScale(1, RoundingMode.DOWN));
        return infoData;
    }

    @Override
    public SysChargeOrder getChargeOrderByStartChargeSeq(String startChargeSeq) {
        if(TextUtils.isBlank(startChargeSeq)){
            return null;
        }
        String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ + startChargeSeq;
        SysChargeOrder sysChargeOrder = RedisUtils.getCacheObject(key);
        if(sysChargeOrder != null){
            return sysChargeOrder;
        }
        LambdaQueryWrapper<SysChargeOrder> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysChargeOrder::getStartChargeSeq, startChargeSeq);
        lambdaQuery.orderByDesc(SysChargeOrder::getId);
        List<SysChargeOrder> list = iService.list(lambdaQuery);
        if(list == null || list.size() == 0){
            return null;
        }
        else{
            sysChargeOrder = list.get(0);
            RedisUtils.setCacheObject(key, sysChargeOrder);
            return sysChargeOrder;
        }
    }

    @Override
    public SysChargeOrder getChargeOrderByTradeNo(String tradeNo) {
        if(TextUtils.isBlank(tradeNo)){
            return null;
        }
        String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_TRADENO + tradeNo;
        SysChargeOrder sysChargeOrder = RedisUtils.getCacheObject(key);
        if(sysChargeOrder != null){
            return sysChargeOrder;
        }
        LambdaQueryWrapper<SysChargeOrder> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysChargeOrder::getTradeNo, tradeNo);
        lambdaQuery.orderByDesc(SysChargeOrder::getId).last("limit 1");
        sysChargeOrder = iService.getOne(lambdaQuery);
        if(sysChargeOrder != null){
            RedisUtils.setCacheObject(key, sysChargeOrder);
        }
        return sysChargeOrder;
    }

    @Override
    public SysChargeOrder getChargingOrderByConnectorId(String connectorId) {
        //充电订单只可能是最新的一个订单
        if(TextUtils.isBlank(connectorId)){
            return null;
        }
        LambdaQueryWrapper<SysChargeOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysChargeOrder::getConnectorId, connectorId);
        lambdaQueryWrapper.orderByDesc(SysChargeOrder::getId);
        lambdaQueryWrapper.last("limit 1");
        SysChargeOrder sysChargeOrder = iService.getOne(lambdaQueryWrapper);
        return sysChargeOrder;
    }

    @Override
    public List<SysChargeOrder> getUnSyncChargeOrderList() {
        //只获取1月内的未提交订单
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        Date lastDay = calendar.getTime();


        LambdaQueryWrapper<SysChargeOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysChargeOrder::getStartChargeSeqStat, 4);
        lambdaQueryWrapper.le(SysChargeOrder::getSyncFlag, 50);
        lambdaQueryWrapper.gt(SysChargeOrder::getCreateTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, lastDay));
        List<SysChargeOrder> orderList = iService.list(lambdaQueryWrapper);
        if(orderList == null){
            orderList = new ArrayList<>();
        }
        return orderList;
    }

    @Override
    public void dealWithExChargeOrder() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -12);
        Date lastDay = calendar.getTime();

        //先筛选出已经充电了24小时的订单
        LambdaQueryWrapper<SysChargeOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ge(SysChargeOrder::getStartChargeSeqStat, 1);
        lambdaQueryWrapper.le(SysChargeOrder::getStartChargeSeqStat, 3);
        lambdaQueryWrapper.ne(SysChargeOrder::getSyncFlag, 100);
        lambdaQueryWrapper.le(SysChargeOrder::getCreateTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, lastDay));
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date lasthour = calendar.getTime();
        lambdaQueryWrapper.le(SysChargeOrder::getUpdateTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, lasthour));

        List<SysChargeOrder> exList = iService.list(lambdaQueryWrapper);
        List<SysChargeOrder> upList = new ArrayList<>();
        Long nowTm = System.currentTimeMillis();
        if(exList != null && exList.size() > 0){
            for (SysChargeOrder sysChargeOrder : exList) {
                try {
                    //只处理1个小时内确实没有更新的，双保险，上面做了筛选
                    if ((nowTm - sysChargeOrder.getUpdateTime().getTime()) > 3600000) {
                        //状态设置为未知
                        sysChargeOrder.setStartChargeSeqStat((short) 5);
                        String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_TRADENO + sysChargeOrder.getTradeNo();
                        String key2 = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ + sysChargeOrder.getStartChargeSeq();
                        RedisUtils.setCacheObject(key, sysChargeOrder);
                        RedisUtils.setCacheObject(key2, sysChargeOrder);
                        upList.add(sysChargeOrder);
                    }
                } catch (Exception ex) {

                    if(sysChargeOrder != null){
                        log.error("【异常订单处理】异常信息(含订单信息)=》" + JSON.toJSONString(sysChargeOrder), ex);
                    }
                    else{
                        log.error("【异常订单处理】异常信息(订单信息null)=》" + ex.toString(), ex);
                    }
                }
            }
        }
        if(upList.size() > 0){
            if(!iService.updateBatchById(upList)){
                log.error("异常订单处理失败");
            }
        }
    }

    @Override
    public void save(SysChargeOrder sysChargeOrder) throws BaseException {
        if(sysChargeOrder == null || TextUtils.isBlank(sysChargeOrder.getStartChargeSeq()) || TextUtils.isBlank(sysChargeOrder.getTradeNo())){
            throw new BaseException("订单号不存在");
        }
        if(TextUtils.isBlank(sysChargeOrder.getOperatorId())){
            throw new BaseException("运营商ID为空");
        }
        if(TextUtils.isBlank(sysChargeOrder.getConnectorId())){
            throw new BaseException("充电接口编码错误");
        }

        if(TextUtils.isBlank(sysChargeOrder.getStationId())){
            SysStation sysStation = connectorService.getStationInfoByConnectorId(sysChargeOrder.getConnectorId());
            if(sysStation != null){
                sysChargeOrder.setStationId(sysStation.getStationId());
            }
        }

        SysChargeOrder repeateOrder = selfService.getChargeOrderByStartChargeSeq(sysChargeOrder.getStartChargeSeq());
        if(repeateOrder != null){
            throw new BaseException("订单已存在");
        }
        log.info("开始创建订单");
        if(sysChargeOrder.getEndTime() == null){
            sysChargeOrder.setEndTime(sysChargeOrder.getStartTime());
        }
        if(!iService.save(sysChargeOrder)){
            log.error("订单创建失败");
            throw new BaseException("订单创建失败");
        }
        else{
            String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_TRADENO + sysChargeOrder.getTradeNo();
            String key2 = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ + sysChargeOrder.getStartChargeSeq();
            RedisUtils.setCacheObject(key, sysChargeOrder);
            RedisUtils.setCacheObject(key2, sysChargeOrder);
        }
        log.info("完成创建订单");
    }

    @Override
    public void update(SysChargeOrder sysChargeOrder) throws BaseException {
        if(sysChargeOrder == null || sysChargeOrder.getId() == null || sysChargeOrder.getId() <= 0){
            throw new BaseException("订单信息错误");
        }
        if(TextUtils.isBlank(sysChargeOrder.getPlateNum())){
            sysChargeOrder.setPlateNum(null);
        }
        if(TextUtils.isBlank(sysChargeOrder.getPhoneNum())){
            sysChargeOrder.setPhoneNum(null);
        }
        if(TextUtils.isBlank(sysChargeOrder.getCarVin())){
            sysChargeOrder.setCarVin(null);
        }
        if(TextUtils.isBlank(sysChargeOrder.getStartChargeSeq())){
            sysChargeOrder.setStartChargeSeq(null);
        }
        log.info("[开始更新订单]" + JSON.toJSONString(sysChargeOrder));

        if(!iService.updateById(sysChargeOrder)){
            log.error("[更新订单信息]更新数据库失败 StartChargeSeq = " + sysChargeOrder.getStartChargeSeq());
        }
        String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_TRADENO + sysChargeOrder.getTradeNo();
        String key2 = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ + sysChargeOrder.getStartChargeSeq();
        RedisUtils.deleteObject(key);
        RedisUtils.deleteObject(key2);
    }

    @Override
    public TableDataInfo<SysChargeOrderDto> getChargeOrderList(QuerySysChargeOrderDto querySysChargeOrderDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysChargeOrder> lambdaQuery = Wrappers.lambdaQuery();
        if(querySysChargeOrderDto != null){

            if(!TextUtils.isBlank(querySysChargeOrderDto.getConnectorId())){
                lambdaQuery.like(SysChargeOrder::getConnectorId, querySysChargeOrderDto.getConnectorId());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getStartChargeSeq())){
                lambdaQuery.eq(SysChargeOrder::getStartChargeSeq, querySysChargeOrderDto.getStartChargeSeq());
            }
            if(querySysChargeOrderDto.getStartChargeSeqStat() != null
                    && querySysChargeOrderDto.getStartChargeSeqStat() > 0
                    && querySysChargeOrderDto.getStartChargeSeqStat() < 6){
                lambdaQuery.eq(SysChargeOrder::getStartChargeSeqStat, querySysChargeOrderDto.getStartChargeSeqStat());
            }
            if(querySysChargeOrderDto.getStartTm() != null){
                lambdaQuery.ge(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, querySysChargeOrderDto.getStartTm()));
            }
            if(querySysChargeOrderDto.getEndTm() != null){
                lambdaQuery.le(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, querySysChargeOrderDto.getEndTm()));
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getCarVin())){
                lambdaQuery.eq(SysChargeOrder::getCarVin, querySysChargeOrderDto.getCarVin());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getPhoneNum())){
                lambdaQuery.eq(SysChargeOrder::getPhoneNum, querySysChargeOrderDto.getPhoneNum());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getPlateNum())){
                lambdaQuery.like(SysChargeOrder::getPlateNum, querySysChargeOrderDto.getPlateNum());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getOperatorId())){
                lambdaQuery.eq(SysChargeOrder::getOperatorId, querySysChargeOrderDto.getOperatorId());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getStationId())){
                List<String> equipmentList = equipmentService.getAllEquipmentIdByStationId(querySysChargeOrderDto.getStationId());
                Set<String> idSet = new HashSet<>();
                for(String equipId : equipmentList){
                    List<String> connectorIdList = connectorService.getAllIdByEquipmentId(equipId);
                    if(connectorIdList != null){
                        for(String cId : connectorIdList){
                            if(!idSet.contains(cId)){
                                idSet.add(cId);
                            }
                        }
                    }
                }
                if(idSet.size() == 0){
                    lambdaQuery.eq(SysChargeOrder::getConnectorId, "---");
                }
                else{
                    lambdaQuery.in(SysChargeOrder::getConnectorId, idSet);
                }
            }

        }
        lambdaQuery.orderByDesc(SysChargeOrder::getId);

        Page<SysChargeOrder> iPage = iService.page(pageQuery.build(), lambdaQuery);

        List<SysChargeOrderDto> dtoList = new ArrayList<>();
        if(iPage != null && iPage.getRecords() != null) {
            for(SysChargeOrder sysChargeOrder : iPage.getRecords()){
                String key = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_TRADENO + sysChargeOrder.getTradeNo();
                String key2 = HlhtRedisKey.SYS_CHARGE_ORDER_INFO_BY_CHARGESEQ + sysChargeOrder.getStartChargeSeq();
                RedisUtils.setCacheObject(key, sysChargeOrder);
                RedisUtils.setCacheObject(key2, sysChargeOrder);

                SysChargeOrderDto sysChargeOrderDto = SysChargeOrderDto.build(sysChargeOrder);
                if(!TextUtils.isBlank(sysChargeOrder.getConnectorId())) {
                    SysStation sysStation = connectorService.getStationInfoByConnectorId(sysChargeOrder.getConnectorId());
                    if(sysStation != null) {
                        sysChargeOrderDto.setStationId(sysStation.getStationId());
                        sysChargeOrderDto.setStationName(sysStation.getStationName());
                    }
                }
                if(!TextUtils.isBlank(sysChargeOrder.getOperatorId())){
                    SysOperator sysOperator = operatorService.getOperatorById(sysChargeOrder.getOperatorId());
                    if(sysOperator != null){
                        sysChargeOrderDto.setOperatorName(sysOperator.getOperatorName());
                    }
                }
                dtoList.add(sysChargeOrderDto);
            }
        }
        Page<SysChargeOrderDto> pageInfo = new Page<>();
        pageInfo.setSize(iPage.getSize());
        pageInfo.setTotal(iPage.getTotal());
        pageInfo.setPages(iPage.getPages());
        pageInfo.setRecords(dtoList);

        return TableDataInfo.build(pageInfo);
    }

    @Override
    public List<SysChargeOrder> getChargeOrderList(QuerySysChargeOrderDto querySysChargeOrderDto) {
        LambdaQueryWrapper<SysChargeOrder> lambdaQuery = Wrappers.lambdaQuery();
        if(querySysChargeOrderDto != null){

            if(!TextUtils.isBlank(querySysChargeOrderDto.getConnectorId())){
                lambdaQuery.like(SysChargeOrder::getConnectorId, querySysChargeOrderDto.getConnectorId());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getStartChargeSeq())){
                lambdaQuery.eq(SysChargeOrder::getStartChargeSeq, querySysChargeOrderDto.getStartChargeSeq());
            }
            if(querySysChargeOrderDto.getStartChargeSeqStat() != null
                    && querySysChargeOrderDto.getStartChargeSeqStat() > 0
                    && querySysChargeOrderDto.getStartChargeSeqStat() < 6){
                lambdaQuery.eq(SysChargeOrder::getStartChargeSeqStat, querySysChargeOrderDto.getStartChargeSeqStat());
            }
            if(querySysChargeOrderDto.getStartTm() != null){
                lambdaQuery.ge(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, querySysChargeOrderDto.getStartTm()));
            }
            if(querySysChargeOrderDto.getEndTm() != null){
                lambdaQuery.le(SysChargeOrder::getEndTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, querySysChargeOrderDto.getEndTm()));
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getCarVin())){
                lambdaQuery.eq(SysChargeOrder::getCarVin, querySysChargeOrderDto.getCarVin());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getPhoneNum())){
                lambdaQuery.eq(SysChargeOrder::getPhoneNum, querySysChargeOrderDto.getPhoneNum());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getPlateNum())){
                lambdaQuery.like(SysChargeOrder::getPlateNum, querySysChargeOrderDto.getPlateNum());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getOperatorId())){
                lambdaQuery.eq(SysChargeOrder::getOperatorId, querySysChargeOrderDto.getOperatorId());
            }
            if(!TextUtils.isBlank(querySysChargeOrderDto.getStationId())){
                lambdaQuery.eq(SysChargeOrder::getStationId, querySysChargeOrderDto.getStationId());
            }
        }
        lambdaQuery.orderByDesc(SysChargeOrder::getId);

        List<SysChargeOrder> list = iService.list(lambdaQuery);
        return list;
    }

}
