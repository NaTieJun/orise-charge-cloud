package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(name = "DisputeChargeOrderData", description = "单项争议交易信息体")
@Data
public class DisputeChargeOrderData {

    @Schema(name = "StartChargeSeq", description = "充电订单号",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "TotalPower", description = "累计充电量",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalPower")
    private BigDecimal totalPower;

    @Schema(name = "TotalMoney", description = "累计总金额",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalMoney")
    private BigDecimal totalMoney;

    @Schema(name = "DisputeReason", description = "争议原因，1:交易不存在;2：交易金额错误;3：交易电量错误;4～99：自定义",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "DisputeReason")
    private Short disputeReason;

}
