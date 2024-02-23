package org.dromara.omind.api.user.mq.producer;

import org.dromara.omind.api.user.mq.constants.TopicKeys;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationEquipChargeStatusData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class UpEquipChargeStatusProducer {

    @Autowired
    StreamBridge streamBridge;

    public void sendMsg(AsynNotificationEquipChargeStatusData asynNotificationEquipChargeStatusData){
        streamBridge.send(TopicKeys.PileUpEquipChargeStatus, MessageBuilder.withPayload(asynNotificationEquipChargeStatusData).build());
    }
}
