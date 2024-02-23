package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "QueryEquipAuthData", description = "请求设备认证")
@Data
public class QueryEquipAuthData {

    @Schema(name = "EquipAuthSeq", description = "设备认证流水号")
    @JsonProperty(value = "EquipAuthSeq")
    private String equipAuthSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码")
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

}
