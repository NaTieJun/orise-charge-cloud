package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "QueryEquipChargeStatusData", description = "查询充电状态Data")
@Data
public class QueryEquipChargeStatusData {

    @Schema(name = "StartChargeSeq", description = "充电订单号")
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

}
