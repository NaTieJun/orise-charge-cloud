package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : flainsky
 * @version : V0.0
 * @data : 2025/3/8 16:03
 * @company : ucode
 * @email : 298542443@qq.com
 * @title :
 * @Description :
 */
@Data
@Schema(description = "支付宝解析用户手机号请求")
public class AliPhoneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "小程序应用id")
    private String appid;

    @Schema(description = "授权token")
    private String token;

    @Schema(description = "用户加密手机号数据")
    String encryptedData;

    @Schema(description = "unionId", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unionId;

    @Schema(description = "openId", requiredMode = Schema.RequiredMode.REQUIRED)
    private String openId;

}

