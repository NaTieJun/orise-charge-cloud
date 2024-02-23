package org.dromara.omind.mq.api.producer;

import org.dromara.omind.mq.api.contants.TopicKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class HeartBeatProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendMsg(String connectorId){
        streamBridge.send(TopicKeys.PileHeartBeat, connectorId);
    }

}
