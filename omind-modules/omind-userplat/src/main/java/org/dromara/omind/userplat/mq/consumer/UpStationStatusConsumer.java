package org.dromara.omind.userplat.mq.consumer;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.userplat.api.domain.datas.ConnectorStatusInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationStationStatusData;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.service.OmindBillService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Consumer;

@Component
@Slf4j
public class UpStationStatusConsumer {

    @Resource
    LockTemplate lockTemplate;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    @Autowired
    @Lazy
    UpStationStatusConsumer selfService;

    @Bean
    Consumer<NotificationStationStatusData> upStationStatus()
    {
        log.info("UpStationStatusConsumer-初始化订阅");
        return notificationStationStatusData -> {
            log.info("UpStationStatusConsumer 消息接收成功=>" + JsonUtils.toJsonString(notificationStationStatusData));
            selfService.dealWithData(notificationStationStatusData);
        };
    }

    void dealWithData(NotificationStationStatusData notificationStationStatusData){
        log.info("rabbitmq-connectorStatus--notificationStationStatusData=" + notificationStationStatusData);
        ConnectorStatusInfoData connectorStatusInfo = notificationStationStatusData.getConnectorStatusInfo();
        String connectorId = connectorStatusInfo.getConnectorID();
        //处理数据业务逻辑
        /**********************业务处理 开始************************/
        String lockKey = PlatRedisKey.NOTIFICATION_CONNECTOR_STATUS_LOCK + connectorId;
        LockInfo lockInfo = null;
        try {
            //推送设备充电状态业务处理，上分布式同步锁
            lockInfo = lockTemplate.lock(lockKey, 5000L, 5000L, RedissonLockExecutor.class);

            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
            if (odConnectorEntity != null) {

                OmindConnectorEntity updateConnectorDataObj = new OmindConnectorEntity();
                updateConnectorDataObj.setId(odConnectorEntity.getId());
                updateConnectorDataObj.setStatus(connectorStatusInfo.getStatus().shortValue());
                updateConnectorDataObj.setParkStatus(connectorStatusInfo.getParkStatus());
                updateConnectorDataObj.setLockStatus(connectorStatusInfo.getLockStatus());

                omindConnectorService.updateConnector(updateConnectorDataObj);
            }

        } catch (BaseException ube) {
            log.error("ConnectorStatusListener-error", ube);
        } catch (Exception e) {
            log.warn("message consume failed: " + e.getMessage());
            log.error("ConnectorStatusListener-error", e);
        } finally {
            //解锁
            if (lockInfo != null) {
                lockTemplate.releaseLock(lockInfo);
            }
        }

        /**********************业务处理 结束************************/
    }
}
