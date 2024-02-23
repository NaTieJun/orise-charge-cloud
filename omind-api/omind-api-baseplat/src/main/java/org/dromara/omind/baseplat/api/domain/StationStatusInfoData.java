package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "StationStatusInfoData", description = "充电站状态")
@Data
public class StationStatusInfoData {

    @Schema(name = "stationID", description = "充电站ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "stationID")
    private String stationID;

    @Schema(name = "ConnectorStatusInfos", description = "充电设备接口状态列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorStatusInfos")
    private List<ConnectorStatusInfoData> connectorStatusInfos;

}
