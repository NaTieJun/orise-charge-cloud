package org.dromara.omind.baseplat.mq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.service.notify.NotifyTradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2
@Component
public class ChargeOrderConsumer {

    @Autowired
    @Lazy
    NotifyTradeInfoService notifyTradeInfoService;

    @Bean
    Consumer<SysChargeOrder> chargeOrderData()
    {
        log.info("ChargeOrderConsumer-初始化订阅");
        return sysChargeOrder -> {
            log.info("ChargeOrderConsumer 消息接收成功=>" + JSON.toJSONString(sysChargeOrder));
            notifyTradeInfoService.send(sysChargeOrder);
        };
    }

}
