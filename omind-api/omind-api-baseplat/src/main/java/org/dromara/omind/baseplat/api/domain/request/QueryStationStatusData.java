package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "QueryStationStatusData", description = "设备接口状态查询Data")
public class QueryStationStatusData {

    @Schema(name = "StationIDs", description = "充电站ID列表，数组长度不超过50")
    @JsonProperty(value = "StationIDs")
    private List<String> stationIDs;

}
