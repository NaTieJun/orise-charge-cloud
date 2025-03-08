package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : flainsky
 * @version : V0.0
 * @data : 2025/3/8 16:07
 * @company : ucode
 * @email : 298542443@qq.com
 * @title :
 * @Description :
 */
@Schema(description = "短信验证码登录请求")
@Data
public class MpMobileLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接收验证码手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobile;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String verCode;

}
