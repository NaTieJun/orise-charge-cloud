package org.dromara.omind.baseplat.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.omind.baseplat.api.domain.ChargeDetailData;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.entity.PlatConnectorRealtimeData;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrderItem;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationEquipChargeStatusData;
import org.dromara.omind.baseplat.service.*;
import org.dromara.omind.baseplat.service.pile.PlatConnectorRealtimeDataService;
import org.dromara.omind.baseplat.service.pile.PlatTradingRecordDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Log4j2
@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    @Lazy
    SysPriceService priceService;

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Autowired
    @Lazy
    SysChargeOrderItemService chargeOrderItemService;

    @Autowired
    @Lazy
    PlatConnectorRealtimeDataService realtimeDataService;

    @Autowired
    @Lazy
    PlatTradingRecordDataService platTradingRecordDataService;

    @Autowired
    @Lazy
    TradeService selfService;

    @Override
    public List<ChargeDetailData> getChargeOrderDetails(String startChargeSeq, boolean refreshCache) {
        SysChargeOrder chargeOrder = chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
        if(chargeOrder == null){
            //查询订单失败
            return new ArrayList<>();
        }
        List<PlatConnectorRealtimeData> realtimeDataList = realtimeDataService.list4TradeNo(chargeOrder.getTradeNo(), refreshCache);
        if(realtimeDataList == null || realtimeDataList.size() == 0){
            //还没有任何数据上报
            return new ArrayList<>();
        }
        List<PolicyInfoData> priceList = null;
        try{
            if(!TextUtils.isBlank(chargeOrder.getPriceInfo())){
                priceList = JSON.parseArray(chargeOrder.getPriceInfo(), PolicyInfoData.class);
            }
        }
        catch (Exception ex){
            log.error("【充电明细生成】获取充电价格失败" + ex.toString(), ex);
        }

        if(priceList == null || priceList.size() == 0) {
            priceList = priceService.getHlhtConnectorPriceList(chargeOrder.getConnectorId());
        }
        if(priceList == null || priceList.size() == 0){
            return new ArrayList<>();
        }


        List<ChargeDetailData> resultList = new ArrayList<>();

        Date startTm = chargeOrder.getStartTime();
        Date endTm = chargeOrder.getEndTime() == null?(new Date()):chargeOrder.getEndTime() ;
        int startHour = DateUtils.getHour(startTm);
        int endHour = DateUtils.getHour(endTm);
        //如果是一个时间段内充完的
        if(startHour == endHour){
            ChargeDetailData detailData = new ChargeDetailData();
            detailData.setDetailStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, startTm));
            PlatConnectorRealtimeData last = realtimeDataList.get(realtimeDataList.size() - 1);
            detailData.setDetailEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endTm));
            detailData.setDetailPower(last.getChargingKWH().setScale(2, RoundingMode.UP));
            detailData.setElecPrice(getPrice(startHour, priceList).getElecPrice().setScale(4, RoundingMode.UP));
            detailData.setSevicePrice(getPrice(startHour, priceList).getSevicePrice().setScale(4, RoundingMode.UP));
            detailData.setDetailElecMoney(detailData.getElecPrice().multiply(detailData.getDetailPower()).setScale(2, RoundingMode.UP));
            detailData.setDetailSeviceMoney(detailData.getSevicePrice().multiply(detailData.getDetailPower()).setScale(2, RoundingMode.UP));
            resultList.add(detailData);
            return resultList;
        }
        Map<String, PlatConnectorRealtimeData> hourMap = new HashMap<>();

        //找出每个小时时段中最后一次统计数据
        for(PlatConnectorRealtimeData realtimeData : realtimeDataList){
            String key = DateUtils.getHour(realtimeData.getCreateTime()) + "";
            if(hourMap.containsKey(key)){
                PlatConnectorRealtimeData data = hourMap.get(key);
                if(data.getChargingKWH().compareTo(realtimeData.getChargingKWH()) < 0){
                    hourMap.remove(key);
                    hourMap.put(key, realtimeData);
                }
            }
            else{
                hourMap.put(key, realtimeData);
            }
        }

        List<PlatConnectorRealtimeData> okList = new ArrayList<>();
        for(String key : hourMap.keySet()){
            okList.add(hourMap.get(key));
        }

        Collections.sort(okList, new Comparator<PlatConnectorRealtimeData>() {
            @Override
            public int compare(PlatConnectorRealtimeData o1, PlatConnectorRealtimeData o2) {
                int flag = o1.getCreateTime().compareTo(o2.getCreateTime());
                return flag;
            }
        });

        BigDecimal power = new BigDecimal("0.0000");
        for(PlatConnectorRealtimeData realtimeData : okList){
            ChargeDetailData detailData = new ChargeDetailData();
            int myHour = DateUtils.getHour(realtimeData.getCreateTime());
            if(startHour == myHour){
                detailData.setDetailStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, startTm));
            }
            else{
                String time = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, realtimeData.getCreateTime()) + String.format(" %02d:00:00", myHour);
                detailData.setDetailStartTime(time);
            }

            if(endHour == myHour){
                detailData.setDetailEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endTm));
            }
            else{
                String time = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, realtimeData.getCreateTime()) + String.format(" %02d:59:59", myHour);
                detailData.setDetailEndTime(time);
            }

            BigDecimal usePower = realtimeData.getChargingKWH().subtract(power);
            detailData.setDetailPower(usePower.setScale(2, RoundingMode.UP));
            detailData.setElecPrice(getPrice(myHour, priceList).getElecPrice().setScale(4, RoundingMode.UP));
            detailData.setSevicePrice(getPrice(myHour, priceList).getSevicePrice().setScale(4, RoundingMode.UP));
            detailData.setDetailElecMoney(detailData.getElecPrice().multiply(detailData.getDetailPower()).setScale(2, RoundingMode.UP));
            detailData.setDetailSeviceMoney(detailData.getSevicePrice().multiply(detailData.getDetailPower()).setScale(2, RoundingMode.UP));
            power = realtimeData.getChargingKWH();
            resultList.add(detailData);
        }
        return resultList;
    }

    @Override
    public NotificationChargeOrderInfoData getCharingTradeInfo(String startChargeSeq) {
        if(TextUtils.isBlank(startChargeSeq)){
            return null;
        }
        //获取订单数据
        SysChargeOrder sysChargeOrder = chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
        if(sysChargeOrder == null){
            return null;
        }
        //获取充电桩推送的订单原始数据
        PlatTradingRecordData platTradingRecordData = platTradingRecordDataService.getByTradeNo(sysChargeOrder.getTradeNo());
        if(platTradingRecordData == null){
            //桩逻辑
            log.info("【平台推送订单】" + JSON.toJSONString(sysChargeOrder));
            NotificationChargeOrderInfoData notificationChargeOrderInfoData = new NotificationChargeOrderInfoData();
            notificationChargeOrderInfoData.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
            notificationChargeOrderInfoData.setConnectorID(sysChargeOrder.getConnectorId());
            notificationChargeOrderInfoData.setStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sysChargeOrder.getStartTime()));
            notificationChargeOrderInfoData.setEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sysChargeOrder.getEndTime() == null?(new Date()):sysChargeOrder.getEndTime()));
            notificationChargeOrderInfoData.setTotalPower(sysChargeOrder.getTotalPower());
            notificationChargeOrderInfoData.setTotalElecMoney(sysChargeOrder.getElecMoney());
            notificationChargeOrderInfoData.setTotalSeviceMoney(sysChargeOrder.getServiceMoney());
            notificationChargeOrderInfoData.setTotalMoney(sysChargeOrder.getTotalMoney());
            notificationChargeOrderInfoData.setStopReason((short) 0);
            notificationChargeOrderInfoData.setVin(sysChargeOrder.getCarVin());
            return notificationChargeOrderInfoData;
        }
        else {

            NotificationChargeOrderInfoData notificationChargeOrderInfoData = new NotificationChargeOrderInfoData();
            notificationChargeOrderInfoData.setStartChargeSeq(startChargeSeq);
            notificationChargeOrderInfoData.setConnectorID(sysChargeOrder.getConnectorId());
            notificationChargeOrderInfoData.setStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, platTradingRecordData.getStartTime()));
            notificationChargeOrderInfoData.setEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, platTradingRecordData.getEndTime()));
            if (!TextUtils.isBlank(sysChargeOrder.getCarVin())) {
                notificationChargeOrderInfoData.setVin(sysChargeOrder.getCarVin());
            }

            //查询充电桩充电时间段明细
            List<ChargeDetailData> detailDataList = selfService.getChargeOrderDetails(startChargeSeq, true);
            notificationChargeOrderInfoData.setSumPeriod((short) detailDataList.size());
            notificationChargeOrderInfoData.setChargeDetails(detailDataList);

            BigDecimal totalPower = new BigDecimal("0.0000");
            BigDecimal totalEMoney = new BigDecimal("0.0000");
            BigDecimal totalSMoney = new BigDecimal("0.0000");
            BigDecimal totalMoney = new BigDecimal("0.0000");

            for (ChargeDetailData data : detailDataList) {
                totalPower = totalPower.add(data.getDetailPower());
                totalEMoney = totalEMoney.add(data.getDetailElecMoney());
                totalSMoney = totalSMoney.add(data.getDetailSeviceMoney());
                //totalMoney = totalMoney.add(data.getDetailSeviceMoney().add(data.getDetailElecMoney()));
            }
            notificationChargeOrderInfoData.setTotalPower(totalPower.setScale(2, RoundingMode.UP));
            notificationChargeOrderInfoData.setTotalElecMoney(totalEMoney.setScale(2, RoundingMode.UP));
            notificationChargeOrderInfoData.setTotalSeviceMoney(totalSMoney.setScale(2, RoundingMode.UP));
            notificationChargeOrderInfoData.setStopReason(platTradingRecordData.getStopType().shortValue());
            totalMoney = notificationChargeOrderInfoData.getTotalElecMoney().add(notificationChargeOrderInfoData.getTotalSeviceMoney());
            notificationChargeOrderInfoData.setTotalMoney(totalMoney.setScale(2, RoundingMode.UP));

            if (sysChargeOrder.getTotalMoney() == null
                    || sysChargeOrder.getTotalMoney().compareTo(notificationChargeOrderInfoData.getTotalMoney()) < 0
                    || sysChargeOrder.getElecMoney().compareTo(BigDecimal.ZERO) <= 0
            ) {
                //需要更新
                SysChargeOrder upChargeOrder = new SysChargeOrder();
                upChargeOrder.setId(sysChargeOrder.getId());
                upChargeOrder.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
                upChargeOrder.setTradeNo(sysChargeOrder.getTradeNo());
                upChargeOrder.setTotalMoney(notificationChargeOrderInfoData.getTotalMoney());
                upChargeOrder.setTotalPower(notificationChargeOrderInfoData.getTotalPower());
                upChargeOrder.setElecMoney(notificationChargeOrderInfoData.getTotalElecMoney());
                upChargeOrder.setServiceMoney(notificationChargeOrderInfoData.getTotalSeviceMoney());
                upChargeOrder.setFailReason(notificationChargeOrderInfoData.getStopReason().intValue());
                chargeOrderService.update(upChargeOrder);
            }

            return notificationChargeOrderInfoData;
        }
    }

    @Override
    public NotificationEquipChargeStatusData getChargingStatus(String startChargeSeq) {
        if(TextUtils.isBlank(startChargeSeq)){
            return null;
        }
        //获取订单数据
        SysChargeOrder sysChargeOrder = chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
        if(sysChargeOrder == null){
            return null;
        }

        NotificationEquipChargeStatusData notificationEquipChargeStatusData = new NotificationEquipChargeStatusData();
        notificationEquipChargeStatusData.setStartChargeSeq(startChargeSeq);
        notificationEquipChargeStatusData.setConnectorID(sysChargeOrder.getConnectorId());
        notificationEquipChargeStatusData.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
        notificationEquipChargeStatusData.setConnectorStatus(sysChargeOrder.getConnectorStatus());
        notificationEquipChargeStatusData.setCurrentA(sysChargeOrder.getCurrentA());
        notificationEquipChargeStatusData.setCurrentB(sysChargeOrder.getCurrentB());
        notificationEquipChargeStatusData.setCurrentC(sysChargeOrder.getCurrentC());
        notificationEquipChargeStatusData.setVoltageA(sysChargeOrder.getVoltageA());
        notificationEquipChargeStatusData.setVoltageB(sysChargeOrder.getVoltageB());
        notificationEquipChargeStatusData.setVoltageC(sysChargeOrder.getVoltageC());
        if(!TextUtils.isBlank(sysChargeOrder.getCarVin())){
            notificationEquipChargeStatusData.setVin(sysChargeOrder.getCarVin());
        }
        if(sysChargeOrder.getSoc() == null){
            notificationEquipChargeStatusData.setSoc(new BigDecimal("0.00"));
        }
        else {
            notificationEquipChargeStatusData.setSoc(sysChargeOrder.getSoc().multiply(new BigDecimal("100.0")));
        }
        if(sysChargeOrder.getStartTime() != null) {
            notificationEquipChargeStatusData.setStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sysChargeOrder.getStartTime()));
        }
        if(sysChargeOrder.getEndTime() != null) {
            notificationEquipChargeStatusData.setEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sysChargeOrder.getEndTime()));
        }
        notificationEquipChargeStatusData.setTotalPower(sysChargeOrder.getTotalPower());
        notificationEquipChargeStatusData.setTotalMoney(sysChargeOrder.getTotalMoney());

        log.info("[MQ_AIO_GUN_REALTIME_DATA]开始生成充电明细");
        List<ChargeDetailData> detailDataList = selfService.getChargeOrderDetails(sysChargeOrder.getStartChargeSeq(), false);
        log.info("[MQ_AIO_GUN_REALTIME_DATA]end of 生成充电明细");
        notificationEquipChargeStatusData.setSumPeriod((short)detailDataList.size());
        notificationEquipChargeStatusData.setChargeDetails(detailDataList);
        BigDecimal sMoney = new BigDecimal("0.00");
        BigDecimal eMoney = new BigDecimal("0.00");
        BigDecimal totalMoney = new BigDecimal("0.00");
        for(ChargeDetailData chargeDetailData : detailDataList){
            sMoney = sMoney.add(chargeDetailData.getDetailSeviceMoney());
            eMoney = eMoney.add(chargeDetailData.getDetailElecMoney());
        }
        if(eMoney.floatValue() < 0.01){
            eMoney = sysChargeOrder.getElecMoney();
        }
        if(sMoney.floatValue() < 0.01){
            sMoney = sysChargeOrder.getServiceMoney();
        }
        totalMoney = eMoney.setScale(2, RoundingMode.UP).add(sMoney.setScale(2, RoundingMode.UP)).setScale(2, RoundingMode.HALF_EVEN);

        notificationEquipChargeStatusData.setElecMoney(eMoney.setScale(2, RoundingMode.UP));
        notificationEquipChargeStatusData.setServiceMoney(sMoney.setScale(2, RoundingMode.UP));
        notificationEquipChargeStatusData.setTotalMoney(totalMoney);
        return notificationEquipChargeStatusData;
    }

    @Override
    public boolean finishTrade(PlatTradingRecordData platTradingRecordData) {
        SysChargeOrder sysChargeOrder = chargeOrderService.getChargeOrderByTradeNo(platTradingRecordData.getTradeNo());
        if(sysChargeOrder == null){
            return false;
        }

        if(sysChargeOrder.getStartTime() != null && platTradingRecordData.getEndTime().getTime() < sysChargeOrder.getStartTime().getTime()){
            //当充电结束时间小于开始时间，那么以当前时间做为结束时间
            sysChargeOrder.setEndTime(new Date());
        }
        else {
            sysChargeOrder.setEndTime(platTradingRecordData.getEndTime());
        }
        sysChargeOrder.setTotalPower(platTradingRecordData.getFinalKwh());

        sysChargeOrder.setStartChargeSeqStat((short) 4);
        if (sysChargeOrder.getConnectorStatus() == 3) sysChargeOrder.setConnectorStatus(2);
        sysChargeOrder.setCurrentA(new BigDecimal("0.00"));
        sysChargeOrder.setCurrentB(new BigDecimal("0.00"));
        sysChargeOrder.setCurrentC(new BigDecimal("0.00"));
        sysChargeOrder.setVoltageA(new BigDecimal("0.00"));
        sysChargeOrder.setVoltageB(new BigDecimal("0.00"));
        sysChargeOrder.setVoltageC(new BigDecimal("0.00"));
        if(platTradingRecordData.getStopType() != null) {
            sysChargeOrder.setFailReason(platTradingRecordData.getStopType());
        }
        if (!TextUtils.isBlank(platTradingRecordData.getVin())) {
            sysChargeOrder.setCarVin(platTradingRecordData.getVin());
        }
        //首先生成交易明细
        List<ChargeDetailData> chargeDetailDataList = selfService.getChargeOrderDetails(sysChargeOrder.getStartChargeSeq(), true);
        List<SysChargeOrderItem> itemList = new ArrayList<>();
        BigDecimal totalKwh = new BigDecimal("0.0000");
        for (ChargeDetailData chargeDetailData : chargeDetailDataList) {
            SysChargeOrderItem sysChargeOrderItem = new SysChargeOrderItem();
            sysChargeOrderItem.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
            sysChargeOrderItem.setStartTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, chargeDetailData.getDetailStartTime()));
            sysChargeOrderItem.setEndTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, chargeDetailData.getDetailEndTime()));
            sysChargeOrderItem.setElecPrice(chargeDetailData.getElecPrice());
            sysChargeOrderItem.setSevicePrice(chargeDetailData.getSevicePrice());
            sysChargeOrderItem.setPower(chargeDetailData.getDetailPower());
            sysChargeOrderItem.setElecMoney(chargeDetailData.getDetailElecMoney());
            sysChargeOrderItem.setServiceMoney(chargeDetailData.getDetailSeviceMoney());
            totalKwh = totalKwh.add(chargeDetailData.getDetailPower());
            itemList.add(sysChargeOrderItem);
        }
        if (itemList.size() > 0) {
            if (platTradingRecordData.getFinalKwh().compareTo(totalKwh) > 0) {
                //总瓦数少了，增加到最后
                SysChargeOrderItem lastItem = itemList.get(itemList.size() - 1);
                BigDecimal more = platTradingRecordData.getFinalKwh().subtract(totalKwh);
                BigDecimal real = lastItem.getPower().add(more).setScale(4, RoundingMode.UP);
                lastItem.setPower(real);
                lastItem.setServiceMoney(real.multiply(lastItem.getSevicePrice()).setScale(4, RoundingMode.UP));
                lastItem.setElecMoney(real.multiply(lastItem.getElecPrice()).setScale(4, RoundingMode.UP));
            }
            if (!chargeOrderItemService.saveBatch(sysChargeOrder.getStartChargeSeq(), itemList)) {
                log.error("【充电结算】数据库保存失败");
                return false;
            }
        }
        BigDecimal totalSerPrice = new BigDecimal("0.00");  //总服务费
        BigDecimal totalElePrice = new BigDecimal("0.00");  //总电费
        for(SysChargeOrderItem item : itemList){
            totalElePrice = totalElePrice.add(item.getElecMoney());
            totalSerPrice = totalSerPrice.add(item.getServiceMoney());
        }
        sysChargeOrder.setSumPeriod((short) itemList.size());
        sysChargeOrder.setServiceMoney(totalSerPrice.setScale(4, RoundingMode.UP));
        sysChargeOrder.setElecMoney(totalElePrice.setScale(4, RoundingMode.UP));
        sysChargeOrder.setTotalMoney(sysChargeOrder.getServiceMoney().add(sysChargeOrder.getElecMoney()).setScale(4, RoundingMode.UP));
        chargeOrderService.update(sysChargeOrder);

        return true;
    }

    private PolicyInfoData getPrice(int hour, List<PolicyInfoData> policyInfoDataList){
        for(PolicyInfoData policyInfoData : policyInfoDataList){
            if(TextUtils.isBlank(policyInfoData.getStartTime()) || policyInfoData.getStartTime().length() < 2){
                continue;
            }
            if(hour >= Long.valueOf(policyInfoData.getStartTime().substring(0,2))){
                return policyInfoData;
            }
        }
        return policyInfoDataList.get(0);
    }

}
