package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "设备接口状态查询Data")
public class QueryStationStatusData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StationIDs", description = "充电站ID列表")
    @JsonProperty(value = "StationIDs")
    private List<String> stationIDs;

}
