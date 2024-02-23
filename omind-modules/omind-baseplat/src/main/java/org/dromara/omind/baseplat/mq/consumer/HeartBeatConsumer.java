package org.dromara.omind.baseplat.mq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyConnectorStatusService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Log4j2
@Component
public class HeartBeatConsumer {

    @Autowired
    SysConnectorService connectorService;

    @Autowired
    RemoteNotifyConnectorStatusService remoteNotifyConnectorStatusService;

    @Autowired
    @Lazy
    HeartBeatConsumer selfService;

    @Bean
    Consumer<List<String>> heartBeat()
    {
        log.info("HeartBeatConsumer-初始化订阅");
        return connectorIdList -> {
            log.info("HeartBeatConsumer 消息接收成功=>" + JSON.toJSONString(connectorIdList));
            selfService.dealWithData(connectorIdList);
        };
    }

    void dealWithData(List<String> msgList){
        try {
            log.info("[rabbitmq] AioGunHeartBeatListener");
            List<SysConnector> cacheRefreshList = new ArrayList<>();
            List<SysConnector> cacheEnableList = new ArrayList<>();
            for(String msg : msgList) {
                String connectorId = msg;
                SysConnector sysConnector = connectorService.getConnectorById(connectorId);
                SysConnector upConnector = new SysConnector();
                upConnector.setConnectorId(sysConnector.getConnectorId());
                if(sysConnector == null){
                    continue;
                }
                upConnector.setId(sysConnector.getId());
                upConnector.setPingTm(new Date());
                sysConnector.setPingTm(upConnector.getPingTm());
                if (sysConnector.getStatus() == 0) {
                    upConnector.setStatus((short) 1);
                    sysConnector.setStatus((short) 1);
                    cacheRefreshList.add(sysConnector);
                    log.info("[修改枪状态01]" + upConnector.getConnectorId() + " status" + upConnector.getStatus());
                }
                else{
                    cacheEnableList.add(upConnector);
                }
            }
            log.info("[Qu_AioGunHeartBeat]" + msgList.size());
            //不需要更新缓存
            connectorService.updateBatchById(cacheEnableList, false);
            //需要更新缓存
            connectorService.updateBatchById(cacheRefreshList, true);
            for(SysConnector sysConnector : cacheRefreshList){
                //推送状态改变
                remoteNotifyConnectorStatusService.heartConnect(sysConnector);
            }
            cacheRefreshList.clear();
            cacheEnableList.clear();
        }
        catch (Exception e){
            log.warn("message consume failed: " + e.getMessage());
        }
    }

}
