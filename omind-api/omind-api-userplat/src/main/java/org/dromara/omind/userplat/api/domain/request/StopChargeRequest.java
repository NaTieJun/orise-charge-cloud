package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "停止充电请求")
@Data
public class StopChargeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

    @Schema(description = "充电设备接口编码")
    private String connectorID;
}