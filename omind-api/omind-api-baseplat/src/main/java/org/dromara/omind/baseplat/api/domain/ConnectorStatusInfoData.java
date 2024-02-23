package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(name = "ConnectorStatusInfoData", description = "充电设备接口信息")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectorStatusInfoData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "connectorID", description = "充电设备接口编码",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "connectorID")
    private String connectorID;

    @Schema(name = "Status", description = "充电设备接口状态 0离网，1空闲，2占用（未充电）3占用（充电中）4占用（预约锁定）255故障", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "Status")
    private Integer status;

    @Schema(name = "ParkStatus", description = "车位状态 0未知 10空闲 50占用",  requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "ParkStatus")
    private Short parkStatus;

    @Schema(name = "LockStatus", description = "地锁状态 0未知 10已解锁 50已上锁", requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "LockStatus")
    private Short lockStatus;

}
