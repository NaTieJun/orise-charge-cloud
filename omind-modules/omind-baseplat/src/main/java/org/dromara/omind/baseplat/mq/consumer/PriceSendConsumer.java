package org.dromara.omind.baseplat.mq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.client.UTcpInnerV1Client;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Consumer;

@Log4j2
@Component
public class PriceSendConsumer {

    @Autowired
    SysPriceService priceService;

    @Autowired
    @Lazy
    PriceSendConsumer selfService;

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Resource
    UTcpInnerV1Client tcpInnerV1Client;

    @Bean
    Consumer<String> priceSender()
    {
        log.info("PriceSendConsumer-初始化订阅");
        return equipmentId -> {
            log.info("PriceSendConsumer 消息接收成功=>" + JSON.toJSONString(equipmentId));
            selfService.dealWithData(equipmentId);
        };
    }


    void dealWithData(String equipmentId)
    {
        log.info("【MQ下发价格】" + equipmentId);
        SysEquipment sysEquipment = equipmentService.getEquipmentById(equipmentId);
        if(sysEquipment == null){
            log.error("【MQ下发价格】未找到充电桩" + equipmentId);
            return;
        }
        //内网代码
        tcpInnerV1Client.sendPrice(sysEquipment.getServerIp(), sysEquipment.getEquipmentId());
//        tcpInnerV1Client.sendPrice(sysEquipment.getServerIp(), sysEquipment.getEquipmentId());
    }

}
