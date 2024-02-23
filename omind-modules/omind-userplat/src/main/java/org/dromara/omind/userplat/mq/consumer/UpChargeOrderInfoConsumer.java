package org.dromara.omind.userplat.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationChargeOrderInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.userplat.service.OmindBillService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class UpChargeOrderInfoConsumer {

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
    UpChargeOrderInfoConsumer selfService;

    @Bean
    Consumer<AsynNotificationChargeOrderInfoData> upChargeOrderInfo()
    {
        log.info("UpChargeOrderInfoConsumer-初始化订阅");
        return asynNotificationChargeOrderInfoData -> {
            log.info("UpChargeOrderInfoConsumer 消息接收成功=>" + JsonUtils.toJsonString(asynNotificationChargeOrderInfoData));
            selfService.dealWithData(asynNotificationChargeOrderInfoData);
        };
    }

    void dealWithData(AsynNotificationChargeOrderInfoData asynNotificationChargeOrderInfoData){
        long durantTm = System.currentTimeMillis();
        log.info("[rabbitmq] ChargeOrderInfoListener");
        NotificationChargeOrderInfoData notificationChargeOrderInfoData = asynNotificationChargeOrderInfoData.getChargeOrderInfoData();
        String operatorId = asynNotificationChargeOrderInfoData.getOperatorID();
        log.info("rabbitmq-chargeOrderInfo--notificationChargeOrderInfoData=" + notificationChargeOrderInfoData);
        String connectorId = notificationChargeOrderInfoData.getConnectorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if(odOperatorInfo != null) {
            try {
                OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
                OmindBillEntity odBillEntity = omindBillService.get(notificationChargeOrderInfoData.getStartChargeSeq());
                if(odBillEntity != null) {

                    try {
                        //处理数据业务逻辑
                        /**********************业务处理 开始************************/

                        //推送设备充电订单业务处理，上分布式同步锁
                        omindBillService.chargeOrderInfoDeal(notificationChargeOrderInfoData, odConnectorEntity, operatorId, odOperatorInfo.getPlatType());

                        /**********************业务处理 结束************************/

                    } catch (BaseException ube) {
                        log.error("ChargeOrderInfoListener-error", ube);
                    } catch (Exception e) {
                        log.error("ChargeOrderInfoListener-error", e);
                    }
                }else{
                    log.info("rabbitmq-chargeOrderInfo--【订单"+notificationChargeOrderInfoData.getStartChargeSeq()+"不存在】，【枪号】="+notificationChargeOrderInfoData.getConnectorID());
                }
            } catch (BaseException ube) {
                log.error("ChargeOrderInfoListener-error", ube);
            } catch (Exception e) {
                log.error("ChargeOrderInfoListener-error", e);
            }
        }

        log.info("rabbitmq-chargeOrderInfo--over durant=" + (System.currentTimeMillis() - durantTm) + "ms");
    }
}
