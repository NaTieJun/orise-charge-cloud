package org.dromara.omind.baseplat.service.notify.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.omind.baseplat.api.domain.ConnectorInfoData;
import org.dromara.omind.baseplat.api.domain.ConnectorStatusInfoData;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStationOperatorLink;
import org.dromara.omind.baseplat.api.domain.notifications.NotificationStationStatusData;
import org.dromara.omind.baseplat.api.domain.response.NotificationStationStatusResponseData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.service.*;
import org.dromara.omind.baseplat.service.notify.NotifyConnectorStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@Service
public class NotifyConnectorStatusServiceImpl implements NotifyConnectorStatusService {

    @Autowired
    SysConnectorService connectorService;

    @Autowired
    SysEquipmentService equipmentService;

    @Autowired
    SysOperatorService operatorService;

    @Resource
    UPlatNotificationV1Client notificationV1Client;

    @Autowired
    RemoteSeqService remoteSeqService;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;

    @Autowired
    @Lazy
    NotifyConnectorStatusService selfService;

    @Autowired
    UserPlatApiService userPlatApiService;


    @Override
    public void heartConnect(SysConnector sysConnector) {
        if(sysConnector == null || TextUtils.isBlank(sysConnector.getConnectorId())){
            return;
        }

        ConnectorStatusInfoData infoData = new ConnectorStatusInfoData();
        infoData.setConnectorID(sysConnector.getConnectorId());
        infoData.setStatus(sysConnector.getStatus().intValue());
        infoData.setLockStatus(sysConnector.getLockStatus());
        infoData.setParkStatus(sysConnector.getParkStatus());

        log.info("[推送枪状态改变]heartConnect=>" + JSON.toJSONString(infoData));
        //发送通知
        selfService.sendStatus(infoData, sysConnector);
    }

    @Override
    public void heartDisConnect(SysConnector sysConnector) {
        if(sysConnector == null || TextUtils.isBlank(sysConnector.getConnectorId())){
            return;
        }
        ConnectorStatusInfoData infoData = new ConnectorStatusInfoData();
        infoData.setConnectorID(sysConnector.getConnectorId());
        infoData.setStatus(sysConnector.getStatus().intValue());
        infoData.setLockStatus(sysConnector.getLockStatus());
        infoData.setParkStatus(sysConnector.getParkStatus());

        log.info("[推送枪状态改变]heartDisConnect=>" + JSON.toJSONString(infoData));
        selfService.sendStatus(infoData, sysConnector);
    }

    @Override
    public void socketClose(SysEquipment sysEquipment) {
        List<ConnectorInfoData> connectorList = connectorService.getAllByEquipmentId(sysEquipment.getEquipmentId());
        if(connectorList == null || connectorList.size() <= 0){
            return;
        }

        for(ConnectorInfoData connectorInfoData : connectorList) {
            ConnectorStatusInfoData infoData = new ConnectorStatusInfoData();
            SysConnector sysConnector = connectorService.getConnectorById(connectorInfoData.getConnectorID());
            if(sysConnector == null){
                continue;
            }
            infoData.setConnectorID(sysConnector.getConnectorId());
            infoData.setStatus(sysConnector.getStatus().intValue());
            infoData.setLockStatus(sysConnector.getLockStatus());
            infoData.setParkStatus(sysConnector.getParkStatus());

            selfService.sendStatus(infoData, sysConnector);
        }
    }

    @Override
    public void realtimeData(String connectorId) {
        if(TextUtils.isBlank(connectorId)){
            return;
        }
        SysConnector sysConnector = connectorService.getConnectorById(connectorId);
        if(sysConnector == null || TextUtils.isBlank(sysConnector.getConnectorId())){
            return;
        }
        ConnectorStatusInfoData infoData = new ConnectorStatusInfoData();
        infoData.setConnectorID(sysConnector.getConnectorId());
        infoData.setStatus(sysConnector.getStatus().intValue());
        infoData.setLockStatus(sysConnector.getLockStatus());
        infoData.setParkStatus(sysConnector.getParkStatus());

        log.info("[推送枪状态改变]realtimeData=>" + JSON.toJSONString(infoData));
        selfService.sendStatus(infoData, sysConnector);
    }

