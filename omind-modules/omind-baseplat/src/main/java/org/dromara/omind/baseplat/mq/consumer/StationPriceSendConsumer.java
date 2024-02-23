package org.dromara.omind.baseplat.mq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.StationFeeDetailData;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStationOperatorLink;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationStationFeeBodyData;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationStationFeeData;
import org.dromara.omind.baseplat.api.domain.response.NotificationStationFeeResponseData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.service.StationOperatorLinkService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.dromara.omind.baseplat.service.UserPlatApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Log4j2
@Component
public class StationPriceSendConsumer {

    @Autowired
    @Lazy
    StationPriceSendConsumer selfService;

    @Autowired
    @Lazy
    StationOperatorLinkService linkService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @Resource
    UPlatNotificationV1Client uPlatNotificationV1Client;

    @Autowired
    @Lazy
    SysPriceService priceService;

    @Autowired
    @Lazy
    RemoteSeqService seqService;

    @Autowired
    @Lazy
    UserPlatApiService userPlatApiService;

    @Bean
    Consumer<String> stationPriceSender()
    {
        log.info("priceUpdate2StationSender-初始化订阅");
        return stationId -> {
            log.info("priceUpdate2StationSender 消息接收成功=>" + stationId);
            selfService.dealWithData(stationId);
        };
    }

    void dealWithData(String stationId)
    {
        List<PolicyInfoData> priceList = priceService.getHlhtPrice4Station(stationId);
        List<SysStationOperatorLink> operatorList = linkService.getList4Station(stationId);
        for(SysStationOperatorLink link : operatorList){
            SysOperator sysOperator = operatorService.getOperatorById(link.getOperatorId());

            NotificationStationFeeData stationFeeData = new NotificationStationFeeData();
            stationFeeData.setOperatorID(sysOperator.getMyOperatorId());
            stationFeeData.setStationID(stationId);
            List<StationFeeDetailData> stationFeeDetailDataList = new ArrayList<>();
            int size = priceList.size();
            for(int i = 0;i<size;i++){
                PolicyInfoData policyInfoData = priceList.get(i);
                if(policyInfoData.getStartTime().length() != 6){
                    continue;
                }
                StationFeeDetailData stationFeeDetailData = new StationFeeDetailData();
                int startHour = Integer.valueOf(policyInfoData.getStartTime().substring(0,2));
                int startMin = Integer.valueOf(policyInfoData.getStartTime().substring(2,4));
                stationFeeDetailData.setStartTime(String.format("%02d:%02d", startHour, startMin));
                if(i == size - 1){
                    stationFeeDetailData.setEndTime("23:59");
                }
                else{
                    PolicyInfoData endData = priceList.get(i + 1);
                    int endHour = Integer.valueOf(endData.getStartTime().substring(0,2));
                    int endMin = Integer.valueOf(endData.getStartTime().substring(2,4));
                    if(endMin <= 0){
                        endHour = endHour - 1;
                        endMin = 59;
                    }
                    else{
                        endMin = endMin - 1;
                    }
                    stationFeeDetailData.setEndTime(String.format("%02d:%02d", endHour, endMin));
                }
                stationFeeDetailData.setElectricityFee(policyInfoData.getElecPrice().setScale(2, RoundingMode.HALF_EVEN));
                stationFeeDetailData.setServiceFee(policyInfoData.getSevicePrice().setScale(2, RoundingMode.HALF_EVEN));

                stationFeeDetailData.setEquipmentType((short)1);
                stationFeeDetailDataList.add(stationFeeDetailData);
            }

            stationFeeData.setChargeFeeDetail(stationFeeDetailDataList);
            NotificationStationFeeBodyData bodyData = new NotificationStationFeeBodyData();
            bodyData.setStationFeeData(stationFeeData);
            String token = userPlatApiService.getTokenAuto(sysOperator);
            HlhtDto dto = new HlhtDto();
            dto.setDataObj(sysOperator, bodyData, seqService.getHlhtRequestSeq());
            uPlatNotificationV1Client.notificationStationFee(sysOperator.getHost(), sysOperator.getOperatorId(), token, dto, (result, request, response) -> {
                // 处理响应结果
                // System.out.println(result);
                if(result.getRet() == 4002){
                    userPlatApiService.refreshToken(sysOperator);
                }
                NotificationStationFeeResponseData resultData = result.getDataObj(sysOperator, NotificationStationFeeResponseData.class);
                log.info("【推送价格更新至】" + JSON.toJSONString(stationFeeData) + "// RESPONSE:" + JSON.toJSONString(resultData));
            });

        }
    }

}
