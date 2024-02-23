package org.dromara.omind.userplat.api.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "小程序端请求停止充电返回数据")
@Data
public class StopChargeResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "操作结果，0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short succStat;

    @Schema(description = "失败原因 0无 1此设备不存在 2此设备离线 3设备已停止充电 4-99自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short failReason;
}
