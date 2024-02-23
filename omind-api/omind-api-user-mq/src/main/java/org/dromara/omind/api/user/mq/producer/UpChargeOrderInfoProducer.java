package org.dromara.omind.api.user.mq.producer;

import org.dromara.omind.api.user.mq.constants.TopicKeys;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationChargeOrderInfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class UpChargeOrderInfoProducer {

    @Autowired
    StreamBridge streamBridge;

    public void sendMsg(AsynNotificationChargeOrderInfoData asynNotificationChargeOrderInfoData){
        streamBridge.send(TopicKeys.PileUpChargeOrderInfo, MessageBuilder.withPayload(asynNotificationChargeOrderInfoData).build());
    }
}
