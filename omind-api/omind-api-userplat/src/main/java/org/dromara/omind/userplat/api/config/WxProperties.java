package org.dromara.omind.userplat.api.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class WxProperties {

    /**
     * 设置小程序appid
     */
    @Value("${wx.miniapp.appid}")
    private String appId;

    /**
     * 设置小程序Secret
     */
    @Value("${wx.miniapp.secret}")
    private String secret;

    /**
     * 设置小程序消息服务token
     */
    @Value("${wx.miniapp.token}")
    private String token;

    /**
     * 设置小程序消息服务EncodingAESKey
     */
    @Value("${wx.miniapp.aesKey}")
    private String aesKey;

    /**
     * 消息格式，XML或者JSON
     */
    @Value("${wx.miniapp.msgDataFormat}")
    private String msgDataFormat;

    /**
     * 商户号
     */
    @Value("${wx.pay.mchId}")
    private String mchId;

    /**
     * 支付商户密钥
     */
    @Value("${wx.pay.mchKey}")
    private String mchKey;

    /**
     * apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    @Value("${wx.pay.keyPath}")
    private String keyPath;

    /**
     * 支付是否支持沙河环境
     */
    @Value("${wx.pay.isSandbox}")
    private Boolean isSandbox;

    @Value("${wx.pay.payCallback}")
    private String payCallback;

}