    @Override
    public void dailyCheck(String connectorId) {
        if(TextUtils.isBlank(connectorId)){
            return;
        }
        SysConnector sysConnector = connectorService.getConnectorById(connectorId);
        if(sysConnector == null || TextUtils.isBlank(sysConnector.getConnectorId())){
            return;
        }
        ConnectorStatusInfoData infoData = new ConnectorStatusInfoData();
        infoData.setConnectorID(sysConnector.getConnectorId());
        infoData.setStatus(sysConnector.getStatus().intValue());
        infoData.setLockStatus(sysConnector.getLockStatus());
        infoData.setParkStatus(sysConnector.getParkStatus());

        log.info("[daily check]=>" + JSON.toJSONString(infoData));
        selfService.sendStatus(infoData, sysConnector);
    }

    @Override
    public void sendStatus(ConnectorStatusInfoData infoData, SysConnector sysConnector){
        if(infoData == null || sysConnector == null || TextUtils.isBlank(sysConnector.getConnectorId())){
            return;
        }

        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
        if(sysEquipment == null || TextUtils.isBlank(sysEquipment.getStationId())){
            return;
        }
        List<SysStationOperatorLink> linkList = stationOperatorLinkService.getList4Station(sysEquipment.getStationId());
        if(linkList == null || linkList.size() <= 0){
            SysOperator sysOperator = connectorService.getDefaultOperatorByConnectorId(sysConnector.getConnectorId());
            if(sysConnector == null){
                return;
            }
            log.info("默认运营商");
            selfService.sendStatus(infoData, sysOperator, 0);
        }
        else{
            log.info("运营商队列");
            for(SysStationOperatorLink link : linkList){
                if(link.getIsEnable() == 1) {
                    SysOperator sysOperator = operatorService.getOperatorById(link.getOperatorId());
                    if (sysConnector != null) {
                        selfService.sendStatus(infoData, sysOperator, 0);
                    }
                }
            }
        }
    }

    @Override
    public void sendStatus(ConnectorStatusInfoData infoData, SysOperator sysOperator, int retryTime) {
        if(retryTime >= 1 || infoData == null || sysOperator == null || TextUtils.isBlank(sysOperator.getOperatorId()) || TextUtils.isBlank(sysOperator.getHost())){
            return;
        }
        NotificationStationStatusData statusData = new NotificationStationStatusData();
        statusData.setConnectorStatusInfo(infoData);
        HlhtDto hlhtDto = new HlhtDto();
        hlhtDto.setDataObj(sysOperator, statusData, remoteSeqService.getHlhtRequestSeq());

        try {
            if(TextUtils.isBlank(sysOperator.getHost()) || TextUtils.isBlank(sysOperator.getOperatorId())){
                return;
            }
            String token = userPlatApiService.getTokenAuto(sysOperator);
            notificationV1Client.notificationStationStatus(sysOperator.getHost(), sysOperator.getOperatorId(),token, hlhtDto, (result, request, response) -> {
                // 处理响应结果
                // System.out.println(result);
                if(result.getRet() == 4002){
                    userPlatApiService.refreshToken(sysOperator);
                }
                if (result == null || result.getRet() != 0) {
                    log.error("【推送充电枪状态失败】Host：" + sysOperator.getHost() + "次数" + retryTime +" 数据：" + JSON.toJSONString(hlhtDto) + sysOperator.getOperatorName());
                    return;
                }
                NotificationStationStatusResponseData responseData = result.getDataObj(sysOperator, NotificationStationStatusResponseData.class);
                log.info("【推送充电枪状态成功】Host：" + sysOperator.getHost() + " 数据：" + JSON.toJSONString(responseData));
                if (responseData == null || responseData.getStatus() == null) {
                    log.error("【推送充电枪状态失败】Host：" + sysOperator.getHost() + "次数" + retryTime +" 数据：" + JSON.toJSONString(hlhtDto) + sysOperator.getOperatorName());
                    return;
                }
            });

        }
        catch (Exception ex){
            log.error("【推送充电枪状态失败】", ex);
            log.error("【推送充电枪状态失败】Host：" + sysOperator.getHost() + " 数据：" + JSON.toJSONString(hlhtDto) + sysOperator.getOperatorName());
        }
    }


}
