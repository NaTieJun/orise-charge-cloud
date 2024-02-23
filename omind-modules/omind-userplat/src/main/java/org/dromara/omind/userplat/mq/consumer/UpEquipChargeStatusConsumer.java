package org.dromara.omind.userplat.mq.consumer;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationEquipChargeStatusData;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationEquipChargeStatusData;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.service.OmindBillService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Consumer;

@Component
@Slf4j
public class UpEquipChargeStatusConsumer {

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
    OmindOperatorService omindOperatorService;

    @Autowired
    @Lazy
    UpEquipChargeStatusConsumer selfService;

    @Bean
    Consumer<AsynNotificationEquipChargeStatusData> upEquipChargeStatus()
    {
        log.info("UpEquipChargeStatusConsumer-初始化订阅");
        return asynNotificationEquipChargeStatusData -> {
            log.info("UpEquipChargeStatusConsumer 消息接收成功=>" + JsonUtils.toJsonString(asynNotificationEquipChargeStatusData));
            selfService.dealWithData(asynNotificationEquipChargeStatusData);
        };
    }

    void dealWithData(AsynNotificationEquipChargeStatusData asynNotificationEquipChargeStatusData){
        long durantStart = System.currentTimeMillis();
        log.info("[rabbitmq] EqiupChargeStatusListener");
        NotificationEquipChargeStatusData notificationEquipChargeStatusData = asynNotificationEquipChargeStatusData.getEquipChargeStatusData();
        log.info("rabbitmq-equipChargeStatus--notificationEquipChargeStatusData=" + notificationEquipChargeStatusData);
        String operatorId = asynNotificationEquipChargeStatusData.getOperatorID();
        String startChargeSeq = notificationEquipChargeStatusData.getStartChargeSeq();
        String connectorId = notificationEquipChargeStatusData.getConnectorID();

        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if(odOperatorInfo != null) {
            String lockKey = PlatRedisKey.NOTIFICATION_CHARGE_STATUS_LOCK + startChargeSeq;
            LockInfo lockInfo = null;
            try {
                //推送设备充电状态业务处理，上分布式同步锁
                lockInfo = lockTemplate.lock(lockKey, 5000L, 5000L, RedissonLockExecutor.class);
                OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
                omindBillService.equipChargeStatusDeal(notificationEquipChargeStatusData, odConnectorEntity, operatorId, odOperatorInfo.getPlatType());

            } catch (BaseException ube) {
                log.error("EquipChargeStatusListener-error", ube);
            } catch (Exception e) {
                log.error("EquipChargeStatusListener-error", e);
            } finally {
                //解锁
                if (lockInfo != null) {
                    lockTemplate.releaseLock(lockInfo);
                }
            }
        }
        log.info("rabbitmq-equipChargeStatus--over  time-use:" + (System.currentTimeMillis() - durantStart) + "ms");
    }
}
