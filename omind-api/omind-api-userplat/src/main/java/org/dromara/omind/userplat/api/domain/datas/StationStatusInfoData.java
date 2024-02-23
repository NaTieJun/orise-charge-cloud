package org.dromara.omind.userplat.api.domain.datas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "充电站状态")
@Data
public class StationStatusInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "stationID", description = "充电站ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "stationID")
    private String stationID;

    @Schema(name = "ConnectorStatusInfos", description = "充电设备接口状态列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorStatusInfos")
    private List<ConnectorStatusInfoData> connectorStatusInfos;

}
