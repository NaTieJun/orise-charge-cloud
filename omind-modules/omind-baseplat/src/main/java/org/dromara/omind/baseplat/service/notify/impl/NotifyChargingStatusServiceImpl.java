package org.dromara.omind.baseplat.service.notify.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationEquipChargeStatusData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.service.*;
import org.dromara.omind.baseplat.service.notify.NotifyChargingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Log4j2
@Service
public class NotifyChargingStatusServiceImpl implements NotifyChargingStatusService {
    @Resource
    UPlatNotificationV1Client notificationV1Client;

    @Autowired
    @Lazy
    NotifyChargingStatusService selfService;

    @Autowired
    SysConnectorService connectorService;

    @Autowired
    SysEquipmentService equipmentService;

    @Autowired
    SysStationService stationService;

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    RemoteSeqService remoteSeqService;

    @Autowired
    TradeService tradeService;

    @Autowired
    UserPlatApiService userPlatApiService;

    @Autowired
    StationOperatorLinkService stationOperatorLinkService;

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Override
    public void remoteStopCharging(SysChargeOrder sysChargeOrder) {

        selfService.sendBySysChargeOrder(sysChargeOrder);

    }

    @Override
    public void realtimeData(SysChargeOrder sysChargeOrder) {
        selfService.sendBySysChargeOrder(sysChargeOrder);
    }

    @Override
    public void bmsStop(SysChargeOrder sysChargeOrder) {
        selfService.sendBySysChargeOrder(sysChargeOrder);
    }

    @Override
    public void chargerStop(SysChargeOrder sysChargeOrder) {
        selfService.sendBySysChargeOrder(sysChargeOrder);
    }

    @Override
    public void chargingFinish(SysChargeOrder sysChargeOrder) {
        selfService.sendBySysChargeOrder(sysChargeOrder);
    }

    @Override
    public void remoteConfirmCharging(SysChargeOrder sysChargeOrder) {

        selfService.sendBySysChargeOrder(sysChargeOrder);

    }

    @Override
    public void disconnect(String startChargeSeq) {
        //不支持，因为网络断开应该对充电通知没有影响
    }

    //统一通过订单信息加工推送数据
    @Override
    public void sendBySysChargeOrder(SysChargeOrder sysChargeOrder) {
        if(sysChargeOrder == null || TextUtils.isBlank(sysChargeOrder.getConnectorId())){
            return;
        }

        //取一下最新的订单数据,防止结束后推送
        SysChargeOrder newestInfo = chargeOrderService.getChargeOrderByStartChargeSeq(sysChargeOrder.getStartChargeSeq());
        if(newestInfo.getStartChargeSeqStat() >= 3){
            return;
        }

        SysOperator sysOperator = operatorService.getOperatorById(sysChargeOrder.getOperatorId());
        if(sysOperator == null || TextUtils.isBlank(sysOperator.getOperatorId())){
            return;
        }
        NotificationEquipChargeStatusData notifyData = tradeService.getChargingStatus(sysChargeOrder.getStartChargeSeq());
        if(notifyData == null){
            return;
        }
        HlhtDto hlhtDto = new HlhtDto();
        log.info("【推送充电状态数据】" + JSON.toJSONString(notifyData));
        hlhtDto.setDataObj(sysOperator, notifyData, remoteSeqService.getHlhtRequestSeq());
        String token = userPlatApiService.getTokenAuto(sysOperator);
        notificationV1Client.notificationEquipChargeStatus(sysOperator.getHost(), sysOperator.getOperatorId(), token, hlhtDto, (result, request, response) -> {
            // 处理响应结果
            if(result.getRet() == 4002){
                userPlatApiService.refreshToken(sysOperator);
            }
        });
    }
}
