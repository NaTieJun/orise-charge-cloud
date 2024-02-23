package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QueryEquipBusinessPolicyData", description = "查询业务策略信息结果Data")
public class QueryEquipBusinessPolicyData {

    @Schema(name = "EquipBizSeq", description = "业务策略查询流水号")
    @JsonProperty(value = "EquipBizSeq")
    private String equipBizSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码")
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

}
