package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查询统计信息")
public class QueryStationStatsData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StationID", description = "充电站ID")
    @JsonProperty(value = "StationID")
    private String stationID;

    @Schema(name = "StartTime", description = "统计开始时间")
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "EndTime", description = "统计结束时间")
    @JsonProperty(value = "EndTime")
    private String endTime;

}
