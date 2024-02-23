package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(name = "ChargeOrderData", description = "单项订单对账信息体")
@Data
public class ChargeOrderData {

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "TotalPower", description = "累计充电量",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalPower")
    private BigDecimal totalPower;

    @Schema(name = "TotalMoney", description = "累计总金额",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalMoney")
    private BigDecimal totalMoney;

}
