package org.dromara.omind.userplat.api.domain.datas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "单项业务策略信息体")
@Data
public class PolicyInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartTime", description = "时段起始时间点", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "ElecPrice", description = "时段电费", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ElecPrice")
    private BigDecimal elecPrice;

    @Schema(name = "ServicePrice", description = "时段服务费", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ServicePrice")
    private BigDecimal servicePrice;

    @Schema(name = "PriceType", description = "电费类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "PriceType")
    private Short priceType;

}
