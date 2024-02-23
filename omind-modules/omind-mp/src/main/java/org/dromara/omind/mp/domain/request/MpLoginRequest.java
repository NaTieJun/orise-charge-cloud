package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "小程序code登录接口")
@Data
public class MpLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "code", name = "微信给的临时code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

}
