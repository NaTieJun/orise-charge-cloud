package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : flainsky
 * @version : V0.0
 * @data : 2025/3/8 16:06
 * @company : ucode
 * @email : 298542443@qq.com
 * @title :
 * @Description :
 */
@Data
@Schema(description = "支付宝获取用户token请求")
public class AliAuthTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "前端小程序获得的code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "小程序应用id")
    private String appid;
}
