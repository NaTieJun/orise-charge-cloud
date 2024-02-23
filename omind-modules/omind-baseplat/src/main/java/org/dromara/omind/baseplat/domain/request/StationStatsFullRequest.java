package org.dromara.omind.baseplat.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "充电站统计请求体")
@Data
public class StationStatsFullRequest {

    @Schema(name = "type", description = "统计方式 0日 1月 2年 3自定义")
    @JsonProperty(value = "type")
    private Short type;

    @Schema(name = "stationId", description = "充电站ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "stationId")
    private String stationId;

    @Schema(description = "开始日期/指定日期 yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTm;

    @Schema(description = "结束日期 yyyy-MM-dd", requiredMode = Schema.RequiredMode.AUTO)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTm;

    @Schema(description = "运营商ID")
    @JsonProperty(value = "operatorId")
    private String operatorId;

}
