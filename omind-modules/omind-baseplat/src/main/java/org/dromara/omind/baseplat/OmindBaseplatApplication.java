package org.dromara.omind.baseplat;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@EnableDubbo
@EnableAsync
@ComponentScan("org.dromara.**")
@SpringBootApplication
public class OmindBaseplatApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
        SpringApplication application = new SpringApplication(OmindBaseplatApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  omind-baseplat 启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
