package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.StationStatsInfoData;

import java.io.Serializable;

@Schema(name = "QueryStationStatsResponseData", description = "查询统计信息 返回数据")
@Data
public class QueryStationStatsResponseData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name="StationStats",description = "充电站的汇总数据")
    @JsonProperty(value = "StationStats")
    StationStatsInfoData stationStats;

}
