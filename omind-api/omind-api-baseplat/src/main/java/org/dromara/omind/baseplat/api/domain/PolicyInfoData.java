package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(name = "PolicyInfoData", description = "单项业务策略信息体")
@Data
public class PolicyInfoData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartTime", description = "时段起始时间点HHmmss", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "ElecPrice", description = "时段电费，小数点后4位", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ElecPrice")
    private BigDecimal elecPrice;

    @Schema(name = "SevicePrice", description = "时段服务费，小数点后4位", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SevicePrice")
    private BigDecimal sevicePrice;

    @Schema(name = "PriceType", description = "电费类型 0尖1峰2平3谷", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "PriceType")
    private Short priceType;
}
