package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "订单强制停止请求")
@Data
public class BillForceStopRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电订单id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "结算电费", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal elecFee;

    @Schema(description = "结算服务费", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal serviceFee;

}
