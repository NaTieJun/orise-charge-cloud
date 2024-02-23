package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QueryStationStatsData", description = "查询统计信息")
public class QueryStationStatsData {

    @Schema(name = "StationID", description = "充电站ID")
    @JsonProperty(value = "StationID")
    private String stationID;

    @Schema(name = "StartTime", description = "统计开始时间，格式yyyy-MM-dd")
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "EndTime", description = "统计结束时间，格式yyyy-MM-dd")
    @JsonProperty(value = "EndTime")
    private String endTime;

}
