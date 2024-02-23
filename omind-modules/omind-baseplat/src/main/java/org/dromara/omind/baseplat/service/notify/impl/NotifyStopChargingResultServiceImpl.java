package org.dromara.omind.baseplat.service.notify.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationStopChargeResultData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.UserPlatApiService;
import org.dromara.omind.baseplat.service.notify.NotifyStopChargingResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Log4j2
@Service
public class NotifyStopChargingResultServiceImpl implements NotifyStopChargingResultService {

    @Resource
    UPlatNotificationV1Client notificationV1Client;

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    RemoteSeqService remoteSeqService;

    @Autowired
    UserPlatApiService userPlatApiService;

    @Override
    public void send(SysChargeOrder sysChargeOrder, boolean isSuccess, int reason) {

        if(sysChargeOrder == null || TextUtils.isBlank(sysChargeOrder.getConnectorId())){
            return;
        }
        SysOperator sysOperator = operatorService.getOperatorById(sysChargeOrder.getOperatorId());
        if(sysOperator == null || TextUtils.isBlank(sysOperator.getOperatorId())){
            return;
        }

        NotificationStopChargeResultData notificationStopChargeResultData = new NotificationStopChargeResultData();
        notificationStopChargeResultData.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
        notificationStopChargeResultData.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
        notificationStopChargeResultData.setConnectorID(sysChargeOrder.getConnectorId());
        notificationStopChargeResultData.setSuccStat((short)(isSuccess?0:1));
        notificationStopChargeResultData.setFailReason((short)reason);

        HlhtDto hlhtDto = new HlhtDto();
        hlhtDto.setDataObj(sysOperator, notificationStopChargeResultData, remoteSeqService.getHlhtRequestSeq());
        //发送通知
        String token = userPlatApiService.getTokenAuto(sysOperator);
        notificationV1Client.notificationStopChargeResult(sysOperator.getHost(), sysOperator.getOperatorId(),token, hlhtDto);
        log.info("【推送设备远程停机回复】"+ JSON.toJSONString(notificationStopChargeResultData));

    }

}
