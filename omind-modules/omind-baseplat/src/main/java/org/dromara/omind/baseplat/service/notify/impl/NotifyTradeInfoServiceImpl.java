package org.dromara.omind.baseplat.service.notify.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.baseplat.api.domain.response.NotificationChargeOrderInfoResponseData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.service.*;
import org.dromara.omind.baseplat.service.notify.NotifyTradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Log4j2
@Service
public class NotifyTradeInfoServiceImpl implements NotifyTradeInfoService {

    @Autowired
    TradeService tradeService;

    @Resource
    UPlatNotificationV1Client notificationV1Client;

    @Autowired
    SysConnectorService connectorService;

    @Autowired
    SysEquipmentService equipmentService;

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    RemoteSeqService remoteSeqService;

    @Autowired
    SysChargeOrderService chargeOrderService;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;


    @Autowired
    UserPlatApiService userPlatApiService;

    @Override
    public void send(SysChargeOrder chargeOrder) {
        if(chargeOrder == null){
            return;
        }
        NotificationChargeOrderInfoData notificationChargeOrderInfoData = tradeService.getCharingTradeInfo(chargeOrder.getStartChargeSeq());
        if(notificationChargeOrderInfoData == null){
            log.error("未找到订单信息" + chargeOrder.getStartChargeSeq());
            return;
        }

        SysOperator sysOperator = operatorService.getOperatorById(chargeOrder.getOperatorId());

        HlhtDto hlhtDto = new HlhtDto();
        hlhtDto.setDataObj(sysOperator, notificationChargeOrderInfoData, remoteSeqService.getHlhtRequestSeq());
        //发送通知
        String token = userPlatApiService.getTokenAuto(sysOperator);
        HlhtResult hlhtResult = notificationV1Client.notificationChargeOrderInfo(sysOperator.getHost(), sysOperator.getOperatorId(), token, hlhtDto);
        NotificationChargeOrderInfoResponseData responseData = hlhtResult.getDataObj(sysOperator, NotificationChargeOrderInfoResponseData.class);

        if(responseData != null){
            if(responseData.getConfirmResult() == 0
                    && responseData.getStartChargeSeq().equals(chargeOrder.getStartChargeSeq()))
            {
                chargeOrder.setSyncFlag((short)100);
                SysChargeOrder updateOrder = new SysChargeOrder();
                updateOrder.setStartChargeSeq(chargeOrder.getStartChargeSeq());
                updateOrder.setTradeNo(chargeOrder.getTradeNo());
                updateOrder.setId(chargeOrder.getId());
                updateOrder.setSyncFlag((short)100);
                chargeOrderService.update(updateOrder);
                log.info("【推送订单数据】"+ JSON.toJSONString(notificationChargeOrderInfoData));
                return;
            }
        }

        if(chargeOrder.getSyncFlag() < 90){
            SysChargeOrder updateOrder = new SysChargeOrder();
            updateOrder.setId(chargeOrder.getId());
            updateOrder.setStartChargeSeq(chargeOrder.getStartChargeSeq());
            updateOrder.setTradeNo(chargeOrder.getTradeNo());
            if(chargeOrder.getSyncFlag() > 49) {
                //标记为同步失败，不再重试
                updateOrder.setSyncFlag((short) ((short)99));
            }
            else{
                updateOrder.setSyncFlag((short) (chargeOrder.getSyncFlag() + 1));
            }
            chargeOrderService.update(updateOrder);
        }
        log.info("【推送订单数据失败】次数"+ chargeOrder.getSyncFlag()
                + " 数据："+ JSON.toJSONString(notificationChargeOrderInfoData));
    }

}
