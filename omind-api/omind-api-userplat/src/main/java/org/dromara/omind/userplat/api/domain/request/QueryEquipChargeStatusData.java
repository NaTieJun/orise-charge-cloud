package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "查询充电状态Data")
@Data
public class QueryEquipChargeStatusData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartChargeSeq", description = "充电订单号")
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

}
