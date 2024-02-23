package org.dromara.omind.mq.api.producer;

import org.dromara.omind.mq.api.contants.TopicKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class StationPriceSendProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendMsg(String stationId)
    {
        streamBridge.send(TopicKeys.StationPriceSender, stationId);
    }

}
