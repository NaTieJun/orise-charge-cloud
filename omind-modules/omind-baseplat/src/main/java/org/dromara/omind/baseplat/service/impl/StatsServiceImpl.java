package org.dromara.omind.baseplat.service.impl;

import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.domain.request.StationStatsFullRequest;
import org.dromara.omind.baseplat.domain.response.StationStatsFullResponse;
import org.dromara.omind.baseplat.domain.vo.StatsUnit;
import org.dromara.omind.baseplat.service.StatsService;
import org.dromara.omind.baseplat.service.SysChargeOrderService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Autowired
    @Lazy
    SysStationService stationService;

    @Override
    public StationStatsFullResponse stationStats(StationStatsFullRequest request) {

        //时间跨度不能超过400天
        if(!TextUtils.isBlank(request.getStartTm()) && !TextUtils.isBlank(request.getEndTm())){
            long start = DateUtils.dateTime("yyyy-MM-dd", request.getStartTm()).getTime();
            long end = DateUtils.dateTime("yyyy-MM-dd", request.getEndTm()).getTime();
            if((end - start) > (370L * 24L * 3600L * 1000L)){
                throw new BaseException("查询时间区间最大为366天");
            }
        }

        StationStatsFullResponse stationStatsFullResponse = new StationStatsFullResponse();

        Date startTm = DateUtils.dateTime(DateUtils.YYYY_MM_DD,request.getStartTm());
        Date endTm = new Date();
        //统计方式 0日 1月 2年 3自定义
        if(request.getType() == 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTm);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
            endTm = calendar.getTime();
        }
        else{
            endTm = DateUtils.dateTime(DateUtils.YYYY_MM_DD,request.getEndTm());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTm);
            if(calendar.get(Calendar.HOUR_OF_DAY) == 0){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.SECOND, -1);
                endTm = calendar.getTime();
            }
        }

        QuerySysChargeOrderDto querySysChargeOrderDto = new QuerySysChargeOrderDto();
        querySysChargeOrderDto.setStationId(request.getStationId());
        querySysChargeOrderDto.setStartTm(startTm);
        querySysChargeOrderDto.setEndTm(endTm);
        querySysChargeOrderDto.setOperatorId(request.getOperatorId());
        List<SysChargeOrder> sysChargeOrderList = chargeOrderService.getChargeOrderList(querySysChargeOrderDto);
        if(sysChargeOrderList == null){
            sysChargeOrderList = new ArrayList<>();
        }

        stationStatsFullResponse.setOrderCount(sysChargeOrderList.size());
        Map<String, StatsUnit> moneyMap = new HashMap<>();
        Map<String, StatsUnit> countMap = new HashMap<>();
        Map<String, StatsUnit> powerMap = new HashMap<>();
        Map<String, StatsUnit> moneyPieMap = new HashMap<>();
        Map<String, StatsUnit> serMoneyMap = new HashMap<>();
        Map<String, StatsUnit> eleMoneyMap = new HashMap<>();
        Map<String, StatsUnit> durantMap = new HashMap<>();

        for(int i = 0 ; i<60;i=i+10){
            StatsUnit moneyPieM =  new StatsUnit((long)i, new BigDecimal("0.00"), i + "-" + (i+10) + "元");
            if(i >= 50){
                moneyPieM.setUnit("50元以上");
            }
            moneyPieMap.put(i+"",moneyPieM);
            stationStatsFullResponse.getChargeMoneyPieList().add(moneyPieM);
        }

        if(request.getType() == 0){
            //小时分割

            //构建24小时数据
            for(int i = 0 ;i < 24;i++){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTm);
                calendar.add(Calendar.HOUR_OF_DAY, i);
                Date dt = calendar.getTime();
                String dtStr = getTmKey(dt, 1);

                StatsUnit moneyM = new StatsUnit();
                moneyM.setTm(dt.getTime());
                moneyM.setUnit("元");
                moneyM.setValue(new BigDecimal("0.0000"));
                moneyMap.put(dtStr, moneyM);

                StatsUnit serMoneyM = new StatsUnit();
                serMoneyM.setTm(dt.getTime());
                serMoneyM.setUnit("元");
                serMoneyM.setValue(new BigDecimal("0.0000"));
                serMoneyMap.put(dtStr, serMoneyM);

                StatsUnit eleMoneyM = new StatsUnit();
                eleMoneyM.setTm(dt.getTime());
                eleMoneyM.setUnit("元");
                eleMoneyM.setValue(new BigDecimal("0.0000"));
                eleMoneyMap.put(dtStr, eleMoneyM);

                StatsUnit countM = new StatsUnit();
                countM.setTm(dt.getTime());
                countM.setUnit("次");
                countM.setValue(new BigDecimal("0.0000"));
                countMap.put(dtStr, countM);

                StatsUnit powerM = new StatsUnit();
                powerM.setTm(dt.getTime());
                powerM.setUnit("KW·h");
                powerM.setValue(new BigDecimal("0.0000"));
                powerMap.put(dtStr, powerM);

                StatsUnit durantM = new StatsUnit();
                durantM.setTm(dt.getTime());
                durantM.setUnit("分钟");
                durantM.setValue(new BigDecimal("0"));
                durantMap.put(dtStr, durantM);

                stationStatsFullResponse.getChargeMoneyList().add(moneyM);
                stationStatsFullResponse.getChargePowerList().add(powerM);
                stationStatsFullResponse.getChargeCountList().add(countM);
                stationStatsFullResponse.getSerMoneyList().add(serMoneyM);
                stationStatsFullResponse.getEleMoneyList().add(eleMoneyM);
                stationStatsFullResponse.getChargeDurantList().add(durantM);
            }
        }
        else if(request.getType() == 2){
            //月分割
            for(int i = 0 ;i < 12;i++){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTm);
                calendar.add(Calendar.MONTH, i);
                Date dt = calendar.getTime();
                String dtStr = getTmKey(dt, 2);

                StatsUnit moneyM = new StatsUnit();
                moneyM.setTm(dt.getTime());
                moneyM.setUnit("元");
                moneyM.setValue(new BigDecimal("0.0000"));
                moneyMap.put(dtStr, moneyM);

                StatsUnit serMoneyM = new StatsUnit();
                serMoneyM.setTm(dt.getTime());
                serMoneyM.setUnit("元");
                serMoneyM.setValue(new BigDecimal("0.0000"));
                serMoneyMap.put(dtStr, serMoneyM);

                StatsUnit eleMoneyM = new StatsUnit();
                eleMoneyM.setTm(dt.getTime());
                eleMoneyM.setUnit("元");
                eleMoneyM.setValue(new BigDecimal("0.0000"));
                eleMoneyMap.put(dtStr, eleMoneyM);


                StatsUnit countM = new StatsUnit();
                countM.setTm(dt.getTime());
                countM.setUnit("次");
                countM.setValue(new BigDecimal("0"));
                countMap.put(dtStr, countM);

                StatsUnit powerM = new StatsUnit();
                powerM.setTm(dt.getTime());
                powerM.setUnit("KW·h");
                powerM.setValue(new BigDecimal("0.0000"));
                powerMap.put(dtStr, powerM);

                StatsUnit durantM = new StatsUnit();
                durantM.setTm(dt.getTime());
                durantM.setUnit("分钟");
                durantM.setValue(new BigDecimal("0"));
                durantMap.put(dtStr, durantM);

                stationStatsFullResponse.getChargeMoneyList().add(moneyM);
                stationStatsFullResponse.getChargePowerList().add(powerM);
                stationStatsFullResponse.getChargeCountList().add(countM);
                stationStatsFullResponse.getSerMoneyList().add(serMoneyM);
                stationStatsFullResponse.getEleMoneyList().add(eleMoneyM);
                stationStatsFullResponse.getChargeDurantList().add(durantM);
            }
        }
        else{
            //日分割
            for(int i = 0 ;;i++){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTm);
                calendar.add(Calendar.DAY_OF_YEAR, i);
                Date dt = calendar.getTime();

                if(dt.getTime() > endTm.getTime()){
                    break;
                }

                String dtStr = getTmKey(dt, 0);

                StatsUnit moneyM = new StatsUnit();
                moneyM.setTm(dt.getTime());
                moneyM.setUnit("元");
                moneyM.setValue(new BigDecimal("0.0000"));
                moneyMap.put(dtStr, moneyM);

                StatsUnit serMoneyM = new StatsUnit();
                serMoneyM.setTm(dt.getTime());
                serMoneyM.setUnit("元");
                serMoneyM.setValue(new BigDecimal("0.0000"));
                serMoneyMap.put(dtStr, serMoneyM);

                StatsUnit eleMoneyM = new StatsUnit();
                eleMoneyM.setTm(dt.getTime());
                eleMoneyM.setUnit("元");
                eleMoneyM.setValue(new BigDecimal("0.0000"));
                eleMoneyMap.put(dtStr, eleMoneyM);


                StatsUnit countM = new StatsUnit();
                countM.setTm(dt.getTime());
                countM.setUnit("次");
                countM.setValue(new BigDecimal("0"));
                countMap.put(dtStr, countM);

                StatsUnit powerM = new StatsUnit();
                powerM.setTm(dt.getTime());
                powerM.setUnit("KW·h");
                powerM.setValue(new BigDecimal("0.0000"));
                powerMap.put(dtStr, powerM);

                StatsUnit durantM = new StatsUnit();
                durantM.setTm(dt.getTime());
                durantM.setUnit("分钟");
                durantM.setValue(new BigDecimal("0"));
                durantMap.put(dtStr, durantM);

                stationStatsFullResponse.getChargeMoneyList().add(moneyM);
                stationStatsFullResponse.getChargePowerList().add(powerM);
                stationStatsFullResponse.getChargeCountList().add(countM);
                stationStatsFullResponse.getSerMoneyList().add(serMoneyM);
                stationStatsFullResponse.getEleMoneyList().add(eleMoneyM);
                stationStatsFullResponse.getChargeDurantList().add(durantM);
            }
        }

        long totalDurant = 0;
        Set<String> userKey = new HashSet<>();

        for(SysChargeOrder sysChargeOrder : sysChargeOrderList){
            stationStatsFullResponse.setTotalMoney(stationStatsFullResponse.getTotalMoney().add(sysChargeOrder.getTotalMoney()));
            stationStatsFullResponse.setTotalPower(stationStatsFullResponse.getTotalPower().add(sysChargeOrder.getTotalPower()));
            stationStatsFullResponse.setTotalSMoney(stationStatsFullResponse.getTotalSMoney().add(sysChargeOrder.getServiceMoney()));
            stationStatsFullResponse.setTotalEMoney(stationStatsFullResponse.getTotalEMoney().add(sysChargeOrder.getElecMoney()));

            int type = 1;
            if(request.getType() == 1 || request.getType() == 3){
                //日
                type = 0;
            }
            else if(request.getType() == 2){
                type = 2;
            }
            String key = getTmKey(sysChargeOrder.getEndTime(), type);
            StatsUnit moneyM = moneyMap.get(key);
            if(moneyM != null){
                StatsUnit countM = countMap.get(key);
                StatsUnit powerM = powerMap.get(key);
                StatsUnit serMoneyM = serMoneyMap.get(key);
                StatsUnit eleMoneyM = eleMoneyMap.get(key);
                StatsUnit durantM = durantMap.get(key);

                int moneyKey = sysChargeOrder.getTotalMoney().intValue() / 10;
                if(moneyKey < 0){
                    moneyKey = 0;
                }
                else if(moneyKey > 5){
                    moneyKey = 5;
                }
                moneyKey = moneyKey * 10;
                StatsUnit moneyPieM = moneyPieMap.get(moneyKey + "");

                if(moneyM != null) {
                    BigDecimal money = moneyM.getValue();
                    money = money.add(sysChargeOrder.getTotalMoney()).setScale(4, RoundingMode.HALF_EVEN);
                    moneyM.setValue(money);
                }

                if(serMoneyM != null) {
                    BigDecimal serMoney = serMoneyM.getValue();
                    serMoney = serMoney.add(sysChargeOrder.getServiceMoney()).setScale(4, RoundingMode.HALF_EVEN);
                    serMoneyM.setValue(serMoney);
                }

                if(eleMoneyM != null) {
                    BigDecimal eleMoney = eleMoneyM.getValue();
                    eleMoney = eleMoney.add(sysChargeOrder.getElecMoney()).setScale(4, RoundingMode.HALF_EVEN);
                    eleMoneyM.setValue(eleMoney);
                }

                if(powerM != null) {
                    BigDecimal power = powerM.getValue();
                    power = power.add(sysChargeOrder.getTotalPower()).setScale(4, RoundingMode.HALF_EVEN);
                    powerM.setValue(power);
                }

                if(durantM != null) {
                    Long durant = durantM.getValue().longValue();
                    if (sysChargeOrder.getStartTime() != null
                            && sysChargeOrder.getEndTime() != null
                            && sysChargeOrder.getEndTime().getTime() > sysChargeOrder.getStartTime().getTime()) {
                        long theDur = ((sysChargeOrder.getEndTime().getTime() - sysChargeOrder.getStartTime().getTime()) / 1000 / 60);
                        durant =  durant + theDur;
                        totalDurant = totalDurant + theDur;
                    }
                    durantM.setValue(new BigDecimal(durant));
                }

                if(moneyPieM != null) {
                    int moneyPie = moneyPieM.getValue().intValueExact() + 1;
                    moneyPieM.setValue(new BigDecimal(moneyPie));
                }

                if(countM != null){
                    int count = countM.getValue().intValueExact() + 1;
                    countM.setValue(new BigDecimal(count));
                }

                String userOnlyKey = sysChargeOrder.getCarVin() + "_" + sysChargeOrder.getPhoneNum() + "_" + sysChargeOrder.getPlateNum();
                if(!userKey.contains(userOnlyKey)){
                    userKey.add(userOnlyKey);
                }
            }
        }
        stationStatsFullResponse.setTotalUserCount(userKey.size());
        stationStatsFullResponse.setTotalDurant(new BigDecimal(totalDurant));
        if(userKey.size() > 0) {
            stationStatsFullResponse.setAvo(stationStatsFullResponse.getTotalMoney().divide(new BigDecimal(userKey.size()), RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN));
        }
        userKey.clear();

        return stationStatsFullResponse;

    }

    public String getTmKey(Date date, int type) {
        //type  0 日 1 小时 2月
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(type == 1){
            //小时
            return DateUtils.parseDateToStr("yyyy-MM-dd HH", date);
        }
        else if(type == 2){
            //月
            return DateUtils.parseDateToStr("yyyy-MM", date);
        }
        else{
            //都是日
            return DateUtils.parseDateToStr("yyyy-MM-dd", date);
        }
    }
}
