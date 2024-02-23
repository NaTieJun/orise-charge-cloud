package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(name = "ChargeDetailData", description = "充电明细信息体")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeDetailData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "DetailStartTime", description = "开始时间，格式“yyyy-MM-dd HH:mm:ss”", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "DetailStartTime")
    private String detailStartTime;

    @Schema(name = "DetailEndTime", description = "结束时间，格式“yyyy-MM-dd HH:mm:ss”", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "DetailEndTime")
    private String detailEndTime;

    @Schema(name = "ElecPrice", description = "时段电价", requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "ElecPrice")
    private BigDecimal elecPrice;

    @Schema(name = "SevicePrice", description = "时段服务费价格", requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "SevicePrice")
    private BigDecimal sevicePrice;

    @Schema(name = "DetailPower", description = "时段充电量", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "DetailPower")
    private BigDecimal detailPower;

    @Schema(name = "DetailElecMoney", description = "时段电费", requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "DetailElecMoney")
    private BigDecimal detailElecMoney;

    @Schema(name = "DetailSeviceMoney", description = "时段服务费", requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "DetailSeviceMoney")
    private BigDecimal detailSeviceMoney;
}
